package org.mbari.vcr4j.vlc.http;

import org.mbari.vcr4j.VideoCommand;

/**
 * @author Brian Schlining
 * @since 2019-06-14T16:07:00
 */
public enum VlcHttpCommands implements VideoCommand<Void> {

    SHOW("show"),
    CLOSE("close");

    private final String name;

    VlcHttpCommands(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Void getValue() {
        return null;
    }

}
