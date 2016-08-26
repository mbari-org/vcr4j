package org.mbari.vcr4j.sharktopoda.model.response;

import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:25:00
 */
public class RequestVideoInfoResponse implements IVideoInfo {

    private String response;
    private UUID uuid;
    private URL url;

    public RequestVideoInfoResponse(UUID uuid, URL url) {
        this.response = "request video information";
        this.uuid = uuid;
        this.url = url;
    }

    public String getResponse() {
        return response;
    }

    public UUID getUuid() {
        return uuid;
    }

    public URL getUrl() {
        return url;
    }
}
