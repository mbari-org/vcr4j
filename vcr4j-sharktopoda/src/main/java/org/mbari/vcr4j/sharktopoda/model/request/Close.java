package org.mbari.vcr4j.sharktopoda.model.request;

import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-29T15:32:00
 */
public class Close {

    private final String command = "close";
    private UUID uuid;

    public Close(UUID uuid) {
        this.uuid = uuid;
    }
}
