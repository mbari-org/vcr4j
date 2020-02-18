package org.mbari.vcr4j.sharktopoda.client.localization;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import zmq.ZMQ;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Brian Schlining
 * @since 2020-02-12T13:15:00
 */
public class IOTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ZContext context = new ZContext();
    private int port0 = 5561;
    private int port1 = 5562;
    private String topic0 = "foo";
    private String topic1 = "bar";
    private IO io0 = new IO(port0, port1, topic0, topic1);
//    private IO io1 = new IOport1, port0, topic1, topic0);

    @Test
    public void testAdd() throws Exception {
        var x = DataGenerator.newLocalization();
        io0.getController().addLocalization(x);
        Thread.sleep(100);
        var y = DataGenerator.newLocalization();
        io0.getController().addLocalization(y);
        Thread.sleep(2000);
    }

    @Test
    public void testPublishing() throws Exception {
        var incomingPort = 5564;
        var outgoingPort = 5565;
        var incomingTopic = "foo";
        var outgoingTopic = "bar";
        var io = new IO(incomingPort, outgoingPort, incomingTopic, outgoingTopic);
        var controller = io.getController();
        var localization0 = DataGenerator.newLocalization();

        var recvMessage = new AtomicReference<Message>();
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
               var localization1 = msg.getLocalizations().get(0);
               assertEquals(localization0.getLocalizationUuid(), localization1.getLocalizationUuid());
               assertEquals(localization0.getConcept(), localization1.getConcept());
               assertEquals(localization0.getElapsedTime(), localization1.getElapsedTime());
               assertEquals(localization0.getDuration(), localization1.getDuration());
               assertEquals(localization0.getEndTime(), localization1.getEndTime());
               assertEquals(localization0.getX(), localization1.getX());
               assertEquals(localization0.getY(), localization1.getY());
               assertEquals(localization0.getWidth(), localization1.getWidth());
               assertEquals(localization0.getHeight(), localization1.getHeight());
               recvMessage.set(msg);
           }
        });
        thread.start();
        controller.addLocalization(localization0);
        Thread.sleep(2000L);
        assertNotNull(recvMessage.get());
    }

//    @After
//    public void close() {
//        io0.close();
//        io1.close();
//    }


}
