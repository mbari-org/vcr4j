package org.mbari.vcr4j.sharktopoda.model.request;

import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T11:11:00
 */
public class Open {
    private final String command = "open";
    private URL url;
    private UUID uuid;

    public Open(URL url, UUID uuid) {
        this.url = url;
        this.uuid = uuid;
    }


}
