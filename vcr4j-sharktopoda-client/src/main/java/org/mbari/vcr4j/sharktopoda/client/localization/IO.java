package org.mbari.vcr4j.sharktopoda.client.localization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mbari.vcr4j.sharktopoda.client.gson.DurationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Brian Schlining
 * @since 2020-02-11T17:00:00
 */
public class IO {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ZContext context = new ZContext();
    private final int incomingPort;
    private final int outgoingPort;
    private LocalizationController controller = new LocalizationController();
    private final Thread outgoingThread;
    private final Thread incomingThread;
    private volatile boolean ok = true;
    private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .registerTypeAdapter(Duration .class, new DurationConverter())
            .create();


    public IO(int incomingPort,
              int outgoingPort,
              String incomingTopic,
              String outgoingTopic) {
        this.incomingPort = incomingPort;
        this.outgoingPort = outgoingPort;
        controller.getOutgoing()
                .ofType(Message.class)
                .subscribe(lcl -> queue.offer(lcl));

        outgoingThread = new Thread(() -> {
            Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:" + outgoingPort);
            while (ok && !Thread.currentThread().isInterrupted()) {
                try {
                    Message msg = queue.poll(5L, TimeUnit.SECONDS);
                    if (msg != null) {
                        publisher.send(outgoingTopic);
                        publisher.send(gson.toJson(msg));
                    }
                }
                catch (InterruptedException e) {
                    log.warn("ZeroMQ Publisher thread was interrupted", e);
                    ok = false;
                }
                catch (Exception e) {
                    log.warn("An exception was thrown will attempting to publish a localization", e);
                }
            }
            publisher.close();
        });
        outgoingThread.setDaemon(true);
        outgoingThread.start();

        incomingThread = new Thread(() -> {
            Socket socket = context.createSocket(SocketType.SUB);
            socket.connect("tcp://localhost:" + incomingPort);
            socket.subscribe(incomingTopic.getBytes(ZMQ.CHARSET));
            while (ok && !Thread.currentThread().isInterrupted()) {
                try {
                    String address = socket.recvStr();
                    String contents = socket.recvStr();
                    Message message = gson.fromJson(contents, Message.class);
                    controller.getIncoming().onNext(message);
                }
                catch (Exception e) {
                    log.warn("An exception occurred while reading from remote app", e);
                }
            }
        });
        incomingThread.setDaemon(true);
        incomingThread.start();
    }



    public int getIncomingPort() {
        return incomingPort;
    }

    public int getOutgoingPort() {
        return outgoingPort;
    }

    public LocalizationController getController() {
        return controller;
    }

    public void close() {
        ok = false;
        context.close();
        controller.getIncoming().onComplete();
        controller.getOutgoing().onComplete();
    }

    public Gson getGson() {
        return gson;
    }
}
