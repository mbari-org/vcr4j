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
 * This is normally sent from the video player to the {{@link org.mbari.vcr4j.remote.control.RemoteControl}}
 * once the video player has completed a framecapture.
 */
public class FrameCaptureDoneCmd extends RCommand<FrameCaptureDoneCmd.Request, FrameCaptureDoneCmd.Response> {

    public static final String COMMAND = "frame capture done";

    public FrameCaptureDoneCmd(Request value) {
        super(value);
    }

    public FrameCaptureDoneCmd(UUID uuid,
                               UUID imageReferenceUuid,
                               String imageLocation,
                               Long elapsedTimeMilllis,
                               String status) {
        super(new Request(uuid, imageReferenceUuid, imageLocation, elapsedTimeMilllis, status));
    }

    public static FrameCaptureDoneCmd fail(FrameCapture fc) {
        return new FrameCaptureDoneCmd(fc.getUuid(),
                fc.getImageReferenceUuid(),
                fc.getImageLocation(),
                fc.getElapsedTimeMillis(),
                RResponse.FAILED);
    }

    public static FrameCaptureDoneCmd success(FrameCapture fc) {
        return new FrameCaptureDoneCmd(fc.getUuid(),
                fc.getImageReferenceUuid(),
                fc.getImageLocation(),
                fc.getElapsedTimeMillis(),
                RResponse.OK);
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest implements FrameCapture {

        private Long elapsedTimeMillis;
        private String imageLocation;
        private UUID imageReferenceUuid;

        private String status;

        public Request(UUID uuid,
                       UUID imageReferenceUuid,
                       String imageLocation,
                       Long elapsedTimeMillis,
                       String status) {
            super(COMMAND, uuid);
            this.imageReferenceUuid = imageReferenceUuid;
            this.imageLocation = imageLocation;
            this.elapsedTimeMillis = elapsedTimeMillis;
            this.status = status;
        }

        public String getImageLocation() {
            return imageLocation;
        }

        public UUID getImageReferenceUuid() {
            return imageReferenceUuid;
        }

        public Long getElapsedTimeMillis() {
            return elapsedTimeMillis;
        }

        public String getStatus() {
            return status;
        }
    }


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
