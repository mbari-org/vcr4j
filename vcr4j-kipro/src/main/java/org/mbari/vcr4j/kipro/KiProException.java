package org.mbari.vcr4j.kipro;

/**
 * @author Brian Schlining
 * @since 2016-02-11T10:42:00
 */
public class KiProException extends RuntimeException {

    public KiProException() {
    }

    public KiProException(Throwable cause) {
        super(cause);
    }

    public KiProException(String message) {
        super(message);
    }

    public KiProException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiProException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
