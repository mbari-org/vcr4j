package org.mbari.vcr4j.vlc.http;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;

import java.io.IOException;
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

    public VlcHttpVideoIO(int port, UUID uuid) {
        this.port = port;
        this.uuid = uuid;

        PublishSubject<VlcState> s2 = PublishSubject.create();
        stateSubject = s2.toSerialized();
        PublishSubject<VlcError> s3 = PublishSubject.create();
        errorSubject = s3.toSerialized();
        PublishSubject<VideoIndex> s4 = PublishSubject.create();
        indexSubject = s4.toSerialized();
        PublishSubject<VideoCommand> s5 = PublishSubject.create();
        commandSubject = s5.toSerialized();

        responseParser = new ResponseParser(stateSubject, errorSubject, indexSubject);
    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {

    }

    private void doOpen() {}
    private void doShow() {}
    private void doClose() {}
    private void doRequestAllVideoInfos() {}
    private void doRequestVideoInfo() {}
    private void doPlay() {}
    private void doPause() {}
    private void doRequestStatus() {}
    private void doShuttle() {}
    private void doRequestIndex() {}
    private void doSeekElapsedTime() {}
    private void doFrameAdvance() {}

    @Override
    public Subject<VideoCommand> getCommandSubject() {
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

    private synchronized void sendCommandAndListenForResponse(URL url, VideoCommand command) {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {

        }
        catch (IOException e) {
            // TODO set error state
        }
    }
}
