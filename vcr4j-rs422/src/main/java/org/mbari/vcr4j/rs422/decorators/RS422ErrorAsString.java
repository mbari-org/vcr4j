package org.mbari.vcr4j.rs422.decorators;

import org.mbari.vcr4j.rs422.RS422Error;

/**
 * @author Brian Schlining
 * @since 2016-02-02T17:14:00
 */
public class RS422ErrorAsString {
    private final String string;

    public RS422ErrorAsString(RS422Error error) {
        string = "{name:'VideoError',class:'" + getClass().getName() + "',error:'" + error.getError() + "'}";
    }

    @Override
    public String toString() {
        return string;
    }
}
