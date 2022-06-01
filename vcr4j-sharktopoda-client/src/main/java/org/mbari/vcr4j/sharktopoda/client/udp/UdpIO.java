package org.mbari.vcr4j.sharktopoda.client.udp;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.sharktopoda.client.gson.DurationConverter;
import org.mbari.vcr4j.sharktopoda.client.model.GenericCommand;
import org.mbari.vcr4j.sharktopoda.client.model.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Brian Schlining
 * @since 2017-12-05T13:02:00
 */
class UdpIO {
    private final int port;
    private DatagramSocket server;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Thread receiverThread;
    private final Subject<GenericCommand> commandSubject;
    private final Subject<GenericResponse> responseSubject;
    private volatile boolean ok = true;
    private final Gson gson = newGson();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UdpIO(int port) {
        this.port = port;
        PublishSubject<GenericCommand> s1 = PublishSubject.create();
        commandSubject = s1.toSerialized();

        Scheduler scheduler = Schedulers.from(executor);
        PublishSubject<GenericResponse> s2 = PublishSubject.create();
        responseSubject = s2.toSerialized();
        responseSubject.subscribeOn(scheduler)
                .subscribe(this::doResponse);

        receiverThread = buildReceiverThread();
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public void close() {
        if (ok) {
            ok = false;
            executor.shutdown();
            commandSubject.onComplete();
            responseSubject.onComplete();
        }
    }

    private void doResponse(GenericResponse response) {
        if (response.isResponseExpected()) {
            try {
                DatagramSocket s = getServer();
                byte[] b = gson.toJson(response).getBytes();
                log.debug("Sending >>> " + new String(b));
                DatagramPacket packet = new DatagramPacket(b,
                        b.length,
                        response.getPacketAddress(),
                        response.getPacketPort());
                s.send(packet);
            } catch (Exception e) {
                log.error("UDP response failed", e);
            }
        }
    }

    private Thread buildReceiverThread() {
        return new Thread(() -> {
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while(ok) {
                try {
                    getServer().receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    log.debug("Received <<< " + msg);
                    GenericCommand r = gson.fromJson(msg, GenericCommand.class);
                    r.setPacketAddress(packet.getAddress());
                    r.setPacketPort(packet.getPort());
                    commandSubject.onNext(r);
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
            log.info("Shutting down UDP server");

        });
    }




    private DatagramSocket getServer() throws SocketException {
        if (server == null || server.isClosed()) {
            server = new DatagramSocket(port);
        }
        return server;
    }

    public static Gson newGson() {
        return new GsonBuilder().setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .registerTypeAdapter(Duration.class, new DurationConverter())
                .create();

    }

    public Subject<GenericResponse> getResponseSubject() {
        return responseSubject;
    }

    public Subject<GenericCommand> getCommandSubject() {
        return commandSubject;
    }
}
