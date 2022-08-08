package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2022-08-08
 */
public interface VideoInfo {

    UUID getUuid();
    URL getUrl();
    Long getDurationMillis();
    Double getFrameRate();

}
