package org.mbari.vcr4j.remote.player;

/**
 *  This is used as a video control to do a first pass at deserializing an incoming command/request.
 *  It parses the command portion of JSON only. The raw field contains the entire JSON of the
 *  original request.
 * @author Brian Schlining
 * @since 2022-08-08
 */
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
