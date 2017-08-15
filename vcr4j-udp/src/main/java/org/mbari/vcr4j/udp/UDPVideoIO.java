package org.mbari.vcr4j.udp;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.time.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-02-04T13:09:00
 */
public class UDPVideoIO implements VideoIO<UDPState, UDPError> {

    private final int port;
    private final InetAddress inetAddress;
    private final DatagramPacket incomingPacket;
    private final DatagramPacket requestTimecodePacket;
    private DatagramSocket socket;
    private byte[] receiveMessage = new byte[1024];
    public static final byte[] GET_TIMECODE = "ltc.".getBytes();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Optional<VideoCommand> timecodeRequest = Optional.of(VideoCommands.REQUEST_TIMECODE);
    UDPResponseParser responseParser = new UDPResponseParser();
    private final Subject<UDPState> stateSubject;

    /**
     * UDP request are done in real time. So we should always add a timestamp to the VideoIndex
     */
    private final Observable<VideoIndex> indexObservable = responseParser.getTimecodeObservable()
            .map(tc -> new VideoIndex(Optional.of(Instant.now()), Optional.empty(), Optional.of(tc)));

    private final Subject<VideoCommand> commandSubject;


    public UDPVideoIO(String host, int port) throws UnknownHostException, SocketException {
        this.port = port;

        PublishSubject<UDPState>  s1 = PublishSubject.create();
        stateSubject = s1.toSerialized();
        PublishSubject<VideoCommand> s2 = PublishSubject.create();
        commandSubject = s2.toSerialized();

        inetAddress = InetAddress.getByName(host);
        incomingPacket = new DatagramPacket(receiveMessage, receiveMessage.length);
        requestTimecodePacket = new DatagramPacket(GET_TIMECODE,
                GET_TIMECODE.length,
                inetAddress,
                port);

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_TIMECODE) || vc.equals(VideoCommands.REQUEST_INDEX))
                .subscribe(vc -> sendCommand(requestTimecodePacket));

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_STATUS))
                .subscribe(vc -> {
                    UDPState state = (socket != null && !socket.isClosed()) ? UDPState.RECORDING : UDPState.STOPPED;
                    stateSubject.onNext(state);
                });
    }


    private synchronized void sendCommand(DatagramPacket packet) {
        try {
            DatagramSocket s = getSocket();

            s.send(packet);
            s.receive(incomingPacket);    // blocks until returned on timeout

            int numBytes = incomingPacket.getLength();

            byte[] response = new byte[numBytes];
            System.arraycopy(incomingPacket.getData(), 0, response, 0, numBytes);
            responseParser.update(response, timecodeRequest);

        }
        catch (Exception e) {
            // response will be null
            if (log.isErrorEnabled()) {
                log.error("UDP connection failed.", e);
                responseParser.getErrorObservable()
                        .onNext(new UDPError(true, false, timecodeRequest));
            }
        }

    }

    @Override
    public void close() {

        if (socket != null && !socket.isClosed()) {
            log.info("Closing UDP port: " + getConnectionID());
            socket.close();
        }
        stateSubject.onNext(UDPState.STOPPED);
        commandSubject.onComplete();
        stateSubject.onComplete();
        responseParser.getErrorObservable().onComplete();
        responseParser.getTimecodeObservable().onComplete();

    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public String getConnectionID() {
        String connectionName = "Not connected";
        try {
            DatagramSocket s = getSocket();
            connectionName = s.getInetAddress().getHostName() + ":" + s.getPort();
        }
        catch (SocketException ex) {
            log.error("Failed to open a DatagramSocket", ex);
            responseParser.getErrorObservable()
                    .onNext(new UDPError(true, false, Optional.empty()));
        }

        return connectionName;
    }

    private DatagramSocket getSocket() throws SocketException {
        if ((socket == null) || socket.isClosed() || !socket.isConnected()) {
            socket = new DatagramSocket(0);
            socket.connect(inetAddress, port);
            socket.setSoTimeout(4000);
        }

        return socket;
    }

    @Override
    protected void finalize() throws Throwable {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        super.finalize();
    }

    @Override
    public Observable<UDPError> getErrorObservable() {
        return responseParser.getErrorObservable();
    }

    @Override
    public Observable<UDPState> getStateObservable() {
        return stateSubject;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    public Observable<Timecode> getTimecodeObservable() {
        return responseParser.getTimecodeObservable();
    }

    public InetAddress getInetAddress() {
        return requestTimecodePacket.getAddress();
    }

    public int getPort() {
        return requestTimecodePacket.getPort();
    }


}
