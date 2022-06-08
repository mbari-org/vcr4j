package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.VideoCommand;

import java.util.UUID;

public class RRequest {

    protected String command;
    protected UUID uuid;

    public RRequest(String command, UUID uuid) {
        this.command = command;
        this.uuid = uuid;
    }

    public String getCommand() {
        return command;
    }

    public UUID getUuid() {
        return uuid;
    }

}
