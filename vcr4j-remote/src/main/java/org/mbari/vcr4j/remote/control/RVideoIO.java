package org.mbari.vcr4j.remote.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;

import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.commands.*;
import org.mbari.vcr4j.remote.control.commands.localization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A {@link VideoIO} for the UPD remote control. This class manages the outbound UDP commands from
 * the {@link RemoteControl} to the {{@link org.mbari.vcr4j.remote.player.VideoControl}}. For general
 * usage don't instantiate this class, use {{@link RemoteControl.Builder}} instead.
 *
 * In practice, this VideoIO controls only one video.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RVideoIO implements VideoIO<RState, RError> {

    private record SizedRequest(RCommand<?, ?> cmd, int size) {}

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create();

    private static final Logger log = LoggerFactory.getLogger(RVideoIO.class);

    public static final double MAX_SHUTTLE_RATE = 8.0;
    public static final double DEFAULT_SHUTTLE_RATE = 3.0;

    private final int port;
    private final InetAddress inetAddress;
    private final UUID uuid;

    // FIX https://github.com/dingosky/Sharktopoda/issues/4
    // private DatagramSocket socket;

    private final Subject<List<? extends VideoInfo>> videoInfoSubject;
    private final Subject<RState> stateSubject;
    private final Subject<RError> errorSubject;
    /**
     * UDP request are done in real time. So we should always add a timestamp to the VideoIndex
     */
    private final Subject<VideoIndex> indexSubject;
    private final Subject<VideoCommand<?>> commandSubject;
    private final Subject<CommandResponse> responseSubject;

    private final RResponseParser responseParser;

    private List<Disposable> disposables = new ArrayList<>();

    private final String connectionId;

    private boolean closed = false;

    public RVideoIO(UUID uuid, String host, int port) throws UnknownHostException, SocketException {
//        Preconditions.checkArgument(uuid != null, "UUID is required");
        this.uuid = uuid;
        this.port = port;
        inetAddress = InetAddress.getByName(host);

        PublishSubject<List<? extends VideoInfo>> s1 = PublishSubject.create();
        videoInfoSubject = s1.toSerialized();
        PublishSubject<RState> s2 = PublishSubject.create();
        stateSubject = s2.toSerialized();
        PublishSubject<RError> s3 = PublishSubject.create();
        errorSubject = s3.toSerialized();
        PublishSubject<VideoIndex> s4 = PublishSubject.create();
        indexSubject = s4.toSerialized();
        PublishSubject<VideoCommand<?>> s5 = PublishSubject.create();
        commandSubject = s5.toSerialized();
        PublishSubject<CommandResponse> s6 = PublishSubject.create();
        responseSubject = s6.toSerialized();
        responseParser = new RResponseParser(uuid, stateSubject, errorSubject, indexSubject, videoInfoSubject);
        connectionId = getConnectionID();
        init();
    }

    private void init() {
        var a = commandSubject.ofType(OpenCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(CloseCmd.class)
                .map(cmd -> new SizedRequest(cmd, 1024))
                .forEach(cmd -> doCommand(cmd.cmd, cmd.size));
        disposables.add(a);

        a = commandSubject.ofType(PlayCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(VideoCommands.class)
                .map(c ->
                    switch (c) {
                        case FAST_FORWARD -> new PlayCmd(uuid, DEFAULT_SHUTTLE_RATE);
                        case PAUSE, STOP -> new PauseCmd(uuid);
                        case PLAY -> new PlayCmd(uuid);
                        case REQUEST_ELAPSED_TIME, REQUEST_INDEX -> new RequestElapsedTimeCmd(uuid);
                        case REQUEST_STATUS -> new RequestPlayerStateCmd(uuid);
                        case REWIND -> new PlayCmd(uuid, -DEFAULT_SHUTTLE_RATE);
                        case REQUEST_DEVICE_TYPE, REQUEST_TIMECODE, REQUEST_TIMESTAMP -> new NoopCmd();
                    })
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(RemoteCommands.class)
                .map(c ->
                    switch (c) {
                        case CLOSE -> new SizedRequest(new CloseCmd(uuid), 1024);
                        case PING -> new SizedRequest(new PingCmd(), 1024);
                        case FRAMEADVANCE -> new SizedRequest(new FrameAdvanceCmd(uuid), 1024);
                        case REQUEST_ALL_VIDEO_INFOS -> new SizedRequest(new RequestAllVideoInfosCmd(), 10240);
                        case REQUEST_VIDEO_INFO -> new SizedRequest(new RequestVideoInfoCmd(), 10240);
                        case SHOW -> new SizedRequest(new ShowCmd(uuid), 1024);
                    })
                .forEach(sizedRequest -> doCommand(sizedRequest.cmd, sizedRequest.size));
        disposables.add(a);

        a = commandSubject.ofType(FrameAdvanceCmd.class)
                .forEach(c -> doCommand(c, 1024));
        disposables.add(a);

        a = commandSubject.ofType(ShuttleCmd.class)
                .map(c -> new PlayCmd(uuid, c.getValue() * MAX_SHUTTLE_RATE))
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(SeekElapsedTimeCmd.class)
                .map(c -> new RSeekElapsedTimeCmd(uuid, c.getValue().toMillis()))
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(FrameCaptureCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(FrameCaptureDoneCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(ConnectCmd.class)
                .map(cmd -> new ConnectCmd(cmd.getValue().getPort(), cmd.getValue().getHost(), uuid))
                .forEach(this::doCommand); 
        disposables.add(a);

        a = commandSubject.ofType(LocalizationsCmd.class)
                .forEach(this::doLocalizationsCommand);
        disposables.add(a);

        // FIX https://github.com/dingosky/Sharktopoda/issues/4
        // try {
        //     socket = new DatagramSocket(0);
        //     socket.connect(inetAddress, port);
        //     socket.setSoTimeout(8000);
        //     log.atInfo().log( "Connected to " + connectionId);
        // }
        // catch (Exception e) {
        //     errorSubject.onNext(new RError(true, false, false, null, null, e));
        // }
    }


    private void doCommand(RCommand<?, ?> cmd) {
        if (!(cmd instanceof NoopCmd)) {
            doCommand(cmd, 1024);
        }
    }

    private void doCommand(RCommand<?, ?> cmd, int responseSize) {
        var packet = asPacket(cmd);
        sendCommand(packet, responseSize, cmd);
    }

    private void doLocalizationsCommand(LocalizationsCmd<?, ?> command) {

        if (command instanceof ClearLocalizationsCmd cmd) {
            doCommand(cmd);
        }
        else if (command instanceof SelectLocalizationsCmd cmd) {
            doCommand(cmd);
        }
        else if (command instanceof AddLocalizationsCmd cmd){
            cmd.groupedPayload(10)
                    .stream()
                    .map(xs -> new AddLocalizationsCmd(cmd.getValue().getUuid(), xs))
                    .forEach(this::doCommand);
        }
        else if (command instanceof RemoveLocalizationsCmd cmd) {
            cmd.groupedPayload(10)
                    .stream()
                    .map(xs -> new RemoveLocalizationsCmd(cmd.getValue().getUuid(), xs))
                    .forEach(this::doCommand);
        }
        else if (command instanceof UpdateLocalizationsCmd cmd) {
            cmd.groupedPayload(10)
                    .stream()
                    .map(xs -> new UpdateLocalizationsCmd(cmd.getValue().getUuid(), xs))
                    .forEach(this::doCommand);
        }
    }


    /**
     * The UUID of the video that is being controlled.
     * @return
     */
    public UUID getUuid() {
        return uuid;
    }


    @Override
    public <A extends VideoCommand<?>> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand<?>> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public String getConnectionID() {
        var id = uuid == null ? "server" : uuid.toString();
        return id + "@" + inetAddress.getCanonicalHostName() + ":" + port ;
    }

    @Override
    public void close() {
        // FIX https://github.com/dingosky/Sharktopoda/issues/4
//        if (socket != null && (!socket.isClosed() || socket.isConnected())) {
        //     log.atInfo().log("Disconnecting from " + connectionId );
        //     socket.close();
        // }
        // socket = null;
        disposables.forEach(Disposable::dispose);
        commandSubject.onComplete();
        indexSubject.onComplete();
        errorSubject.onComplete();
        stateSubject.onComplete();
        videoInfoSubject.onComplete();
        closed = true;
    }

    public boolean isClosed() {
        // FIX https://github.com/dingosky/Sharktopoda/issues/4
        // return socket == null || socket.isClosed();
        return closed;
    }

    @Override
    public Observable<RError> getErrorObservable() {
        return errorSubject;
    }

    protected Subject<RError> getErrorSubject() {
        return errorSubject;
    }

    @Override
    public Observable<RState> getStateObservable() {
        return stateSubject;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexSubject;
    }

    public Observable<List<? extends VideoInfo>> getVideoInfoObservable() {
        return videoInfoSubject;
    }

    public Observable<CommandResponse> getResponseSubject() {
        return responseSubject;
    }

    private synchronized void sendCommand(DatagramPacket packet,
                                          int sizeBytes, RCommand<?, ?> command) {
                                            
        // FIX https://github.com/dingosky/Sharktopoda/issues/4                       
        try(var socket = new DatagramSocket()) {
        // try {
            int timeout = (command instanceof OpenCmd) ? 20000 : 1000;
            socket.setSoTimeout(timeout);

            var incomingBytes = new byte[sizeBytes];
            var incomingPacket = new DatagramPacket(incomingBytes, incomingBytes.length);

            socket.send(packet);

            if (log.isDebugEnabled()) { // && command.getName().contains("localization")) {
                log.debug(connectionId + " - Sending command >>> " + new String(packet.getData()));
            }

            socket.receive(incomingPacket);    // blocks until returned on timeout

            int numBytes = incomingPacket.getLength();
            var response = new String(incomingBytes, 0, numBytes, StandardCharsets.UTF_8);

            if (log.isDebugEnabled()) { // && command.getName().contains("localization")) {
                log.debug(connectionId + " - Received response <<< " + response);
            }

            var opt = responseParser.handle(command, response);
            opt.map(r -> new CommandResponse(command, r))
                    .ifPresent(responseSubject::onNext);
        } catch (Exception e) {
            // response will be null
            if (log.isErrorEnabled()) {
                log.error(connectionId + " - UDP connection failed", e);
                errorSubject.onNext(new RError(true, false, false, command));
            }
        }

    }

    public DatagramPacket asPacket(RCommand<?, ?> cmd) {
        byte[] b = RVideoIO.GSON.toJson(cmd.getValue()).getBytes();
        return new DatagramPacket(b, b.length, inetAddress, port);
    }


    

}
