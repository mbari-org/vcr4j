package org.mbari.vcr4j.sharktopoda.decorators;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.sharktopoda.Constants;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.ConnectCmd;
import org.mbari.vcr4j.sharktopoda.commands.FramecaptureCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.mbari.vcr4j.sharktopoda.model.request.Connect;
import org.mbari.vcr4j.sharktopoda.model.request.Framecapture;
import org.mbari.vcr4j.sharktopoda.model.response.FramecaptureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2016-08-30T09:39:00
 */
public class FramecaptureDecorator implements Decorator {

    private final SharktopodaVideoIO io;
    private final int port;
    private DatagramSocket server;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Subject<FramecaptureResponse> framecaptureSubject;
    private Disposable disposable;
    private volatile boolean ok = true;
    private final Observer<FramecaptureCmd> subscriber;

    private final Thread receiverThread;

    /**
     *
     * @param io
     * @param port The out-of-band port tha should be used by Sharktopoda
     */
    public FramecaptureDecorator(SharktopodaVideoIO io, int port) {
        this.io = io;
        this.port = port;
        PublishSubject<FramecaptureResponse> s1 = PublishSubject.create();
        framecaptureSubject = s1.toSerialized();

        receiverThread = new Thread(() -> {
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while(ok) {
                try {
                    getServer().receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    log.debug("GOT MSG: " + msg);
                    FramecaptureResponse r = Constants.GSON.fromJson(msg, FramecaptureResponse.class);
                    framecaptureSubject.onNext(r);
                }
                catch (Exception e) {
                    log.info("Error while reading UDP datagram", e);
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

        receiverThread.setDaemon(true);
        receiverThread.start();

        subscriber = new Observer<FramecaptureCmd>() {
            @Override
            public void onComplete() {
                ok = false;
            }

            @Override
            public void onError(Throwable throwable) {
                ok = false;
            }

            @Override
            public void onNext(FramecaptureCmd cmd) {
                doFrameCapture(cmd);
            }

            @Override
            public void onSubscribe(Disposable disposable) {
                FramecaptureDecorator.this.disposable = disposable;
            }
        };

        io.getCommandSubject()
                .ofType(FramecaptureCmd.class)
                .subscribe(subscriber);

        io.getCommandSubject()
                .ofType(ConnectCmd.class)
                .forEach(this::doConnect);

        io.getCommandSubject()
                .filter(cmd -> cmd == SharkCommands.CLOSE)
                .forEach(cmd -> ok = false);

        io.send(new ConnectCmd(port));

    }

    public Observable<FramecaptureResponse> getFramecaptureObservable() {
        return framecaptureSubject;
    }

    private DatagramSocket getServer() throws SocketException {
        if (server == null || server.isClosed()) {
            server = new DatagramSocket(port);
        }
        return server;
    }

    private void doFrameCapture(FramecaptureCmd cmd) {
        FramecaptureCmd.Params params = cmd.getValue();
        try {
            URL url = new File(params.getImageLocation()).toURI().toURL();
            Framecapture obj = new Framecapture(io.getUUID(), params.getImageReferenceUuid(), url.toExternalForm());
            DatagramPacket packet = io.asPacket(obj);
            io.sendCommand(packet, cmd);
        }
        catch (MalformedURLException e) {
            log.info("Unable to convert file path to URL", e);
        }
    }

    private void doConnect(ConnectCmd cmd) {
        ConnectCmd.Params params = cmd.getValue();
        Connect obj = new Connect(params.getPort(), params.getHost());
        DatagramPacket packet = io.asPacket(obj);
        io.sendCommand(packet, cmd);
    }


    @Override
    public void unsubscribe() {
        server.close();
        ok = false;
        disposable.dispose();
    }
}
