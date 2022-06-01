package org.mbari.vcr4j.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIndex;

import org.mbari.vcr4j.remote.model.VideoInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.UUID;

public class RVideoIO {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create();

    private static final Logger log = LoggerFactory.getLogger(RVideoIO.class);

    public static final double MAX_SHUTTLE_RATE = 8.0;
    public static final double DEFAULT_SHUTTLE_RATE = 3.0;

    private final int port;
    private final InetAddress inetAddress;
    private final UUID uuid;
    private DatagramSocket socket;

    private final Subject<VideoInformation> videoInfoSubject;
    private final Subject<RState> stateSubject;
    private final Subject<RError> errorSubject;
    /**
     * UDP request are done in real time. So we should always add a timestamp to the VideoIndex
     */
    private final Subject<VideoIndex> indexSubject;
    private final Subject<VideoCommand<?>> commandSubject;

    public RVideoIO(UUID uuid, String host, int port) throws UnknownHostException, SocketException {
        this.uuid = uuid;
        this.port = port;
        inetAddress = InetAddress.getByName(host);

        PublishSubject<VideoInformation> s1 = PublishSubject.create();
        videoInfoSubject = s1.toSerialized();
        PublishSubject<RState> s2 = PublishSubject.create();
        stateSubject = s2.toSerialized();
        PublishSubject<RError> s3 = PublishSubject.create();
        errorSubject = s3.toSerialized();
        PublishSubject<VideoIndex> s4 = PublishSubject.create();
        indexSubject = s4.toSerialized();
        PublishSubject<VideoCommand<?>> s5 = PublishSubject.create();
        commandSubject = s5.toSerialized();
    }


    private DatagramSocket getSocket() throws SocketException {
        if ((socket == null) || socket.isClosed() || !socket.isConnected()) {
            socket = new DatagramSocket(0);
            socket.connect(inetAddress, port);
            socket.setSoTimeout(8000);
        }

        return socket;
    }


    public synchronized void sendCommand(DatagramPacket packet,
                                         int sizeBytes, VideoCommand<?> command) {
        try {
            int timeout = (command instanceof OpenCmd) ? 20000 : 1000;
            byte[] msg = new byte[sizeBytes];
            DatagramPacket incomingPacket = new DatagramPacket(msg, msg.length);

            DatagramSocket s = getSocket();
            s.setSoTimeout(timeout);
            s.send(packet);

            if (log.isDebugEnabled()) {
                log.debug(command.toString() + " >>> " + new String(packet.getData()));
            }

            s.receive(incomingPacket);    // blocks until returned on timeout

            int numBytes = incomingPacket.getLength();
            byte[] response = new byte[numBytes];
            System.arraycopy(incomingPacket.getData(), 0, response, 0, numBytes);

            responseParser.parse(command, response);
        } catch (Exception e) {
            // response will be null
            if (log.isErrorEnabled()) {
                log.error("UDP connection failed.", e);
                errorSubject.onNext(new SharktopodaError(true, false, false, Optional.of(command)));
            }
        }

    }

}
