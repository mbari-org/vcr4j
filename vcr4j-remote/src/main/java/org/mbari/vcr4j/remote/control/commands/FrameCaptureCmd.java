package org.mbari.vcr4j.remote.control.commands;

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
