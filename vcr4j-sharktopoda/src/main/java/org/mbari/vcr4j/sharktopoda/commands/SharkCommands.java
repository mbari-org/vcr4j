package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.VideoCommand;

/**
 * @author Brian Schlining
 * @since 2016-08-29T11:14:00
 */
public enum SharkCommands implements VideoCommand<Void>  {

    FRAMEADVANCE("frame advance"),
    REQUEST_ALL_VIDEO_INFOS("request all video information"),
    REQUEST_VIDEO_INFO("request video information"),
    SHOW("show"),
    CLOSE("close");


    private final String name;

    SharkCommands(String name) {
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
