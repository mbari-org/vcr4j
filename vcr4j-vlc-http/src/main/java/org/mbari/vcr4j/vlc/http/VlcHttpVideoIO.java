package org.mbari.vcr4j.vlc.http;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.vlc.http.commands.EndPoints;
import org.mbari.vcr4j.vlc.http.commands.OpenCmd;
import org.mbari.vcr4j.vlc.http.commands.VlcHttpCommands;
import org.mbari.vcr4j.vlc.http.model.PlayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

public class VlcHttpVideoIO implements VideoIO<VlcState, VlcError> {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(10))
            .writeTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .build();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String basicAuth;

    private final UUID uuid; // One VideoIO per video

    private final Subject<PlayList> playListSubject;
    private final Subject<VlcState> stateSubject;
    private final Subject<VlcError> errorSubject;
    private final Subject<VideoIndex> indexSubject;
    private final Subject<VideoCommand> commandSubject;
    private final ResponseParser responseParser;
    private final EndPoints endPoints;

    public VlcHttpVideoIO(String username, String password, int port, UUID uuid) {

        this.uuid = uuid;
        endPoints = new EndPoints(port);

        Base64.Encoder encoder = Base64.getEncoder();
        String auth = username + ":" + password;
        basicAuth = "Basic " + encoder.encodeToString(auth.getBytes());

        PublishSubject<PlayList> s1 = PublishSubject.create();
        playListSubject = s1.toSerialized();
        PublishSubject<VlcState> s2 = PublishSubject.create();
        stateSubject = s2.toSerialized();
        PublishSubject<VlcError> s3 = PublishSubject.create();
        errorSubject = s3.toSerialized();
        PublishSubject<VideoIndex> s4 = PublishSubject.create();
        indexSubject = s4.toSerialized();
        PublishSubject<VideoCommand> s5 = PublishSubject.create();
        commandSubject = s5.toSerialized();

        responseParser = new ResponseParser(stateSubject, errorSubject,
                indexSubject, playListSubject);

        commandSubject.ofType(OpenCmd.class)
                .forEach(this::doOpen);

        commandSubject.filter(cmd -> cmd.equals(VlcHttpCommands.SHOW))
                .forEach(cmd -> doShow());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.PLAY))
                .forEach(cmd -> doPlay());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.PAUSE) || cmd.equals(VideoCommands.STOP))
                .forEach(cmd -> doPause());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.REQUEST_STATUS))
                .forEach(cmd -> doRequestStatus());

        commandSubject.filter(cmd -> cmd.equals(VideoCommands.REQUEST_ELAPSED_TIME)
                || cmd.equals(VideoCommands.REQUEST_INDEX))
                .forEach(this::doRequestIndex);

        commandSubject.ofType(SeekElapsedTimeCmd.class)
                .forEach(this::doSeekElapsedTime);

    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    private void doOpen(OpenCmd cmd) {
        URL url = endPoints.openAndPlay(cmd.getValue());
        executeCommand(url, cmd);
        url = endPoints.pauseIfPlaying();
        executeCommand(url, VideoCommands.PAUSE);
        url = endPoints.seekTo("0");
        executeCommand(url, VideoCommands.);
    }

    private void doShow() {}
    private void doClose() {}
    private void doRequestPlaylist() {}
    private void doPlay() {}
    private void doPause() {}
    private void doRequestStatus() {}
    private void doShuttle() {}
    private void doRequestIndex(VideoCommand cmd) {}
    private void doSeekElapsedTime(SeekElapsedTimeCmd command) {}
    private void doFrameAdvance() {}

    @Override
    public Subject<VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public String getConnectionID() {
        return null;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void close() {

    }

    public Observable<PlayList> getPlayListObservable() {
        return playListSubject;
    }

    @Override
    public Observable<VlcError> getErrorObservable() {
        return errorSubject;
    }

    @Override
    public Observable<VlcState> getStateObservable() {
        return stateSubject;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexSubject;
    }

    private void executeCommand(URL url, VideoCommand command) {
        String responseBody = sendCommand(url, command);
        responseParser.parseAndHandle(command, responseBody);
    }

    private String sendCommand(URL url, VideoCommand command) {
        log.debug(">>> " + command.getName() + " : " + command.getValue());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", basicAuth)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body != null) {
                String b = body.string();
                log.debug("<<< " + b);

                return b;
            }
            else {
                log.error("HTTP GET request to " + url + " did not return a response");
                throw new RuntimeException("No content was returned from " + url);
            }
        }
        catch (IOException e) {
            log.error("HTTP GET request to " + url + " failed", e);
            VlcError error = new VlcError(e, command);
            errorSubject.onNext(error);
            return "";
        }
    }
}
