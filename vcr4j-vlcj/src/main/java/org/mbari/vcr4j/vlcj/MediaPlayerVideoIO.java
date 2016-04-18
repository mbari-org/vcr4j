package org.mbari.vcr4j.vlcj;

import org.mbari.vcr4j.SimpleVideoError;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import uk.co.caprica.vlcj.player.MediaPlayer;

import java.time.Duration;

/**
 * @author Brian Schlining
 * @since 2016-04-18T15:42:00
 */
public class MediaPlayerVideoIO implements VideoIO<MediaPlayerState, SimpleVideoError> {

    public static final float MAX_RATE = 8F;
    private static final double eps = 0.01;
    public static final float FAST_FORWARD_RATE = 3F;

    private final Subject<VideoCommand, VideoCommand> commandSubject =
            new SerializedSubject<>(PublishSubject.create());

    private final Subject<SimpleVideoError, SimpleVideoError> errorObservable =
            new SerializedSubject<>(PublishSubject.create());

    private final Subject<VideoIndex, VideoIndex> indexObservable =
            new SerializedSubject<>(PublishSubject.create());

    private final Subject<MediaPlayerState, MediaPlayerState> stateObservable =
            new SerializedSubject<>(PublishSubject.create());

    private final MediaPlayer mediaPlayer;

    public MediaPlayerVideoIO(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_INDEX)
                || vc.equals(VideoCommands.REQUEST_ELAPSED_TIME))
                .forEach(vc -> requestCurrentTime());

        commandSubject.ofType(ShuttleCmd.class)
                .forEach(vc -> mediaPlayer.setRate((float) (vc.getValue() * MAX_RATE)));

        commandSubject.filter(vc -> vc.equals(VideoCommands.PLAY))
                .forEach(vc -> {
                    mediaPlayer.setRate(1.0F);
                    mediaPlayer.play();
                });

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_STATUS))
                .forEach(vc -> requestStatus());

        commandSubject.filter(vc -> vc.equals(VideoCommands.FAST_FORWARD))
                .forEach(vc -> mediaPlayer.setRate(FAST_FORWARD_RATE));

        commandSubject.filter(vc -> vc.equals(VideoCommands.PAUSE))
                .forEach(vc -> mediaPlayer.pause());

        commandSubject.filter(vc -> vc.equals(VideoCommands.STOP))
                .forEach(vc -> mediaPlayer.stop());

        commandSubject.ofType(SeekElapsedTimeCmd.class)
                .forEach(vc -> seekDuration(vc.getValue()));

    }

    @Override
    public void close() {
        commandSubject.onCompleted();
        errorObservable.onCompleted();
        indexObservable.onCompleted();
        stateObservable.onCompleted();
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
        return null;
    }

    @Override
    public Observable<SimpleVideoError> getErrorObservable() {
        return errorObservable;
    }

    @Override
    public Observable<MediaPlayerState> getStateObservable() {
        return stateObservable;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    private void requestCurrentTime() {
        long millis = mediaPlayer.getTime();
        Duration duration = Duration.ofMillis(millis);
        indexObservable.onNext(new VideoIndex(duration));
    }

    private void requestStatus() {
        MediaPlayerState state = new MediaPlayerState(mediaPlayer);
        stateObservable.onNext(state);
    }

    private void seekDuration(Duration duration) {
        long millis = duration.toMillis();
        mediaPlayer.setTime(millis);
    }
}
