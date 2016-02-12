package org.mbari.vcr4j.kipro;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.kipro.json.ConfigEvent;
import org.mbari.vcr4j.kipro.json.ConnectionID;
import org.mbari.vcr4j.kipro.json.Constants;
import org.mbari.vcr4j.time.Timecode;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Brian Schlining
 * @since 2016-02-04T11:16:00
 */
public class QuadVideoIO implements VideoIO<QuadState, QuadError> {

    /** Always ends with "/" */
    private final String httpAddress;
    private final AtomicInteger connectionID = new AtomicInteger(0);


    private final Subject<QuadError, QuadError> errorObservable = new SerializedSubject<>(PublishSubject.create());
    private final Subject<QuadState, QuadState> statusObservable = new SerializedSubject<>(PublishSubject.create());
    private final Subject<Timecode, Timecode> timecodeObservable =
            new SerializedSubject<>(PublishSubject.create());
    private final Subject<VideoIndex, VideoIndex> indexObservable =
            new SerializedSubject<>(PublishSubject.create());

    private final Subject<VideoCommand, VideoCommand> commandSubject = new SerializedSubject<>(PublishSubject.create());

    public QuadVideoIO(String httpAddress) {
        this.httpAddress = httpAddress.endsWith("/") ? httpAddress : httpAddress + "/";
        statusObservable.map(QuadState::getConnectionID)
                .forEach(connectionID::set);

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_INDEX) || vc.equals(VideoCommands.REQUEST_TIMECODE))
                .forEach(vc -> requestTimecode());

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_TIMESTAMP))
                .forEach(vc -> {
                    VideoIndex videoIndex = new VideoIndex(Optional.of(Instant.now()), Optional.empty(), Optional.empty());
                    indexObservable.onNext(videoIndex);
                });

        timecodeObservable.subscribe(tc -> {
            VideoIndex videoIndex = new VideoIndex(Optional.of(Instant.now()), Optional.empty(), Optional.of(tc));
            indexObservable.onNext(videoIndex);
        });

    }

    public static QuadVideoIO open(String httpAddress) {
        QuadVideoIO quadVideoIO = new QuadVideoIO(httpAddress);
        quadVideoIO.connect();
        return quadVideoIO;
    }

    @Override
    public void close() {

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
        return httpAddress;
    }

    @Override
    public Observable<QuadError> getErrorObservable() {
        return errorObservable;
    }

    @Override
    public Observable<QuadState> getStateObservable() {
        return statusObservable;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    public void connect() {
        String request = ConnectionID.buildRequest(httpAddress);
        sendRequestAndCheckError(request).ifPresent(s -> {
            ConnectionID id = ConnectionID.fromJSON(s);
            statusObservable.onNext(new QuadState(id.getConnectionid()));
        });
    }

    public void requestTimecode() {
        int cid = connectionID.get();
        String request = ConfigEvent.buildRequest(httpAddress, cid);
        Optional<String> response = sendRequestAndCheckError(request);
        AtomicBoolean ok = new AtomicBoolean(false);
        if (response.isPresent()) {
            ConfigEvent[] events = ConfigEvent.fromJSON(response.get());
            Optional<Timecode> timecode = ConfigEvent.toTimecode(events);
            timecode.ifPresent(tc -> {
                ok.set(true);
                timecodeObservable.onNext(tc);
            });
        }

        if (!ok.get()) {
            QuadError error = new QuadError(true, false, Optional.empty(), Optional.empty());
            errorObservable.onNext(error);
        }
    }

    private Optional<String> sendRequestAndCheckError(String request) {
        Optional<String> json;
        try {
            json = Optional.of(sendRequest(request));
        }
        catch (Exception e) {
            QuadError error = new QuadError(true, true, Optional.empty(), Optional.of(e));
            errorObservable.onNext(error);
            json = Optional.empty();
        }
        return json;
    }

    public static String sendRequest(String request) {
        System.out.println("REQUEST: " + request);
        try {
            HttpResponse<String> response = Unirest.get(request)
                    .header("Accept", "application/json")
                    .asString();
            return response.getBody();
        }
        catch (UnirestException e) {
            throw new KiProException("Failed on request: " + request, e);
        }
    }

}
