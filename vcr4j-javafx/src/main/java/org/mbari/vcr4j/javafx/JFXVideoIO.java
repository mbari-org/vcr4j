package org.mbari.vcr4j.javafx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.mbari.vcr4j.SimpleVideoError;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;


/**
 * @author Brian Schlining
 * @since 2016-03-25T10:55:00
 */
public class JFXVideoIO implements VideoIO<JFXVideoState, SimpleVideoError> {

    public static final double MAX_RATE = 8D; // According to JavaFX docs this is the max rate
    private static final double eps = 0.1;
    public static final double FAST_FORWARD_RATE = 3D;

    private final Subject<VideoCommand<?>> commandSubject;

    private final Subject<SimpleVideoError> errorObservable;

    private final Subject<VideoIndex> indexObservable;

    private final Subject<JFXVideoState> stateObservable;

    private final MediaPlayer mediaPlayer;

    public JFXVideoIO(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        PublishSubject<VideoCommand<?>> s1 = PublishSubject.create();
        commandSubject = s1.toSerialized();
        PublishSubject<SimpleVideoError> s2 = PublishSubject.create();
        errorObservable = s2.toSerialized();
        PublishSubject<VideoIndex> s3 = PublishSubject.create();
        indexObservable = s3.toSerialized();
        PublishSubject<JFXVideoState> s4 = PublishSubject.create();
        stateObservable = s4.toSerialized();

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_INDEX)
                || vc.equals(VideoCommands.REQUEST_ELAPSED_TIME))
                .forEach(vc -> requestCurrentTime());

        commandSubject.ofType(ShuttleCmd.class)
                .filter(vc -> vc.getValue() >= 0) // JavaFX only support shuttle forward
                .forEach(vc -> playAtRate(vc.getValue() * MAX_RATE));

        commandSubject.filter(vc -> vc.equals(VideoCommands.PLAY))
                .forEach(vc -> playAtRate(1.0));

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_STATUS))
                .forEach(vc -> requestStatus());

        commandSubject.filter(vc -> vc.equals(VideoCommands.FAST_FORWARD))
                .forEach(vc -> playAtRate(FAST_FORWARD_RATE));

        commandSubject.filter(vc -> vc.equals(VideoCommands.PAUSE) || vc.equals(VideoCommands.STOP))
                .forEach(vc -> mediaPlayer.pause());

        commandSubject.ofType(SeekElapsedTimeCmd.class)
                .forEach(vc -> seekDuration(vc.getValue()));

    }

    @Override
    public void close() {
        commandSubject.onComplete();
        errorObservable.onComplete();
        indexObservable.onComplete();
        stateObservable.onComplete();
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
        return mediaPlayer.getMedia().getSource();
    }

    @Override
    public Observable<SimpleVideoError> getErrorObservable() {
        return errorObservable;
    }

    @Override
    public Observable<JFXVideoState> getStateObservable() {
        return stateObservable;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    public Subject<VideoIndex> getIndexSubject() {
        return indexObservable;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void requestCurrentTime() {
        Duration currentTime = mediaPlayer.getCurrentTime();
        java.time.Duration duration = java.time.Duration.ofMillis((long) currentTime.toMillis());
        indexObservable.onNext(new VideoIndex(duration));
    }

    private void requestStatus() {
        double currentRate = mediaPlayer.getCurrentRate();
        boolean fastForward = currentRate > 1;

        boolean stopped = Math.abs(currentRate) <= eps;
        //boolean tapeEnd = mediaPlayer.getCurrentTime().compareTo(mediaPlayer.getTotalDuration()) >= 0;
        boolean playing = Math.abs(currentRate - 1D) <= eps;
        boolean shuttling = !playing && currentRate > 0;
        stateObservable.onNext(new JFXVideoState(fastForward, shuttling, stopped, playing));
    }

    private void seekDuration(java.time.Duration duration) {
        Duration jfxDuration = Duration.millis(duration.toMillis());
        mediaPlayer.seek(jfxDuration);
    }

    private void playAtRate(double rate) {
        mediaPlayer.pause();
        mediaPlayer.setRate(rate);
        mediaPlayer.play();
    }
}
