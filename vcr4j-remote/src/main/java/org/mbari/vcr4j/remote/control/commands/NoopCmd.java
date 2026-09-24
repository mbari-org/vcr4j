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

import java.util.UUID;

/**
 * Does nothing. Some commands not used by {{@link org.mbari.vcr4j.remote.control.RVideoIO}} are
 * mapped to this.
 */
public class NoopCmd extends RCommand<NoopCmd.Request, NoopCmd.Response> {

    public static final String COMMAND = "noop";

    public NoopCmd() {
        super(new Request(COMMAND, new UUID(0L, 0L)));
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    @Override
    public String getName() {
        return COMMAND;
    }

    public static class Request extends RRequest {
        public Request(String command, UUID uuid) {
            super(command, uuid);
        }
    }

    public static class Response extends RResponse {

        public Response(String response, String status) {
            super(response, status);
        }


        public Response(String response, String status, String cause) {
            super(response, status, cause);
        }

        @Override
        public boolean success() {
            return true;
        }

    }
}
