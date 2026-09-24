package org.mbari.vcr4j.remote.control.commands;

/*-
 * #%L
 * vcr4j-remote
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
import static org.junit.Assert.*;

import org.mbari.vcr4j.remote.control.RVideoIO;

import java.util.UUID;

public class OpenDoneCmdTest {

    @Test
    public void testRequestJsonParsingWithCause() {
        var uuid = UUID.randomUUID();
        var json = "{\"command\":\"open done\",\"uuid\":\"" + uuid + "\","
                + "\"status\":\"failed\",\"cause\":\"file not found\"}";
        var request = RVideoIO.GSON.fromJson(json, OpenDoneCmd.Request.class);
        assertNotNull(request);
        assertEquals(OpenDoneCmd.COMMAND, request.getCommand());
        assertEquals(uuid, request.getUuid());
        assertEquals("failed", request.getStatus());
        assertEquals("file not found", request.getCause());
        assertFalse(request.isOk());
    }

    @Test
    public void testRequestJsonParsingOkWithoutCause() {
        var uuid = UUID.randomUUID();
        var json = "{\"command\":\"open done\",\"uuid\":\"" + uuid + "\","
                + "\"status\":\"ok\"}";
        var request = RVideoIO.GSON.fromJson(json, OpenDoneCmd.Request.class);
        assertNotNull(request);
        assertEquals(OpenDoneCmd.COMMAND, request.getCommand());
        assertEquals(uuid, request.getUuid());
        assertEquals("ok", request.getStatus());
        assertNull(request.getCause());
        assertTrue(request.isOk());
    }
}
