package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

/**
 * Defines teh parameters that should be returned when a framecapture has been completed.
 */
public interface FrameCapture {

    /**
     *
     * @return The uuid of the video
     */
    UUID getUuid();

    /**
     *
     * @return Where the image was saved.
     */
    String getImageLocation();

    /**
     *
     * @return The uuid of the image
     */
    UUID getImageReferenceUuid();

    /**
     *
     * @return The elapsed time (as millis) into the video when the frame capture was taken
     */
    Long getElapsedTimeMillis();

}
