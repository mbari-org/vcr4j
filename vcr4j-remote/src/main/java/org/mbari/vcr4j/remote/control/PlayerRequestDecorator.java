package org.mbari.vcr4j.remote.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Optional;

public class PlayerRequestDecorator {

    private static final Logger log = LoggerFactory.getLogger(PlayerRequestDecorator.class);

    private final RVideoIO videoIO;
    private final int port;
    private DatagramSocket server;
    private final Thread receiverThread;
    private volatile boolean ok = true;

    public PlayerRequestDecorator(RVideoIO videoIO, int port) {
        this.videoIO = videoIO;
        this.port = port;

    }

    private void init() {
        try {
            server = new DatagramSocket(port);
        }
        catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .log("Failed to initialize UDP socket");
        }
    }

    private Thread buildReceiverThread()  {
        return new Thread(() -> {
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while(ok) {
                try {
                    server.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    log.debug("Received <<< " + msg);
                    FramecaptureResponse r = Constants.GSON.fromJson(msg, FramecaptureResponse.class);
                    framecaptureSubject.onNext(r);
                }
                catch (Exception e) {
                    log.info("Error while reading UDP datagram", e);
                    io.getErrorSubject()
                            .onNext(new SharktopodaError(true, true, false, Optional.empty()));

                    if (!server.isClosed()) {
                        server.close();
                    }
                    if (server != null) {
                        server = null;
                    }
                }

            }
        });
    }
}
