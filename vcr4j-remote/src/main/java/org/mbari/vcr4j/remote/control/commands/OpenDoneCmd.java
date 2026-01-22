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

import java.util.UUID;

public class OpenDoneCmd {

    public static final String COMMAND = "open done";

    public static class Request extends RRequest {

        private String status;

        public Request(UUID uuid, String status) {
            super(COMMAND, uuid);
            this.status = status;
        }

        public String getStatus() {
            return status;
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
