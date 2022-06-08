package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.VideoCommand;

public enum RemoteCommands implements VideoCommand<Void> {

    FRAMEADVANCE("frame advance"),

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
