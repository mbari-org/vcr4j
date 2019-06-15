package org.mbari.vcr4j.vlc.http.model;

/**
 * @author Brian Schlining
 * @since 2019-06-14T17:01:00
 */
public class ErrorMsg {
    private final int code;
    private final String message;

    public ErrorMsg(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
