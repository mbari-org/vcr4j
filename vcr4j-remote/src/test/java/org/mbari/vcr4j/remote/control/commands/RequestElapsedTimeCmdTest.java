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
