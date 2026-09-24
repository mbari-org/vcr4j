/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.remote.control;

import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.remote.control.commands.RequestAllVideoInfosCmd;
import org.mbari.vcr4j.remote.control.commands.RequestElapsedTimeCmd;
import org.mbari.vcr4j.remote.control.commands.RequestPlayerStateCmd;
import org.mbari.vcr4j.remote.control.commands.RequestVideoInfoCmd;
import org.mbari.vcr4j.remote.control.commands.VideoInfo;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class RResponseParserTest {

    private PublishSubject<RState> stateSubject;
    private PublishSubject<RError> errorSubject;
    private PublishSubject<VideoIndex> indexSubject;
    private PublishSubject<List<? extends VideoInfo>> videoInfoSubject;
    private RResponseParser parser;

    private TestObserver<RState> stateObs;
    private TestObserver<RError> errorObs;
    private TestObserver<VideoIndex> indexObs;
    private TestObserver<List<? extends VideoInfo>> videoInfoObs;

    @Before
    public void setUp() {
        stateSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
        indexSubject = PublishSubject.create();
        videoInfoSubject = PublishSubject.create();
        parser = new RResponseParser(UUID.randomUUID(),
                stateSubject, errorSubject, indexSubject, videoInfoSubject);

        stateObs = stateSubject.test();
        errorObs = errorSubject.test();
        indexObs = indexSubject.test();
        videoInfoObs = videoInfoSubject.test();
    }

    @Test
    public void playerStateResponseDispatchesToStateSubject() {
        var cmd = new RequestPlayerStateCmd(UUID.randomUUID());
        var json = "{\"response\":\"request player state\",\"state\":\"playing\",\"status\":\"ok\",\"rate\":1.0}";

        var result = parser.handle(cmd, json);

        assertTrue(result.isPresent());
        stateObs.assertValueCount(1);
        assertEquals(RState.State.PLAYING, stateObs.values().get(0).getState());
        errorObs.assertValueCount(0);
    }

    @Test
    public void elapsedTimeResponseDispatchesToIndexSubject() {
        var cmd = new RequestElapsedTimeCmd(UUID.randomUUID());
        var json = "{\"response\":\"request elapsed time\",\"status\":\"ok\",\"elapsedTimeMillis\":12345}";

        var result = parser.handle(cmd, json);

        assertTrue(result.isPresent());
        indexObs.assertValueCount(1);
        assertEquals(12345L, indexObs.values().get(0).getElapsedTime().get().toMillis());
        errorObs.assertValueCount(0);
    }

    @Test
    public void videoInfoResponseDispatchesToVideoInfoSubject() {
        var cmd = new RequestVideoInfoCmd();
        var json = "{\"response\":\"request information\",\"status\":\"ok\","
                + "\"uuid\":\"29f056c7-8d18-4880-bc26-62e9012f98b3\","
                + "\"url\":\"http://example/x.mp4\",\"durationMillis\":1000,\"frameRate\":30.0}";

        var result = parser.handle(cmd, json);

        assertTrue(result.isPresent());
        videoInfoObs.assertValueCount(1);
        assertEquals(1, videoInfoObs.values().get(0).size());
        errorObs.assertValueCount(0);
    }

    @Test
    public void allVideoInfosResponseDispatchesWhenSuccessful() {
        var cmd = new RequestAllVideoInfosCmd();
        var json = "{\"response\":\"request all information\",\"status\":\"ok\","
                + "\"videos\":[{\"uuid\":\"29f056c7-8d18-4880-bc26-62e9012f98b3\","
                + "\"url\":\"http://example/x.mp4\",\"durationMillis\":1000,\"frameRate\":30}]}";

        var result = parser.handle(cmd, json);

        assertTrue(result.isPresent());
        videoInfoObs.assertValueCount(1);
        assertEquals(1, videoInfoObs.values().get(0).size());
        errorObs.assertValueCount(0);
    }

    @Test
    public void allVideoInfosResponseWithoutVideosFieldIsGuarded() {
        // The parser guards videoInfoSubject.onNext against a null videos list —
        // downstream subscribers must not receive null.
        var cmd = new RequestAllVideoInfosCmd();
        var json = "{\"response\":\"request all information\",\"status\":\"ok\"}";

        var result = parser.handle(cmd, json);

        assertTrue(result.isPresent());
        videoInfoObs.assertValueCount(0);
        // success() is false → parse() also emits an "unsuccessful" RError.
        errorObs.assertValueCount(1);
    }

    @Test
    public void unsuccessfulResponseEmitsError() {
        // status:"failed" makes RequestPlayerStateCmd.Response.success() false.
        var cmd = new RequestPlayerStateCmd(UUID.randomUUID());
        var json = "{\"response\":\"request player state\",\"state\":\"playing\",\"status\":\"failed\"}";

        var result = parser.handle(cmd, json);

        assertTrue(result.isPresent());
        errorObs.assertValueCount(1);
        var err = errorObs.values().get(0);
        assertFalse(err.isConnectionError());
        assertFalse(err.isParseError());
        assertTrue(err.hasError());
        // Dispatch still happens for parsed responses regardless of success — state
        // subject sees "playing" as reported on the wire.
        stateObs.assertValueCount(1);
    }

    @Test
    public void malformedJsonEmitsParseErrorAndReturnsEmpty() {
        var cmd = new RequestPlayerStateCmd(UUID.randomUUID());
        var json = "{not-valid-json";

        var result = parser.handle(cmd, json);

        assertFalse(result.isPresent());
        errorObs.assertValueCount(1);
        var err = errorObs.values().get(0);
        assertTrue(err.isParseError());
        assertTrue(err.getException().isPresent());
        stateObs.assertValueCount(0);
    }

    @Test
    public void elapsedTimeResponseWithoutFieldDoesNotEmitIndex() {
        // getVideoIndex() returns empty when elapsedTimeMillis is null — the parser's
        // ifPresent guard should keep the index subject silent.
        var cmd = new RequestElapsedTimeCmd(UUID.randomUUID());
        var json = "{\"response\":\"request elapsed time\",\"status\":\"failed\"}";

        parser.handle(cmd, json);

        indexObs.assertValueCount(0);
        // success() is false → parse() emits the "unsuccessful" RError.
        errorObs.assertValueCount(1);
    }
}
