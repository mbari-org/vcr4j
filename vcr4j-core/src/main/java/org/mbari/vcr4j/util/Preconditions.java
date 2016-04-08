package org.mbari.vcr4j.util;

/**
 * @author Brian Schlining
 * @since 2016-01-28T12:41:00
 */
public class Preconditions {
    public static void checkArgument(boolean arg, String msg) {
        if (!arg) {
            throw new IllegalArgumentException(msg);
        }
    }
}
