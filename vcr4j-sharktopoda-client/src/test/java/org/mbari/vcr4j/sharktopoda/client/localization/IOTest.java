package org.mbari.vcr4j.sharktopoda.client.localization;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import zmq.ZMQ;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Brian Schlining
 * @since 2020-02-12T13:15:00
 */
public class IOTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final ZContext context = new ZContext();
    private static int port0 = 5567;
    private static int port1 = 5568;
    private static String topic0 = "foo";
    private static String topic1 = "bar";
    private static IO io0 = new IO(port0, port1, topic0, topic1);
    private static IO io1 = new IO(port1, port0, topic1, topic0);

    @Before
    public void reset() {
        io0.getController().clearAllLocalizations();
        io1.getController().clearAllLocalizations();
    }

    @Test
    public void testMessagePassing() throws Exception {
        reset();
        var msg = new Message(Message.ACTION_CLEAR_ALL);
        io0.getController().getOutgoing().onNext(msg);
        Thread.sleep(1000); // allow message time to propagate
        io1.getController().getOutgoing().onNext(msg);
        Thread.sleep(1000); // allow message time to propagate
    }

    @Test
    public void testAdd() throws Exception {
        reset();
        var x = DataGenerator.newLocalization();
        io0.getController().addLocalization(x);
        Thread.sleep(1000); // allow message time to propagate
        var y = DataGenerator.newLocalization();
        io0.getController().addLocalization(y);
        Thread.sleep(1000); // allow message time to propagate
        assertEquals(2, io0.getController().getLocalizations().size());
        assertEquals(2, io1.getController().getLocalizations().size());
    }

    @Test
    public void testAddAndRemove() throws Exception {
        reset();
        var x = DataGenerator.newLocalization();
        io0.getController().addLocalization(x);
        Thread.sleep(1000); // allow message time to propagate
        io0.getController().removeLocalization(x);
        Thread.sleep(1000); // allow message time to propagate
        assertEquals(0, io0.getController().getLocalizations().size());
        assertEquals(0, io1.getController().getLocalizations().size());
    }

    @Test
    public void testMultiAddAndClear() throws Exception {
        reset();
        var n = 5;
        var xs = DataGenerator.newLocalizations(n);
        io0.getController()
                .getIncoming()
                .onNext(new Message(Message.ACTION_ADD, xs));
        Thread.sleep(1000); // allow message time to propagate
        assertEquals(n, io0.getController().getLocalizations().size());
        io1.getController()
                .getOutgoing()
                .onNext(new Message(Message.ACTION_CLEAR_ALL));
        Thread.sleep(1000); // allow message time to propagate
        assertEquals(0, io0.getController().getLocalizations().size());
    }

    @Test
    public void testAddAndClear() throws Exception {
        reset();
        var x = DataGenerator.newLocalization();
        io0.getController()
                .addLocalization(x);
        Thread.sleep(2000); // allow message time to propagate
        assertEquals(1, io0.getController().getLocalizations().size());
        assertEquals(1, io1.getController().getLocalizations().size());
        io1.getController()
                .getOutgoing()
                .onNext(new Message(Message.ACTION_CLEAR_ALL));
        Thread.sleep(1000); // allow message time to propagate
        assertEquals(0, io0.getController().getLocalizations().size());
        assertEquals(1, io1.getController().getLocalizations().size());
    }


    @AfterClass
    public static void close() {
        io0.close();
        io1.close();
    }


}
