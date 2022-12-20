package org.mbari.vcr4j.remote.control.commands;

import org.junit.Test;
import org.mbari.vcr4j.remote.control.RVideoIO;
import static org.junit.Assert.*;

public class RequestElapsedTimeCmdTest {

    @Test
    public void testJsonParsing() {
        var json = "{\"elapsedTimeMillis\":692769,\"response\":\"request elapsed time\",\"status\":\"ok\"}";
        var response = RVideoIO.GSON.fromJson(json, RequestElapsedTimeCmd.Response.class);
        assertNotNull(response);
        assertNotNull(response.getElapsedTimeMillis());
        assertEquals(692769L, response.getElapsedTimeMillis().longValue());
    }
}
