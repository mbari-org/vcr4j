package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.UUID;

public interface VideoInfo {

    UUID getUuid();
    URL getUrl();
    Long getDurationMillis();
    Double getFrameRate();

}
