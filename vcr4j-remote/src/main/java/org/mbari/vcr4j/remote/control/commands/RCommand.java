package org.mbari.vcr4j.remote.control.commands;

import org.mbari.vcr4j.VideoCommand;

public abstract class RCommand<A extends RRequest, B extends RResponse> implements VideoCommand<A> {

    private final A value;

    public RCommand(A value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return value.getCommand();
    }

    @Override
    public A getValue() {
        return value;
    }

    public abstract Class<B> responseType();

//    public DatagramPacket asPacket(InetAddress inetAddress, int port) {
//        byte[] b = RVideoIO.GSON.toJson(getValue()).getBytes(StandardCharsets.UTF_8);
//        return new DatagramPacket(b, b.length, inetAddress, port);
//    }

}
