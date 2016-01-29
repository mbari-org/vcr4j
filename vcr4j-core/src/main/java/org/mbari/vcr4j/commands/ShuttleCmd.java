package org.mbari.vcr4j.commands;

import org.mbari.vcr4j.util.Preconditions;

public class ShuttleCmd extends SimpleVideoCommand<Double> {
    
    public ShuttleCmd(double rate) {
        super("shuttle", rate);
        Preconditions.checkArgument(rate <= 1 && rate >= -1,
                "rate must be between -1 and 1 [you provided " + rate + "]");
    }
    
}