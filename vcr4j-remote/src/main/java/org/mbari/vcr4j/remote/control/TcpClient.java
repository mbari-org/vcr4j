package org.mbari.vcr4j.remote.control;

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
import org.mbari.vcr4j.remote.control.commands.loc.LocalizationsCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClient implements VideoIO<RState, RError> {

    private static final Logger log = LoggerFactory.getLogger(TcpClient.class);

    public static final double MAX_SHUTTLE_RATE = 8.0;
    public static final double DEFAULT_SHUTTLE_RATE = 3.0;

    private final int port;
    private final UUID uuid;

    private final String secret;

    private final URI serverUri;

    private final Subject<List<VideoInfo>> videoInfoSubject;
    private final Subject<RState> stateSubject;
    private final Subject<RError> errorSubject;
    /**
     * UDP request are done in real time. So we should always add a timestamp to the VideoIndex
     */
    private final Subject<VideoIndex> indexSubject;
    private final Subject<VideoCommand<?>> commandSubject;

    private final RResponseParser responseParser;

    private List<Disposable> disposables = new ArrayList<>();
    private final String connectionId;

    private final HttpClient httpClient;
    private final ExecutorService httpExecutor;
    private boolean isClosed = false;

    public TcpClient(UUID uuid, String host, int port, String secret) throws UnknownHostException {
        this.uuid = uuid;
        this.port = port;
        this.secret = secret;
        var inetAddress = InetAddress.getByName(host);
        serverUri = URI.create("http://" + inetAddress.getHostName() + ":" + port);

        httpExecutor = Executors.newSingleThreadExecutor();
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5L))
                .followRedirects(HttpClient.Redirect.NEVER)
                .executor(httpExecutor)
                .build();

        PublishSubject<List<VideoInfo>> s1 = PublishSubject.create();
        videoInfoSubject = s1.toSerialized();
        PublishSubject<RState> s2 = PublishSubject.create();
        stateSubject = s2.toSerialized();
        PublishSubject<RError> s3 = PublishSubject.create();
        errorSubject = s3.toSerialized();
        PublishSubject<VideoIndex> s4 = PublishSubject.create();
        indexSubject = s4.toSerialized();
        PublishSubject<VideoCommand<?>> s5 = PublishSubject.create();
        commandSubject = s5.toSerialized();
        responseParser = new RResponseParser(uuid, stateSubject, errorSubject, indexSubject, videoInfoSubject);
        connectionId = getConnectionID();
    }

    private void init() {
        var a = commandSubject.ofType(OpenCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(VideoCommands.class)
                .map(c ->
                        switch (c) {
                            case FAST_FORWARD -> new PlayCmd(uuid, DEFAULT_SHUTTLE_RATE);
                            case PAUSE, STOP -> new PauseCmd(uuid);
                            case PLAY -> new PlayCmd(uuid);
                            case REQUEST_ELAPSED_TIME, REQUEST_INDEX -> new RequestElapsedTimeCmd(uuid);
                            case REQUEST_STATUS -> new RequestStatusCmd(uuid);
                            case REWIND -> new PlayCmd(uuid, -DEFAULT_SHUTTLE_RATE);
                            case REQUEST_DEVICE_TYPE, REQUEST_TIMECODE, REQUEST_TIMESTAMP -> new NoopCmd();
                        })
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(RemoteCommands.class)
                .map(c ->
                        switch (c) {
                            case CLOSE -> new CloseCmd(uuid);
                            case FRAMEADVANCE -> new FrameAdvanceCmd(uuid);
                            case REQUEST_ALL_VIDEO_INFOS -> new RequestAllVideoInfosCmd();
                            case REQUEST_VIDEO_INFO -> new RequestVideoInfoCmd();
                            case SHOW -> new ShowCmd(uuid);
                        })
                .forEach(this::doCommand);
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

        a = commandSubject.ofType(ConnectCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(LocalizationsCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

        a = commandSubject.ofType(OpenCmd.class)
                .forEach(this::doCommand);
        disposables.add(a);

    }

    private void doCommand(RCommand<?, ?> cmd) {
        if (!(cmd instanceof NoopCmd)) {
            var body = RVideoIO.GSON.toJson(cmd.getValue());
            var request = HttpRequest.newBuilder()
                    .uri(serverUri)
                    .header("X-API-Key", secret)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            try {
                var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                responseParser.handle(cmd, response.body());
            }
            catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(connectionId + " - TCP connection failed", e);
                    errorSubject.onNext(new RError(true, false, false, cmd));
                }
            }
        }
    }


    @Override
    public String getConnectionID() {
        return null;
    }

    @Override
    public void close() {
        isClosed = true;
        httpExecutor.shutdown();
        disposables.forEach(Disposable::dispose);
    }

    public boolean isClosed() {
        return isClosed;
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

    public Observable<List<VideoInfo>> getVideoInfoObservable() {
        return videoInfoSubject;
    }
    @Override
    public <A extends VideoCommand<?>> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand<?>> getCommandSubject() {
        return commandSubject;
    }
}
