package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-25T16:37:00
 */
public class SharktopodaVideoIO implements VideoIO<SharktopodaState, SharktopodaError> {

    private final int port;
    private final InetAddress inetAddress;
    private final UUID uuid; // ONe VideoIO to one window in Sharktopoda
    private DatagramSocket socket;

    private final Subject<SharktopodaState, SharktopodaState> stateSubject = new SerializedSubject<>(PublishSubject.create());
    private final Subject<SharktopodaError, SharktopodaError> errorSubject = new SerializedSubject<>(PublishSubject.create());
    /**
     * UDP request are done in real time. So we should always add a timestamp to the VideoIndex
     */
    private final Subject<VideoIndex, VideoIndex> indexSubject = new SerializedSubject<>(PublishSubject.create());
    private final Subject<VideoCommand, VideoCommand> commandSubject = new SerializedSubject<>(PublishSubject.create());
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SharktopodaResponseParser responseParser = new SharktopodaResponseParser(stateSubject, errorSubject, indexSubject);

    public SharktopodaVideoIO(UUID uuid, String host, int port) throws UnknownHostException, SocketException {
        this.uuid = uuid;
        this.port = port;
        inetAddress = InetAddress.getByName(host);
    }

    private DatagramSocket getSocket() throws SocketException {
        if ((socket == null) || socket.isClosed() || !socket.isConnected()) {
            socket = new DatagramSocket(0);
            socket.connect(inetAddress, port);
            socket.setSoTimeout(4000);
        }

        return socket;
    }

    private synchronized void sendCommandAndListenForResponse(DatagramPacket packet,
            int sizeBytes, Optional<VideoCommand> command) {
        try {
            DatagramSocket s = getSocket();
            s.send(packet);

            byte[] msg = new byte[sizeBytes];
            DatagramPacket incomingPacket = new DatagramPacket(msg, msg.length);
            s.receive(incomingPacket);    // blocks until returned on timeout

            int numBytes = incomingPacket.getLength();
            byte[] response = new byte[numBytes];
            System.arraycopy(incomingPacket.getData(), 0, response, 0, numBytes);

            responseParser.parse(response);
        }
        catch (Exception e) {
            // response will be null
            if (log.isErrorEnabled()) {
                log.error("UDP connection failed.", e);
                errorSubject.onNext(new SharktopodaError(true, false, command));
            }
        }

    }

    private synchronized void sendCommand(DatagramPacket packet, Optional<VideoCommand> command) {
        try {
            DatagramSocket s = getSocket();
            s.send(packet);
        }
        catch (Exception e) {
            // response will be null
            if (log.isErrorEnabled()) {
                log.error("UDP connection failed.", e);
                errorSubject.onNext(new SharktopodaError(true, false, command));
            }
        }
    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand, VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public String getConnectionID() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public Observable<SharktopodaError> getErrorObservable() {
        return errorSubject;
    }

    @Override
    public Observable<SharktopodaState> getStateObservable() {
        return stateSubject;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexSubject;
    }
}
