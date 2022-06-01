package org.mbari.vcr4j;


import io.reactivex.rxjava3.core.Observable;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.SeekTimestampCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.time.Timecode;

import java.time.Duration;
import java.time.Instant;

public class VideoController<S extends VideoState, E extends VideoError> {

    private final VideoIO<S, E> io;

    public VideoController(VideoIO<S, E> io) {
        this.io = io;
    }

    public void fastForward() {
        io.send(VideoCommands.FAST_FORWARD);
    }

    public void play() {
        io.send(VideoCommands.PLAY);
    }

    public void rewind() {
        io.send(VideoCommands.REWIND);
    }
    
    /**
     * If you're not sure what the video index is you can call this.
     * A new VideoIndex object should appear in the observable, populated
     * with an appropriate video index.
     */
    public void requestIndex() {
        io.send(VideoCommands.REQUEST_INDEX);
    }
    
    public void requestElapsedTime() {
        io.send(VideoCommands.REQUEST_ELAPSED_TIME);
    }

    public void requestStatus() {
        io.send(VideoCommands.REQUEST_STATUS);
    }
    
    public void requestTimestamp() {
        io.send(VideoCommands.REQUEST_TIMESTAMP);
    }
    
    public void requestTimecode() {
        io.send(VideoCommands.REQUEST_TIMECODE);
    }

    public void seek(Duration elapsedTime) {
        io.send(new SeekElapsedTimeCmd(elapsedTime));
    }

    public void seek(Instant timestamp) {
        io.send(new SeekTimestampCmd(timestamp));
    }

    public void seek(Timecode timecode) {
        io.send(new SeekTimecodeCmd(timecode));
    }

    public <A extends VideoCommand> void send(A command) {
        io.send(command);
    }
    
    public void shuttle(double rate) {
        io.send(new ShuttleCmd(rate));
    }

    public void stop() {
        io.send(VideoCommands.STOP);
    }

    public Observable<E> getErrorObservable() {
        return io.getErrorObservable();
    }

    public Observable<S> getStateObservable() {
        return io.getStateObservable();
    }

    public Observable<VideoIndex> getIndexObservable() {
        return io.getIndexObservable();
    }

    public VideoIO<S, E> getVideoIO() {
        return io;
    }
}
