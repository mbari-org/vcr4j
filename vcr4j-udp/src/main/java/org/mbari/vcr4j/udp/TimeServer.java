package org.mbari.vcr4j.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Instant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brian Schlining
 * @since 2016-02-04T15:09:00
 */
public class TimeServer {

    private final DatagramSocket socket;
    private final AtomicBoolean run = new AtomicBoolean(false);
    private Thread serverThread;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public TimeServer(int port) throws SocketException {
        socket = new DatagramSocket(port);

    }

    public void start() {

        if (!run.get()) {
            log.debug("Starting time server");
            run.set(true);
            Runnable runner = () -> {
                byte[] buffer = new byte[4096];
                while (run.get()) {
                    DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                    try {
                        socket.receive(incoming);
                        Calendar calendar = new GregorianCalendar();
                        double frames = calendar.get(Calendar.MILLISECOND) / 1000D * 29.97;
                        String reply = String.format("%tT:%02d", calendar, Math.round(frames));
                        DatagramPacket outgoing = new DatagramPacket(reply.getBytes(),
                                reply.length(),
                                incoming.getAddress(),
                                incoming.getPort());
                        socket.send(outgoing);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            serverThread = new Thread(runner, getClass().getSimpleName() + "-" + Instant.now());
            serverThread.start();
        }

    }

    public void stop() {
        log.debug("Stopping time server");
        run.set(false);
        serverThread = null;
    }

    public static void main(String[] args) throws Exception {
        int port = 9000;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        TimeServer server = new TimeServer(port);
        server.start();
    }




}
