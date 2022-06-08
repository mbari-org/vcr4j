package org.mbari.vcr4j.remote.control;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.remote.control.commands.CloseCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerRequestDecorator implements Decorator {

    private static final Logger log = LoggerFactory.getLogger(PlayerRequestDecorator.class);

    private final RVideoIO videoIO;
    private final int port;

    private List<Disposable> disposables = new ArrayList<>();

    private final Subject<SimpleResponse> responseSubject;
    private DatagramSocket server;
    private Thread receiverThread;
    private volatile boolean ok = true;

    public PlayerRequestDecorator(RVideoIO videoIO, int port) {
        this.videoIO = videoIO;
        this.port = port;
        PublishSubject<SimpleResponse> s = PublishSubject.create();
        responseSubject = s.toSerialized();
        init();
    }

    private void init() {
        try {
            server = new DatagramSocket(port);
            receiverThread = buildReceiverThread();
            receiverThread.setDaemon(true);
            receiverThread.start();
        }
        catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .log("Failed to initialize UDP socket");
        }
        var d = videoIO.getCommandSubject()
                .ofType(CloseCmd.class)
                .forEach(cmd -> {
                    if (cmd.getValue().getUuid().equals(videoIO.getUuid())) {
                        unsubscribe();
                    }
                });
        disposables.add(d);
    }

    private Thread buildReceiverThread()  {
        return new Thread(() -> {
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while(ok) {
                if (server == null) {
                    ok = false;
                    continue;
                }
                try {
                    server.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    log.debug("Received <<< " + msg);
                    var r = RVideoIO.GSON.fromJson(msg, SimpleResponse.class);
                    r.setRaw(msg);
                    responseSubject.onNext(r);
                }
                catch (Exception e) {
                    log.info("Error while reading UDP datagram", e);
                    videoIO.getErrorSubject()
                            .onNext(new RError(true, true, false,
                                    null, "Error while reading UDP datagram", e));

                    if (!server.isClosed()) {
                        server.close();
                    }
                    if (server != null) {
                        server = null;
                    }
                }
            }
            if (server != null) {
                server.close();
            }
            log.info("Shutting down UDP server that listens to Sharktopoda for framegrabs");
        });
    }

    @Override
    public void unsubscribe() {
        server.close();
        ok = false;
        disposables.forEach(Disposable::dispose);
    }
}
