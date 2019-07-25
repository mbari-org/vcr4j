package org.mbari.vcr4j.vlc.http.model;

import java.net.URI;
import java.util.List;

public class PlayList {

    private String ro;
    private String type;
    private String name;
    private String id;
    private List<PlayList> children;
    private Integer duration;
    private URI uri;

    public PlayList() {}

    public PlayList(String ro, String type, String name, String id,
                    List<PlayList> children, Integer duration, URI uri) {
        this.ro = ro;
        this.type = type;
        this.name = name;
        this.id = id;
        this.children = children;
        this.duration = duration;
        this.uri = uri;
    }

    public String getRo() {
        return ro;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<PlayList> getChildren() {
        return children;
    }

    public Integer getDuration() {
        return duration;
    }

    public URI getUri() {
        return uri;
    }
}
