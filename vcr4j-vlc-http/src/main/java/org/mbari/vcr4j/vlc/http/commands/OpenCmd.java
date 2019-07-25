package org.mbari.vcr4j.vlc.http.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

import java.net.URL;

public class OpenCmd extends SimpleVideoCommand<String> {

    public OpenCmd(String mrl) {
        super("open", mrl);
    }
}