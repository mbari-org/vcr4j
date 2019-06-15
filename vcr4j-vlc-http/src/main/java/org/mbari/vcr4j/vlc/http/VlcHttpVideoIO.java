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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class VlcHttpVideoIO implements VideoIO<VlcState, VlcError> {


    private final OkHttpClient client = new OkHttpClient();

    private final int port;
    private final UUID uuid; // One VideoIO per video

    private final Subject<VlcState> stateSubject;
    private final Subject<VlcError> errorSubject;
    private final Subject<VideoIndex> indexSubject;
    private final Subject<VideoCommand> commandSubject;
    private final ResponseParser responseParser;
    private final EndPoints endPoints;

    public VlcHttpVideoIO(int port, UUID uuid) {
        this.port = port;
        this.uuid = uuid;
        endPoints = new EndPoints(port);

        PublishSubject<VlcState> s2 = PublishSubject.create();
        stateSubject = s2.toSerialized();
        PublishSubject<VlcError> s3 = PublishSubject.create();
        errorSubject = s3.toSerialized();
        PublishSubject<VideoIndex> s4 = PublishSubject.create();
        indexSubject = s4.toSerialized();
        PublishSubject<VideoCommand> s5 = PublishSubject.create();
        commandSubject = s5.toSerialized();

        responseParser = new ResponseParser(stateSubject, errorSubject, indexSubject);

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

    }
    private void doShow() {}
    private void doClose() {}
    private void doRequestAllVideoInfos() {}
    private void doRequestVideoInfo() {}
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

    @Override
    public Observable<VlcError> getErrorObservable() {
        return null;
    }

    @Override
    public Observable<VlcState> getStateObservable() {
        return null;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return null;
    }

    private String sendCommandAndListenForResponse(URL url, VideoCommand command) {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            }
            else {
                throw new RuntimeException("No content was returned from " + url);
            }
        }
        catch (IOException e) {
            // TODO set error state
            return "[]";
        }
    }
}
