package org.mbari.vcr4j.remote.player;

/*-
 * #%L
 * vcr4j-remote
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

import com.google.gson.Gson;

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * This is the receiving server for the video player. All commands are passed to a {{@link RequestHandler}}
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class PlayerIO {

    private static final System.Logger log = System.getLogger(PlayerIO.class.getName());
    private static final Gson gson = RVideoIO.GSON;

    private final int port;
    private final RequestHandler requestHandler;
    private DatagramSocket server;
    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean ok = true;

    private String connectionId;

    public PlayerIO(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        try {
            connectionId = InetAddress.getLocalHost().getHostName() + ":" + port;
        }
        catch (Exception e) {
            connectionId = "localhost:" + port;
        }
        init();
    }

    private void init() {
        try {
            server = new DatagramSocket(port);
            var serverRunnable = buildServerRunnable();
            serverExecutor.submit(serverRunnable);
            log.log(System.Logger.Level.DEBUG, connectionId + " - Started server's receiver using: " + serverExecutor);
        }
        catch (Exception e) {
            log.log(System.Logger.Level.ERROR, "Failed to initialize UDP socket", e);
        }

    }


    private void respond(RResponse response, InetAddress address, int port) throws IOException {
        var msg = gson.toJson(response);
        var bytes = msg.getBytes();
        var responsePacket = new DatagramPacket(bytes, bytes.length, address, port);
        if (log.isLoggable(System.Logger.Level.DEBUG)) {
            log.log(System.Logger.Level.DEBUG,connectionId + " - Responding >>> " + msg);
        }
        server.send(responsePacket);
    }

    private void handleRequest(SimpleRequest request, InetAddress address, int port) {
        try {
            var response = requestHandler.composeResponse(request);
            respond(response, address, port);
        }
        catch (Exception e) {
            handleError(request, address, port, e);
        }
    }

    private void handleError(SimpleRequest simpleRequest,
                             InetAddress address,
                             int port,
                             Exception ex) {
        var response = ex == null ? requestHandler.handleError(simpleRequest)
                : requestHandler.handleError(simpleRequest, ex);
        try {
            respond(response, address, port);
        } catch (IOException e) {
            log.log(System.Logger.Level.ERROR, connectionId + " - Unable to send an error response for request: " + simpleRequest.getRaw(), e);
        }
    }


    private Runnable buildServerRunnable()  {
        return () -> {
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while(ok) {
                if (server == null) {
                    ok = false;
                    continue;
                }
                try {
                    server.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    log.log(System.Logger.Level.DEBUG, connectionId + " - Received command <<< " + msg);
                    var simpleRequest = RVideoIO.GSON.fromJson(msg, SimpleRequest.class);
                    simpleRequest.setRaw(msg);
                    handleRequest(simpleRequest, packet.getAddress(), packet.getPort());
                }
                catch (Exception e) {
                    log.log(System.Logger.Level.DEBUG, connectionId + " - Error while reading UDP datagram", e);
                }
            }
            if (server != null && !server.isClosed()) {
                server.close();
                server = null;
            }
            log.log(System.Logger.Level.INFO, connectionId + " - Shutting down UDP server");
        };
    }

    public void close() {
        ok = false;
        server.close();
        serverExecutor.shutdown();
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }
}
