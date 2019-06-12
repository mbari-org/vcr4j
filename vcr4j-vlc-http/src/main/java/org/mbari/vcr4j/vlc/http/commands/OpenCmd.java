package org.mbari.vcr4j.vlc.http.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

import java.net.URL;

public class OpenCmd extends SimpleVideoCommand<URL> {

    public OpenCmd(URL value) {
        super("open", value);
    }
}