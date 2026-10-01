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
 * Sent by the video player to the remote control, after the player has responded to an
 * {@link OpenCmd}, to report whether the video was actually opened.
 */
public class OpenDoneCmd extends RCommand<OpenDoneCmd.Request, OpenDoneCmd.Response> {

    public static final String COMMAND = "open done";

    public OpenDoneCmd(Request value) {
        super(value);
    }

    public OpenDoneCmd(UUID uuid, String status, String cause) {
        this(new Request(uuid, status, cause));
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest {

        private String status;
        private String cause;

        public Request(UUID uuid, String status) {
            this(uuid, status, null);
        }

        public Request(UUID uuid, String status, String cause) {
            super(COMMAND, uuid);
            this.status = status;
            this.cause = cause;
        }

        public String getStatus() {
            return status;
        }

        public String getCause() {
            return cause;
        }

        public boolean isOk() {
            return status != null && status.equalsIgnoreCase(RResponse.OK);
        }
    }

    public static class Response extends RResponse {
        public Response() {
            super(COMMAND, RResponse.OK);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }
}
