package org.mbari.vcr4j.remote.commands;

import org.mbari.vcr4j.VideoCommand;

import java.util.UUID;

public class RCommand<A extends RRequest> implements VideoCommand<A> {

    private final A value;

    public RCommand(A value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return value.getCommand();
    }

    @Override
    public A getValue() {
        return value;
    }

}
