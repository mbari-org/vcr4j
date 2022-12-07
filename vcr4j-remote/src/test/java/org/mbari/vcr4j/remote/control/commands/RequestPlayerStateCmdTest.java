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
}
