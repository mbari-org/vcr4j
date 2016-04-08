package org.mbari.vcr4j.rxtx;

/**
 * @author Brian Schlining
 * @since 2016-01-28T16:40:00
 */
public class RXTXException extends RuntimeException {

    public RXTXException() {
    }

    public RXTXException(Throwable cause) {
        super(cause);
    }

    public RXTXException(String message) {
        super(message);
    }

    public RXTXException(String message, Throwable cause) {
        super(message, cause);
    }

    public RXTXException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
