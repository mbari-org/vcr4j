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
import org.mbari.vcr4j.remote.control.RVideoIO;

import static org.junit.Assert.*;

public class RequestAllVideoInfosCmdTest {

    @Test
    public void successWithNonEmptyVideos() {
        var json = "{\"response\":\"request all information\",\"status\":\"ok\","
                + "\"videos\":[{\"uuid\":\"29f056c7-8d18-4880-bc26-62e9012f98b3\","
                + "\"url\":\"http://example/x.mp4\",\"durationMillis\":1000,\"frameRate\":30}]}";
        var response = RVideoIO.GSON.fromJson(json, RequestAllVideoInfosCmd.Response.class);
        assertTrue(response.success());
        assertEquals(1, response.getVideos().size());
    }

    @Test
    public void successWithEmptyVideos() {
        var json = "{\"response\":\"request all information\",\"status\":\"ok\",\"videos\":[]}";
        var response = RVideoIO.GSON.fromJson(json, RequestAllVideoInfosCmd.Response.class);
        assertTrue(response.success());
        assertTrue(response.getVideos().isEmpty());
    }

    @Test
    public void missingVideosFieldIsNotSuccess() {
        // GSON bypasses the constructor, so a wire response that omits "videos" leaves
        // the field null — success() must report false to protect callers from NPE
        // when calling getVideos().stream().
        var json = "{\"response\":\"request all information\",\"status\":\"ok\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestAllVideoInfosCmd.Response.class);
        assertNull(response.getVideos());
        assertFalse(response.success());
    }

    @Test
    public void failedStatusIsNotSuccess() {
        var json = "{\"response\":\"request all information\",\"status\":\"failed\",\"videos\":[]}";
        var response = RVideoIO.GSON.fromJson(json, RequestAllVideoInfosCmd.Response.class);
        assertFalse(response.isOk());
        assertFalse(response.success());
    }

    @Test
    public void serverSideResponseCarriesOkStatus() {
        // Sanity check: the server-side constructor now sets status:"ok", so a round-
        // tripped response should be recognized as successful by the client.
        var response = new RequestAllVideoInfosCmd.Response(java.util.List.of());
        var json = RVideoIO.GSON.toJson(response);
        var parsed = RVideoIO.GSON.fromJson(json, RequestAllVideoInfosCmd.Response.class);
        assertTrue(parsed.isOk());
        assertTrue(parsed.success());
    }
}
