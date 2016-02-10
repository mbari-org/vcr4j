package org.mbari.vcr4j.kipro;

import com.mashape.unirest.http.Unirest;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.kipro.commands.CommandToGETRequest;
import org.mbari.vcr4j.kipro.commands.QuadVideoCommands;
import rx.Observable;
import rx.subjects.Subject;

/**
 * @author Brian Schlining
 * @since 2016-02-04T11:16:00
 */
public class QuadVideoIO implements VideoIO<QuadVideoState, QuadVideoError> {

    /** Always ends with "/" */
    private final String httpAddress;
    private CommandToGETRequest transform;

    public QuadVideoIO(String httpAddress) {
        this.httpAddress = httpAddress.endsWith("/") ? httpAddress : httpAddress + "/";
        send(QuadVideoCommands.CONNECT);
    }

    @Override
    public void close() {

    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {

    }

    @Override
    public Subject<VideoCommand, VideoCommand> getCommandSubject() {
        return null;
    }

    @Override
    public String getConnectionID() {
        return null;
    }

    @Override
    public Observable<QuadVideoError> getErrorObservable() {
        return null;
    }

    @Override
    public Observable<QuadVideoState> getStateObservable() {
        return null;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return null;
    }

    private int connect() {
        String response = Unirest.get(httpAddress + "config?action=connect").getBody().toString();
        ConnectionID id =
    }



}
