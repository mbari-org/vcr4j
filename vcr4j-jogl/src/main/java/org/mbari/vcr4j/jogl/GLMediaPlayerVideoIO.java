package org.mbari.vcr4j.jogl;

import com.jogamp.opengl.util.av.GLMediaPlayer;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.SimpleVideoError;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;

import java.time.Duration;

/**
 * @author Brian Schlining
 * @since 2016-08-15T16:35:00
 */
public class GLMediaPlayerVideoIO implements VideoIO<GLMediaPlayerState, SimpleVideoError> {

    public static final float MAX_RATE = 8F;
    private static final double eps = 0.01;
    public static final float FAST_FORWARD_RATE = 3F;

    private final Subject<VideoCommand> commandSubject;

    private final Subject<SimpleVideoError> errorObservable;

    private final Subject<VideoIndex> indexObservable;

    private final Subject<GLMediaPlayerState> stateObservable;

    private final GLMediaPlayer mediaPlayer;

    public GLMediaPlayerVideoIO(GLMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        PublishSubject<VideoCommand> s1 = PublishSubject.create();
        commandSubject = s1.toSerialized();
        PublishSubject<SimpleVideoError> s2 = PublishSubject.create();
        errorObservable = s2.toSerialized();
        PublishSubject<VideoIndex> s3 = PublishSubject.create();
        indexObservable = s3.toSerialized();
        PublishSubject<GLMediaPlayerState> s4 = PublishSubject.create();
        stateObservable = s4.toSerialized();

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_INDEX)
                || vc.equals(VideoCommands.REQUEST_ELAPSED_TIME))
                .forEach(vc -> requestCurrentTime());

        commandSubject.ofType(ShuttleCmd.class)
                .forEach(vc -> mediaPlayer.setPlaySpeed((float) (vc.getValue() * MAX_RATE)));

        commandSubject.filter(vc -> vc.equals(VideoCommands.PLAY))
                .forEach(vc -> {
                    mediaPlayer.setPlaySpeed(1.0F);
                    mediaPlayer.play();
                });

        commandSubject.filter(vc -> vc.equals(VideoCommands.REQUEST_STATUS))
                .forEach(vc -> requestStatus());

        commandSubject.filter(vc -> vc.equals(VideoCommands.FAST_FORWARD))
                .forEach(vc -> mediaPlayer.setPlaySpeed(FAST_FORWARD_RATE));

        commandSubject.filter(vc -> vc.equals(VideoCommands.PAUSE))
                .forEach(vc -> mediaPlayer.pause(true));

        commandSubject.filter(vc -> vc.equals(VideoCommands.STOP))
                .forEach(vc -> mediaPlayer.pause(true));

        commandSubject.ofType(SeekElapsedTimeCmd.class)
                .forEach(vc -> seekDuration(vc.getValue()));

    }

    @Override
    public <A extends VideoCommand> void send(A videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public String getConnectionID() {
        return mediaPlayer.getUri().toString();
    }

    @Override
    public void close() {

    }

    @Override
    public Observable<SimpleVideoError> getErrorObservable() {
        return errorObservable;
    }

    @Override
    public Observable<GLMediaPlayerState> getStateObservable() {
        return stateObservable;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }

    private void requestCurrentTime() {
        int timeMillis = mediaPlayer.getVideoPTS();
        Duration duration = Duration.ofMillis(timeMillis);
        indexObservable.onNext(new VideoIndex(duration));
    }

    private void requestStatus() {
        GLMediaPlayerState state = new GLMediaPlayerState(mediaPlayer);
        stateObservable.onNext(state);
    }

    private void seekDuration(Duration duration) {
        int millis = (int) duration.toMillis();
        mediaPlayer.seek(millis);
    }

}
