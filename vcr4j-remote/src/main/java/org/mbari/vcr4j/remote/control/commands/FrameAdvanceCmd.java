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

/**
 * Advance a single frame
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class FrameAdvanceCmd extends RCommand<FrameAdvanceCmd.Request, FrameAdvanceCmd.Response> {

    public static final String COMMAND = "frame advance";

    public FrameAdvanceCmd(FrameAdvanceCmd.Request value) {
        super(value);
    }

    public FrameAdvanceCmd(UUID uuid) {
        this(new Request(uuid));
    }

    public FrameAdvanceCmd(UUID uuid, boolean forward) {
        this(new Request(uuid, forward));
    }

    public static class Request extends RRequest {

        private Integer direction;

        public Request(UUID uuid) {
            this(uuid, true);
        }

        public Request(UUID uuid, boolean forward) {
            super(COMMAND, uuid);
            direction = forward ? 1 : -1;
        }
    }

    // Ack
    public static class Response extends RResponse {
        public Response(String status) {
            super(COMMAND, status);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
