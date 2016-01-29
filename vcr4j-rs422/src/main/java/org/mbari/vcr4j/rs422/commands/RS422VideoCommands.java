package org.mbari.vcr4j.rs422.commands;

import org.mbari.vcr4j.commands.VideoCommand;

public enum RS422VideoCommands implements VideoCommand<RS422ByteCommands> {
    DISCONNECT("disconnect", RS422ByteCommands.UNDEFINED),
    EJECT("eject", RS422ByteCommands.EJECT_TAPE),
    RECORD("record", RS422ByteCommands.RECORD),
    RELEASE_TAPE("release tape", RS422ByteCommands.RELEASE_TAPE),
    REQUEST_LTIMECODE("request longitudinal timecode", RS422ByteCommands.GET_LTIMECODE),
    REQUEST_LUSERBITS("request longitudinal userbits", RS422ByteCommands.GET_LUBTIMECODE),
    REQUEST_LOCAL_DISABLE("request local disable", RS422ByteCommands.LOCAL_DISABLE),
    REQUEST_LOCAL_ENABLE("request local enable", RS422ByteCommands.LOCAL_ENABLE),
    REQUEST_USERBITS("request userbits", RS422ByteCommands.UNDEFINED),
    REQUEST_VTIMECODE("request vertical timecode", RS422ByteCommands.GET_VTIMECODE),
    REQUEST_VUSERBITS("request vertical userbits", RS422ByteCommands.GET_VUBTIMECODE);


    private final String name;
    private final RS422ByteCommands value;

    RS422VideoCommands(String name, RS422ByteCommands value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RS422ByteCommands getValue() {
        return value;
    }
}
