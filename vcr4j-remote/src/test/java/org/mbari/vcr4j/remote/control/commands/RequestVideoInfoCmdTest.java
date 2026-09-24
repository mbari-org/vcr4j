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

public class RequestVideoInfoCmdTest {

    @Test
    public void testJsonParsing() {
        var json = "{\"durationMillis\":899431,\"frameRate\":29.970027923583984,\"isKey\":false,\"response\":\"request information\",\"status\":\"ok\",\"url\":\"http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T210458.233Z_t4s4_1280_tc03560915_h264.mp4\",\"uuid\":\"29f056c7-8d18-4880-bc26-62e9012f98b3\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestVideoInfoCmd.Response.class);
        assertNotNull(response);
        assertTrue(response.isOk());
        assertTrue(response.success());
    }

    @Test
    public void failedStatusIsNotSuccess() {
        var json = "{\"durationMillis\":899431,\"frameRate\":29.97,\"response\":\"request information\",\"status\":\"failed\",\"url\":\"http://example/x.mp4\",\"uuid\":\"29f056c7-8d18-4880-bc26-62e9012f98b3\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestVideoInfoCmd.Response.class);
        assertFalse(response.isOk());
        assertFalse(response.success());
    }

    @Test
    public void missingFieldsIsNotSuccess() {
        var json = "{\"response\":\"request information\",\"status\":\"ok\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestVideoInfoCmd.Response.class);
        assertTrue(response.isOk());
        assertFalse(response.success());
    }

    @Test
    public void serverSideResponseCarriesOkStatus() {
        // Sanity check: a server-constructed successful Response should serialize with
        // status:"ok" so a client sees isOk() as true after round-trip.
        var response = new RequestVideoInfoCmd.Response(
                java.util.UUID.randomUUID(), null, 1000L, 30.0);
        var json = RVideoIO.GSON.toJson(response);
        var parsed = RVideoIO.GSON.fromJson(json, RequestVideoInfoCmd.Response.class);
        assertTrue(parsed.isOk());
        assertTrue(parsed.success());
    }
}
