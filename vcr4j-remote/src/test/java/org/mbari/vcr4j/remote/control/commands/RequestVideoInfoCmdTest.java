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
}
