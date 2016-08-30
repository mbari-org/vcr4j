package org.mbari.vcr4j.sharktopoda;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.mbari.vcr4j.sharktopoda.model.request.Close;
import org.mbari.vcr4j.sharktopoda.model.request.Open;
import org.mbari.vcr4j.sharktopoda.model.request.Pause;
import org.mbari.vcr4j.sharktopoda.model.request.Play;
import org.mbari.vcr4j.sharktopoda.model.request.RequestElapsedTime;
import org.mbari.vcr4j.sharktopoda.model.request.RequestStatus;
import org.mbari.vcr4j.sharktopoda.model.request.RequestVideoInfo;
import org.mbari.vcr4j.sharktopoda.model.response.FramecaptureResponse;
import org.mbari.vcr4j.sharktopoda.model.response.IVideoInfo;
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

    public static final double MAX_SHUTTLE_RATE = 8.0;
    public static final double DEFAULT_SHUTTLE_RATE = 3.0;

    private final int port;
    private final InetAddress inetAddress;
    private final UUID uuid; // One VideoIO to one window in Sharktopoda
    private DatagramSocket socket;

    private final Subject<FramecaptureResponse, FramecaptureResponse>  framecaptureSubject = new SerializedSubject<>(PublishSubject.create());
    private final Subject<IVideoInfo, IVideoInfo> videoInfoSubject = new SerializedSubject<>(PublishSubject.create());
    private final Subject<SharktopodaState, SharktopodaState> stateSubject = new SerializedSubject<>(PublishSubject.create());
    private final Subject<SharktopodaError, SharktopodaError> errorSubject = new SerializedSubject<>(PublishSubject.create());
    /**
     * UDP request are done in real time. So we should always add a timestamp to the VideoIndex
     */
    private final Subject<VideoIndex, VideoIndex> indexSubject = new SerializedSubject<>(PublishSubject.create());
    private final Subject<VideoCommand, VideoCommand> commandSubject = new SerializedSubject<>(PublishSubject.create());
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SharktopodaResponseParser responseParser;

    // TODO add timeout for opening movies to the constructor
    public SharktopodaVideoIO(UUID uuid, String host, int port) throws UnknownHostException, SocketException {
        this.uuid = uuid;
        this.port = port;
        inetAddress = InetAddress.getByName(host);

        responseParser = new SharktopodaResponseParser(uuid,
                stateSubject, errorSubject, indexSubject, videoInfoSubject, framecaptureSubject);

        commandSubject.ofType(OpenCmd.class)
                .forEach(this::doOpen);

        commandSubject.filter(cmd -> cmd.equals(SharkCommands.CLOSE))
                .forEach(cmd -> doClose());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.PLAY))
                .forEach(cmd -> doPlay());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.PAUSE) || cmd.equals(VideoCommands.STOP))
                .forEach(cmd -> doPause());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.REQUEST_STATUS))
                .forEach(cmd -> doRequestStatus());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.FAST_FORWARD))
                .forEach(cmd -> doShuttle(DEFAULT_SHUTTLE_RATE, cmd));

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.REWIND))
                .forEach(cmd -> doShuttle(-DEFAULT_SHUTTLE_RATE, cmd));

        commandSubject.ofType(ShuttleCmd.class)
                .forEach(cmd -> doShuttle(cmd.getValue() * MAX_SHUTTLE_RATE, cmd));

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.REQUEST_ELAPSED_TIME)
                    || cmd.equals(VideoCommands.REQUEST_INDEX))
                .forEach(this::doRequestIndex);

        commandSubject.filter(cmd -> cmd.equals(SharkCommands.REQUEST_VIDEO_INFO))
                .forEach(cmd -> doRequestVideoInfo());

    }

    private DatagramSocket getSocket() throws SocketException {
        if ((socket == null) || socket.isClosed() || !socket.isConnected()) {
            socket = new DatagramSocket(0);
            socket.connect(inetAddress, port);
            socket.setSoTimeout(8000);
        }

        return socket;
    }

    private synchronized void sendCommandAndListenForResponse(DatagramPacket packet,
            int sizeBytes, VideoCommand command) {
        try {
            int timeout = (command instanceof OpenCmd) ? 20000 : 1000;
            byte[] msg = new byte[sizeBytes];
            DatagramPacket incomingPacket = new DatagramPacket(msg, msg.length);

            DatagramSocket s = getSocket();
            s.setSoTimeout(timeout);
            s.send(packet);

            if (log.isDebugEnabled()) {
                log.debug(command.toString() + " -> " + new String(packet.getData()));
            }

            s.receive(incomingPacket);    // blocks until returned on timeout

            int numBytes = incomingPacket.getLength();
            byte[] response = new byte[numBytes];
            System.arraycopy(incomingPacket.getData(), 0, response, 0, numBytes);

            responseParser.parse(command, response);
        }
        catch (Exception e) {
            // response will be null
            if (log.isErrorEnabled()) {
                log.error("UDP connection failed.", e);
                errorSubject.onNext(new SharktopodaError(true, false, false, Optional.of(command)));
            }
        }

    }

    private synchronized void sendCommand(DatagramPacket packet, VideoCommand command) {
        try {
            DatagramSocket s = getSocket();
            s.send(packet);
            if (log.isDebugEnabled()) {
                log.debug(command.toString() + " -> " + new String(packet.getData()));
            }
        }
        catch (Exception e) {
            // response will be null
            if (log.isErrorEnabled()) {
                log.error("UDP connection failed.", e);
                errorSubject.onNext(new SharktopodaError(true, false, false, Optional.of(command)));
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
        return uuid.toString() + "@" + inetAddress.getCanonicalHostName() + ":" + port ;
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

    /**
     * Tracks information about what videos are open.
     * @return
     */
    public Subject<IVideoInfo, IVideoInfo> getVideoInfoSubject() {
        return videoInfoSubject;
    }

    /**
     * Tracks information about framecaptures
     * @return
     */
    public Subject<FramecaptureResponse, FramecaptureResponse> getFramecaptureSubject() {
        return framecaptureSubject;
    }

    private DatagramPacket asPacket(Object cmd) {
        byte[] b = Constants.GSON.toJson(cmd).getBytes();
        return new DatagramPacket(b, b.length, inetAddress, port);
    }

    private void doOpen(OpenCmd cmd) {
        Open obj = new Open(cmd.getValue(), uuid);
        DatagramPacket packet = asPacket(obj);
        sendCommandAndListenForResponse(packet, 1024, cmd);
    }

    private void doClose() {
        Close obj = new Close(uuid);
        DatagramPacket packet = asPacket(obj);
        sendCommand(packet, SharkCommands.CLOSE);
    }

    private void doPlay() {
        Play obj = new Play(uuid, 1.0);
        DatagramPacket packet = asPacket(obj);
        sendCommandAndListenForResponse(packet, 1024, VideoCommands.PLAY);
    }

    private void doPause() {
        Pause obj = new Pause(uuid);
        DatagramPacket packet = asPacket(obj);
        sendCommandAndListenForResponse(packet, 1024, VideoCommands.PAUSE);
    }

    private void doRequestStatus() {
        RequestStatus obj = new RequestStatus(uuid);
        DatagramPacket packet = asPacket(obj);
        sendCommandAndListenForResponse(packet, 1024, VideoCommands.REQUEST_STATUS);
    }

    private void doShuttle(double rate, VideoCommand command) {
        Play obj = new Play(uuid, rate);
        DatagramPacket packet = asPacket(obj);
        sendCommandAndListenForResponse(packet, 1024, command);
    }

    private void doRequestIndex(VideoCommand command) {
        RequestElapsedTime obj = new RequestElapsedTime(uuid);
        DatagramPacket packet = asPacket(obj);
        sendCommandAndListenForResponse(packet, 1024, command);
    }

    private void doRequestVideoInfo() {
        RequestVideoInfo obj = new RequestVideoInfo(uuid);
        DatagramPacket packet = asPacket(obj);
        sendCommandAndListenForResponse(packet, 2048, SharkCommands.REQUEST_VIDEO_INFO);
    }
}
