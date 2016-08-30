package org.mbari.vcr4j.sharktopoda.decorators;

import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;

/**
 * @author Brian Schlining
 * @since 2016-08-30T16:29:00
 */
public class VideoInfoDecorator implements Decorator {

    private final SharktopodaVideoIO io;

    public VideoInfoDecorator(SharktopodaVideoIO io) {
        this.io = io;
    }

    @Override
    public void unsubscribe() {

    }
}
