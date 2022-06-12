package org.mbari.vcr4j.remote.player;

public class SimpleRequest {
    private String command;

    private String raw;

    public SimpleRequest(String command, String raw) {
        this.command = command;
        this.raw = raw;
    }

    public SimpleRequest(String command) {
        this(command, null);
    }

    public String getCommand() {
        return command;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }


}
