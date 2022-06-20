package org.mbari.vcr4j.remote.control.commands;

import java.util.UUID;

public interface FrameCapture {

    UUID getUuid();

    String getImageLocation();

    UUID getImageReferenceUuid();

    Long getElapsedTimeMillis();

}
