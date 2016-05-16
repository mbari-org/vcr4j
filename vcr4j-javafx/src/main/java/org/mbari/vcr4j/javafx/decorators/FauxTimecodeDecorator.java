package org.mbari.vcr4j.javafx.decorators;

import javafx.scene.media.MediaPlayer;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.javafx.JFXVideoIO;
import org.mbari.vcr4j.time.Timecode;

import java.time.Duration;
import java.util.Optional;

/**
 * Decorator class for VARS. This decorator converts elapsed-time into a timecode representation.
 * It also listens for timecode requests and repeats them as elapsed-time requests.
 * @author Brian Schlining
 * @since 2016-05-16T10:08:00
 */
public class FauxTimecodeDecorator implements Decorator {

    public FauxTimecodeDecorator(JFXVideoIO io) {
        io.getCommandSubject()
                .filter(vc -> vc.equals(VideoCommands.REQUEST_TIMECODE))
                .forEach(vc -> {
                    MediaPlayer mediaPlayer = io.getMediaPlayer();
                    Duration elapsedTime = Duration.ofMillis((long) mediaPlayer.getCurrentTime().toMillis());
                    Timecode timecode = durationToTimecode(elapsedTime);
                    io.getIndexSubject().onNext(new VideoIndex(Optional.empty(),
                            Optional.of(elapsedTime),
                            Optional.of(timecode)));
                });

        io.getCommandSubject()
                .ofType(SeekTimecodeCmd.class)
                .filter(vc -> vc.getValue().isValid())
                .forEach(vc -> {
                    Duration elapsedTime  = timecodeToDuration(vc.getValue());
                    io.getCommandSubject().onNext(new SeekElapsedTimeCmd(elapsedTime));
                });

    }

    @Override
    public void unsubscribe() {

    }

    private Timecode durationToTimecode(Duration duration) {
        // Convert to 100 fps
        return new Timecode(duration.toMillis() / 10D, 100D);
    }

    private Duration timecodeToDuration(Timecode timecode) {
        Timecode newTimecode = new Timecode(timecode.toString(), 100D);
        return Duration.ofMillis(Math.round(newTimecode.getSeconds() * 1000L));
    }

}
