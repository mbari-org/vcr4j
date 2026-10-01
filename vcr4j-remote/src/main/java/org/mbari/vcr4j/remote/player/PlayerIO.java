/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.remote.player;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
    private volatile DatagramSocket server;
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
        var bytes = msg.getBytes(StandardCharsets.UTF_8);
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


    /**
     * Best effort to find the command in a message that could not be parsed.
     * @return The command, or null if it can't be determined
     */
    private static String findCommand(String msg) {
        try {
            var json = JsonParser.parseString(msg);
            if (json.isJsonObject()) {
                var command = json.getAsJsonObject().get("command");
                if (command != null && command.isJsonPrimitive() && command.getAsJsonPrimitive().isString()) {
                    return command.getAsString();
                }
            }
        }
        catch (Exception e) {
            // fall through
        }
        return null;
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
                String msg;
                try {
                    // The length of a reused packet shrinks to the size of the last message
                    // received. Reset it, or larger messages would be silently truncated.
                    packet.setLength(buffer.length);
                    server.receive(packet);
                    msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    log.log(System.Logger.Level.DEBUG, connectionId + " - Received command <<< " + msg);
                }
                catch (Exception e) {
                    log.log(System.Logger.Level.DEBUG, connectionId + " - Error while reading UDP datagram", e);
                    continue;
                }

                var address = packet.getAddress();
                var senderPort = packet.getPort();
                try {
                    var simpleRequest = RVideoIO.GSON.fromJson(msg, SimpleRequest.class);
                    if (simpleRequest == null) {
                        simpleRequest = new SimpleRequest(null);
                    }
                    simpleRequest.setRaw(msg);
                    simpleRequest.setSender(address);
                    handleRequest(simpleRequest, address, senderPort);
                }
                catch (JsonParseException e) {
                    // Not JSON, or the structure is wrong. The protocol requires a reply.
                    log.log(System.Logger.Level.DEBUG, connectionId + " - Invalid message: " + msg, e);
                    try {
                        respond(RequestHandler.invalidMessage(findCommand(msg)), address, senderPort);
                    }
                    catch (Exception e2) {
                        log.log(System.Logger.Level.ERROR, connectionId + " - Unable to respond to invalid message", e2);
                    }
                }
                catch (Exception e) {
                    log.log(System.Logger.Level.DEBUG, connectionId + " - Error while handling UDP datagram", e);
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
        if (server != null && !server.isClosed()) {
            server.close();
        }
        if (!serverExecutor.isShutdown()) {
            serverExecutor.shutdownNow();
            // Wait for the server loop to actually exit before returning. Callers (e.g.
            // VideoControl.close, RemoteControl.close) then close the request handler,
            // and if the loop is still running it can dispatch to a handler mid-close.
            try {
                if (!serverExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    log.log(System.Logger.Level.WARNING,
                            connectionId + " - Server executor did not terminate within 2s of shutdownNow()");
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }
}
