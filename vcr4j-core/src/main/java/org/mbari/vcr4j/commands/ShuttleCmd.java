package org.mbari.vcr4j.commands;

import org.mbari.vcr4j.util.Preconditions;

/**
 * A command to tell the video to shuttle forward (+ values) or backwards (- values). This class
 * excepts values between -1 and 1. ! being the maximum rate supported by the video. Your
 * VideoIO implementation should convert that to the appropriate values.
 */
public class ShuttleCmd extends SimpleVideoCommand<Double> {

    /**
     * @param rate -1 <= rate <= 1. 1 represents the maximum shuttle rate for the device. Your
     *             VideoIO implementation should convert this representation to the appropriate
     *             value.
     */
    public ShuttleCmd(double rate) {
        super("shuttle", rate);
        Preconditions.checkArgument(rate <= 1 && rate >= -1,
                "rate must be between -1 and 1 [you provided " + rate + "]");
    }
    
}