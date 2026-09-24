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
package org.mbari.vcr4j.remote.control.commands;

import org.junit.Test;
import static org.junit.Assert.*;

import org.mbari.vcr4j.remote.control.RState;
import org.mbari.vcr4j.remote.control.RVideoIO;

public class RequestPlayerStateCmdTest {

    @Test
    public void testJsonParsing() {
        var json = "{\"elapsedTimeMillis\":3894,\"rate\":1,\"response\":\"request player state\",\"state\":\"playing\",\"status\":\"ok\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestPlayerStateCmd.Response.class);
        assertNotNull(response);
        assertEquals(response.state().getState(), RState.State.PLAYING);
        assertEquals(3894L, response.getElapsedTimeMillis().longValue());
        assertTrue(response.isOk());
        assertTrue(response.success());
    }

    @Test
    public void notFoundIsNotSuccess() {
        // "not found" is a valid response meaning no video is loaded — callers using
        // success() to gate "video is accessible" must see false here.
        var json = "{\"response\":\"request player state\",\"state\":\"not found\",\"status\":\"ok\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestPlayerStateCmd.Response.class);
        assertTrue(response.isOk());
        assertEquals(RState.State.NOT_FOUND, response.state().getState());
        assertFalse(response.success());
    }

    @Test
    public void unknownStateIsNotSuccess() {
        var json = "{\"response\":\"request player state\",\"state\":\"garbled\",\"status\":\"ok\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestPlayerStateCmd.Response.class);
        assertFalse(response.success());
    }

    @Test
    public void failedStatusIsNotSuccess() {
        var json = "{\"response\":\"request player state\",\"state\":\"playing\",\"status\":\"failed\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestPlayerStateCmd.Response.class);
        assertFalse(response.isOk());
        assertFalse(response.success());
    }

    @Test
    public void missingStateIsNotSuccess() {
        var json = "{\"response\":\"request player state\",\"status\":\"ok\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestPlayerStateCmd.Response.class);
        assertNull(response.getState());
        assertFalse(response.success());
    }
}
