package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

public class FrameCaptureDoneCmd extends RCommand<FrameCaptureDoneCmd.Request, FrameCaptureDoneCmd.Response> {

    public static final String Command = "frame capture done";

    public FrameCaptureDoneCmd(Request value) {
        super(value);
    }

    public FrameCaptureDoneCmd(UUID uuid,
                               UUID imageReferenceUuid,
                               String imageLocation,
                               Long elapsedTimeMilllis) {
        super(new Request(uuid, imageReferenceUuid, imageLocation, elapsedTimeMilllis));
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest {

        private Long elapsedTimeMillis;
        private String imageLocation;
        private UUID imageReferenceUuid;

        public Request(UUID uuid,
                       UUID imageReferenceUuid,
                       String imageLocation,
                       Long elapsedTimeMillis) {
            super(Command, uuid);
            this.imageReferenceUuid = imageReferenceUuid;
            this.imageLocation = imageLocation;
            this.elapsedTimeMillis = elapsedTimeMillis;
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
    }


    public static class Response extends RResponse {
        public Response(String status) {
            super(Command, status);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }
}
