package org.mbari.vcr4j.commands;

import org.mbari.vcr4j.VideoCommand;

public enum VideoCommands implements VideoCommand<Void> {
    FAST_FORWARD("fast forward"),
    PAUSE("pause"),
    PLAY("play"),
    REQUEST_DEVICE_TYPE("request device type"),
    REQUEST_ELAPSED_TIME("request elapsed time"),
    REQUEST_INDEX("request index"),
    REQUEST_STATUS("request status"),
    REQUEST_TIMECODE("request timecode"),
    REQUEST_TIMESTAMP("request timestamp"),
    REWIND("rewind"),
    STOP("stop");

    private final String name;

    VideoCommands(String name) {
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
