package org.mbari.vcr4j.rs422;

/**
 * @author Brian Schlining
 * @since 2016-02-02T12:27:00
 */
public class RS422Exception extends RuntimeException {

    public RS422Exception() {
    }

    public RS422Exception(Throwable cause) {
        super(cause);
    }

    public RS422Exception(String message) {
        super(message);
    }

    public RS422Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public RS422Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
