package org.mbari.vcr4j.sharktopoda.client.udp;

import org.mbari.vcr4j.sharktopoda.client.model.Video;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2020-02-11T11:29:00
 */
public class BadMockClientController extends MockClientController {

    @Override
    public Optional<Video> requestVideoInfo() {
        return Optional.empty();
    }
}
