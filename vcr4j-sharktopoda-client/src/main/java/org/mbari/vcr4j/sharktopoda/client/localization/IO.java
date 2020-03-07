package org.mbari.vcr4j.sharktopoda.client.localization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
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
    private final LocalizationController controller;
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
              String outgoingTopic,
              LocalizationController controller) {

        this.incomingPort = incomingPort;
        this.outgoingPort = outgoingPort;
        this.controller = controller;
        this.controller.getOutgoing()
                .ofType(Message.class)
                .subscribe(lcl -> queue.offer(lcl));

        // publisher
        outgoingThread = new Thread(() -> {
            String address = "tcp://*:" + outgoingPort;
            log.info("ZeroMQ Publishing to {} using topic '{}'", address, outgoingTopic);
            Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind(address);
            // This sleep is critical to give ZMQ time to bind and setup
            // Otherwise messages will not be sent.
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                log.warn("ZeroMQ publisher thread was interrupted", e);
            }
            while (ok && !Thread.currentThread().isInterrupted()) {
                try {
                    Message msg = queue.poll(5L, TimeUnit.SECONDS);
                    if (msg != null) {
                        String json = gson.toJson(msg);
                        log.debug("Publishing to '{}': \n{}", outgoingTopic, json);
                        publisher.sendMore(outgoingTopic);
                        publisher.send(json);
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
            log.info("Shutting down ZeroMQ publisher at {}", address);
            publisher.close();
        });
        outgoingThread.setDaemon(true);
        outgoingThread.start();

        // subscriber
        incomingThread = new Thread(() -> {
            String address = "tcp://localhost:" + incomingPort;
            log.info("ZeroMQ Subscribing to {} using topic '{}'", address, incomingTopic);
            Socket socket = context.createSocket(SocketType.SUB);
            socket.connect(address);
            socket.subscribe(incomingTopic.getBytes(ZMQ.CHARSET));
            while (ok && !Thread.currentThread().isInterrupted()) {
                try {
                    String topicAddress = socket.recvStr();
                    String contents = socket.recvStr();
                    log.debug("Received on '{}': {}", topicAddress, contents);
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

    public IO(int incomingPort,
              int outgoingPort,
              String incomingTopic,
              String outgoingTopic) {
        this(incomingPort, outgoingPort, incomingTopic, outgoingTopic, new LocalizationController());
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

    public void publish(Message msg) {
        controller.getOutgoing().onNext(msg);
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
