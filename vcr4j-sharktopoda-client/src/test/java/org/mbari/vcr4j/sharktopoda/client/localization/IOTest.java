package org.mbari.vcr4j.sharktopoda.client.localization;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import zmq.ZMQ;

import java.time.Duration;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2020-02-12T13:15:00
 */
public class IOTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testPublishing() throws Exception {
        var incomingPort = 5564;
        var outgoingPort = 5565;
        var incomingTopic = "foo";
        var outgoingTopic = "bar";
        var io = new IO(incomingPort, outgoingPort, incomingTopic, outgoingTopic);
        var controller = io.getController();
        var localizationUuid = UUID.randomUUID();
        var localization0 = new Localization("Nanomia",
                Duration.ofSeconds(3),
                localizationUuid,
                10, 11, 12, 13,
                Duration.ofMillis(1234));
        var context = new ZContext();
        var thread = new Thread(() -> {
           var socket = context.createSocket(SocketType.SUB);
           socket.connect("tcp://localhost:" + outgoingPort);
           socket.subscribe(outgoingTopic.getBytes(ZMQ.CHARSET));
           while (!Thread.currentThread().isInterrupted()) {
               var address = socket.recvStr();
               var contents = socket.recvStr();
               var msg = io.getGson().fromJson(contents, Message.class);
               log.info("Got: " + msg);
               assertEquals(msg.getAction(), Message.ACTION_ADD);
               var localization1 = msg.getLocalization();
               assertEquals(localization0.getLocalizationUuid(), localization1.getLocalizationUuid());
               assertEquals(localization0.getConcept(), localization1.getConcept());
               assertEquals(localization0.getElapsedTime(), localization1.getElapsedTime());
               assertEquals(localization0.getDuration(), localization1.getDuration());
               assertEquals(localization0.getEndTime(), localization1.getEndTime());
               assertEquals(localization0.getX(), localization1.getX());
               assertEquals(localization0.getY(), localization1.getY());
               assertEquals(localization0.getWidth(), localization1.getWidth());
               assertEquals(localization0.getHeight(), localization1.getHeight());
           }
        });
        thread.start();
        controller.addLocalization(localization0);
        Thread.sleep(1000L);
    }


}
