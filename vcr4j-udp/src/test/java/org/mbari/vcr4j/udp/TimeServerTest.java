package org.mbari.vcr4j.udp;

import org.junit.Test;
import static org.junit.Assert.*;
import java.net.SocketException;

/**
 * @author Brian Schlining
 * @since 2016-02-04T15:25:00
 */
public class TimeServerTest {

    @Test
    public void testServer() {
        try {
            TimeServer server = new TimeServer(9000);
            server.start();
            Thread.sleep(200);
            server.stop();
        }
        catch (SocketException e) {
            fail("An exception occurred with the Socket: " + e.getMessage());
        }
        catch (InterruptedException e) {
            fail("An interruption occurred while running a simple socket test");
        }

    }
}
