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
 * Initiate a frame capture
 */
public class FrameCaptureCmd extends RCommand<FrameCaptureCmd.Request, FrameCaptureCmd.Response> {

    public static final String COMMAND = "frame capture";

    public FrameCaptureCmd(Request value) {
        super(value);
    }

    public FrameCaptureCmd(UUID uuid, UUID imageReferenceUuid, String imageLocation) {
        this(new Request(uuid, imageReferenceUuid, imageLocation));
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest implements FrameCapture {

        private String imageLocation;
        private UUID imageReferenceUuid;

        public Request(UUID uuid, UUID imageReferenceUuid, String imageLocation) {
            super(COMMAND, uuid);
            this.imageReferenceUuid = imageReferenceUuid;
            this.imageLocation = imageLocation;
        }

        public String getImageLocation() {
            return imageLocation;
        }

        public UUID getImageReferenceUuid() {
            return imageReferenceUuid;
        }

        public Long getElapsedTimeMillis() {
            return null;
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
}
