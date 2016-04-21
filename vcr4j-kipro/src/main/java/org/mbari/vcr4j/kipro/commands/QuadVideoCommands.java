package org.mbari.vcr4j.kipro.commands;

import org.mbari.vcr4j.VideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-02-08T16:31:00
 */
public enum QuadVideoCommands implements VideoCommand<Void> {
    CONNECT("connect"),
    CONFIG_EVENT("config event");

    private final String name;

    QuadVideoCommands(String name) {
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
