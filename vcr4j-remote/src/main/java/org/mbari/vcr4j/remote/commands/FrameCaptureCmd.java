package org.mbari.vcr4j.remote.commands;

import java.util.UUID;

public class FrameCaptureCmd extends RCommand<FrameCaptureCmd.Request, FrameCaptureCmd.Response> {

    public static final String Command = "frame capture";

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

    public static class Request extends RRequest {

        private String imageLocation;
        private UUID imageReferenceUuid;

        public Request(UUID uuid, UUID imageReferenceUuid, String imageLocation) {
            super(Command, uuid);
            this.imageReferenceUuid = imageReferenceUuid;
            this.imageLocation = imageLocation;
        }

        public String getImageLocation() {
            return imageLocation;
        }

        public UUID getImageReferenceUuid() {
            return imageReferenceUuid;
        }
    }

    // Ack
    public static class Response extends RResponse {
        private Long elapsedTimeMillis;
        private UUID imageReferenceUuid;
        private String imageLocation;

        public Response(String status,
                        Long elapsedTimeMillis,
                        UUID imageReferenceUuid,
                        String imageLocation) {
            super(Command, status);
            this.elapsedTimeMillis = elapsedTimeMillis;
            this.imageReferenceUuid = imageReferenceUuid;
            this.imageLocation = imageLocation;
        }

        public Long getElapsedTimeMillis() {
            return elapsedTimeMillis;
        }

        public UUID getImageReferenceUuid() {
            return imageReferenceUuid;
        }

        public String getImageLocation() {
            return imageLocation;
        }

        @Override
        public boolean success() {
            return isAck() || (isOk() && elapsedTimeMillis != null);
        }
    }
}
