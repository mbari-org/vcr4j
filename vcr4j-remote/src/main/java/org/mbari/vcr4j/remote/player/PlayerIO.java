package org.mbari.vcr4j.remote.player;

import com.google.gson.Gson;

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class PlayerIO {

    private static final Logger log = LoggerFactory.getLogger(PlayerIO.class);
    private static final Gson gson = RVideoIO.GSON;

    private final int port;
    private final RequestHandler requestHandler;
    private DatagramSocket server;
    private Thread receiverThread;
    private volatile boolean ok = true;

    public PlayerIO(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        init();
    }

    private void init() {
        try {
            server = new DatagramSocket(port);
            receiverThread = buildReceiverThread();
            receiverThread.setDaemon(true);
            receiverThread.start();
        }
        catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .log("Failed to initialize UDP socket");
        }

    }




    private void respond(RResponse response, InetAddress address, int port) throws IOException {
        var bytes = gson.toJson(response).getBytes();
        var responsePacket = new DatagramPacket(bytes, bytes.length, address, port);
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

    private void handleConnectRequest(SimpleRequest request)

    private void handleError(SimpleRequest simpleRequest,
                             InetAddress address,
                             int port,
                             Exception ex) {
        var response = ex == null ? requestHandler.handleError(simpleRequest)
                : requestHandler.handleError(simpleRequest, ex);
        try {
            respond(response, address, port);
        } catch (IOException e) {
            log.atError()
                    .setCause(e)
                    .log("Unable to send an error response for request: " + simpleRequest.getRaw());
        }
    }


    private Thread buildReceiverThread()  {
        return new Thread(() -> {
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
                    log.debug("Received <<< " + msg);
                    var simpleRequest = RVideoIO.GSON.fromJson(msg, SimpleRequest.class);
                    simpleRequest.setRaw(msg);
                    handleRequest(simpleRequest, packet.getAddress(), packet.getPort());
                }
                catch (Exception e) {
                    log.debug("Error while reading UDP datagram", e);
                }
            }
            if (server != null && !server.isClosed()) {
                server.close();
                server = null;
            }
            log.info("Shutting down UDP server");
        });
    }

    public void close() {
        ok = false;
    }
}