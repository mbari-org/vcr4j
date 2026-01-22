package org.mbari.vcr4j.udp;

/*-
 * #%L
 * vcr4j-udp
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
    private volatile Thread serverThread;

    private static final System.Logger log = System.getLogger(TimeServer.class.getName());

    public TimeServer(int port) throws SocketException {
        socket = new DatagramSocket(port);

    }

    public void start() {

        if (!run.get()) {
            log.log(System.Logger.Level.DEBUG, "Starting time server");
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
        log.log(System.Logger.Level.DEBUG, "Stopping time server");
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
