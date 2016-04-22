package org.mbari.vcr4j.commands;

import org.mbari.vcr4j.VideoIndex;

/**
 * Occasionally, we need to force a video index. VideoIO's that support this should
 * just add the contained value to the index subject so it can be passed on in
 * the indexObservable
 */
public class InjectVideoIndexCmd extends SimpleVideoCommand<VideoIndex> {

    public InjectVideoIndexCmd(String name, VideoIndex value) {
        super(name, value);
    }
}
