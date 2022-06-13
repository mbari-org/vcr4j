package org.mbari.vcr4j.remote.player;

import com.google.gson.Gson;

import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.control.commands.loc.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.ClearLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.RemoveLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.UpdateLocalizationsCmd;
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
    private final Player player;
    private DatagramSocket server;
    private Thread receiverThread;
    private volatile boolean ok = true;

    public PlayerIO(int port, Player player) {
        this.port = port;
        this.player = player;
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

    private RResponse composeResponse(SimpleRequest simpleRequest) {

        var clazz = switch (simpleRequest.getCommand()) {
            case FrameCaptureCmd.Command ->  FrameCaptureCmd.Request.class;
            case FrameCaptureDoneCmd.Command -> FrameCaptureDoneCmd.Request.class;
            case AddLocalizationsCmd.Command -> AddLocalizationsCmd.Request.class;
            case ClearLocalizationsCmd.Command -> ClearLocalizationsCmd.Request.class;
            case RemoveLocalizationsCmd.Command -> RemoveLocalizationsCmd.Request.class;
            case UpdateLocalizationsCmd.Command -> UpdateLocalizationsCmd.Request.class;
            default -> NoopCmd.Request.class;
        };
        var request = gson.fromJson(simpleRequest.getRaw(), clazz);

        var response = switch (simpleRequest.getCommand()) {
            case FrameCaptureCmd.Command ->  player.handleFrameCaptureRequest((FrameCaptureCmd.Request) request);
            case FrameCaptureDoneCmd.Command -> player.handleFrameCaptureDoneRequest((FrameCaptureDoneCmd.Request) request);
            case AddLocalizationsCmd.Command -> player.handleAddLocalizationsRequest((AddLocalizationsCmd.Request) request);
            case ClearLocalizationsCmd.Command -> player.handleClearLocalizationsRequest((ClearLocalizationsCmd.Request) request);
            case RemoveLocalizationsCmd.Command -> player.handleDeleteLocalizationsRequest((RemoveLocalizationsCmd.Request) request);
            case UpdateLocalizationsCmd.Command -> player.handleUpdateLocalizationsRequest((UpdateLocalizationsCmd.Request) request);
            default -> player.handleError(simpleRequest);
        };
        return response;
    }

    private void respond(RResponse response, InetAddress address, int port) throws IOException {
        var bytes = gson.toJson(response).getBytes();
        var responsePacket = new DatagramPacket(bytes, bytes.length, address, port);
        server.send(responsePacket);
    }

    private void handleRequest(SimpleRequest request, InetAddress address, int port) {
        try {
            var response = composeResponse(request);
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
        var response = ex == null ? player.handleError(simpleRequest)
                : player.handleError(simpleRequest, ex);
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
                    var requestOrResponse = RVideoIO.GSON.fromJson(msg, SimpleRequest.class);
                    requestOrResponse.setRaw(msg);
                    handleRequest(requestOrResponse, packet.getAddress(), packet.getPort());
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
