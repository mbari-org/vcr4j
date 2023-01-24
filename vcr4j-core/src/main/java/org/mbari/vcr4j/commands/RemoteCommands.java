package org.mbari.vcr4j.commands;

import org.mbari.vcr4j.VideoCommand;

/**
 *
 * @author Brian Schlining
 * @since 2022-08-08
 */
public enum RemoteCommands implements VideoCommand<Void> {

    FRAMEADVANCE("frame advance"),

    PING("ping"),

    REQUEST_ALL_VIDEO_INFOS("request all video information"),

    REQUEST_VIDEO_INFO("request video information"),

    SHOW("show"),

    CLOSE("close");


    private final String name;

    RemoteCommands(String name) {
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
