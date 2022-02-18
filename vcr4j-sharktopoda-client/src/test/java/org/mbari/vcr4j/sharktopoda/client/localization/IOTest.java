package org.mbari.vcr4j.sharktopoda.client.localization;

import static org.junit.Assert.*;

import javafx.collections.ListChangeListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import zmq.ZMQ;

import java.util.concurrent.atomic.AtomicInteger;
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
    ChangeCount c0 = new ChangeCount();
    ChangeCount c1 = new ChangeCount();
    private final long sleepTime = 200;

    {
        io0.getController()
                .getLocalizations()
                .addListener(c0.newListener());
        io1.getController()
                .getLocalizations()
                .addListener(c1.newListener());
    }



    @Before
    public void reset() {
        io0.getController().clear();
//        io1.getController().clear();
        c0.resetCount();
        c1.resetCount();
        try {
            Thread.sleep(sleepTime);
        }
        catch (InterruptedException e) {
            fail("Reset was interrupted");
        }
    }

    @Test
    public void testMessagePassing() throws Exception {
        reset();
        var msg = new Message(Message.ACTION_CLEAR);
        io0.getController().getOutgoing().onNext(msg);
        Thread.sleep(sleepTime); // allow message time to propagate
        io1.getController().getOutgoing().onNext(msg);
        Thread.sleep(sleepTime); // allow message time to propagate
    }

    @Test
    public void testAdd() throws Exception {
        reset();
        var x = DataGenerator.newLocalization();
        io0.getController().addLocalization(x);
        var y = DataGenerator.newLocalization();
        io0.getController().addLocalization(y);
        Thread.sleep(sleepTime * 4); // allow message time to propagate
        assertEquals(2, io0.getController().getLocalizations().size());
        assertEquals(2, io1.getController().getLocalizations().size());
        c0.assertCount(0, 2, 0, 0, 0);
        c1.assertCount(0, 2, 0, 0, 0);
    }

    @Test
    public void testAddAndRemove() throws Exception {
        reset();
        var x = DataGenerator.newLocalization();
        io0.getController().addLocalization(x);
        Thread.sleep(sleepTime); // allow message time to propagate
        io0.getController().removeLocalization(x);
        Thread.sleep(sleepTime); // allow message time to propagate
        assertEquals(0, io0.getController().getLocalizations().size());
        assertEquals(0, io1.getController().getLocalizations().size());
        c0.assertCount(0, 1, 0, 0, 1);
        c1.assertCount(0, 1, 0, 0, 1);
    }

    @Test
    public void testMultiAddAndClear() throws Exception {
        reset();
        var n = 5;
        var xs = DataGenerator.newLocalizations(n);

        io0.getController().addLocalizations(xs);
        Thread.sleep(sleepTime); // allow message time to propagate
        assertEquals(n, io0.getController().getLocalizations().size());
        assertEquals(n, io1.getController().getLocalizations().size());
        c0.assertCount(0, n, 0, 0, 0);
        c1.assertCount(0, n, 0, 0, 0);
        io1.getController().clear();
        Thread.sleep(sleepTime); // allow message time to propagate
        assertEquals(0, io0.getController().getLocalizations().size());
        c0.assertCount(0, n, 0, 0, n);
        c1.assertCount(0, n, 0, 0, n);
    }

    @Test
    public void testAddAndClear() throws Exception {
        reset();
        var x = DataGenerator.newLocalization();
        io0.getController()
                .addLocalization(x);
        Thread.sleep(sleepTime); // allow message time to propagate
        assertEquals(1, io0.getController().getLocalizations().size());
        assertEquals(1, io1.getController().getLocalizations().size());
        io1.getController().clear();
        Thread.sleep(sleepTime); // allow message time to propagate
        assertEquals(0, io0.getController().getLocalizations().size());
        assertEquals(0, io1.getController().getLocalizations().size());
    }

    /**
     * Controller 0 creates an annotation. Controller 1 modifies it and
     * replaces it.
     * @throws Exception
     */
    @Test
    public void testPingPong() throws Exception {
        reset();


        // Ping
        var x = DataGenerator.newLocalization();
        io1.getController().addLocalization(x);
        Thread.sleep(sleepTime);
        c0.assertCount(0, 1, 0, 0, 0);
        c1.assertCount(0, 1, 0, 0, 0);

        // Pong
        var y = new Localization(x);
        y.setConcept("FOO");
        io0.getController().addLocalization(y);
        Thread.sleep(sleepTime);
        c0.assertCount(0, 2, 0, 1, 1);
        c1.assertCount(0, 2, 0, 1, 1);

    }


    @AfterClass
    public static void close() {
        io0.close();
        io1.close();
    }


}
