package org.mbari.vcr4j.sharktopoda.client.localization;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Brian Schlining
 * @since 2020-02-11T15:33:00
 */
public class Preconditions {
    public static void require(boolean ok, String msg)  {
        if (!ok) {
            throw new IllegalArgumentException(msg);
        }
    }
}
