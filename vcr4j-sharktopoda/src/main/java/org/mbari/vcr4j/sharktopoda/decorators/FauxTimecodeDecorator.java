package org.mbari.vcr4j.sharktopoda.decorators;

import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.time.Timecode;

import java.time.Duration;
import java.util.Optional;

/**
 * Decorator class for VARS. This decorator converts elapsed-time into a timecode representation.
 * It also listens for timecode requests and repeats them as elapsed-time requests.
 * @author Brian Schlining
 * @since 2016-10-05T15:18:00
 */
public class FauxTimecodeDecorator implements Decorator {

    public FauxTimecodeDecorator(SharktopodaVideoIO io) {

        // --- Request an elapsed time when a timecode is requested
        io.getCommandSubject()
                .filter(vc -> vc.equals(VideoCommands.REQUEST_TIMECODE))
                .forEach(vc -> io.getCommandSubject().onNext(VideoCommands.REQUEST_ELAPSED_TIME));

        // --- Convert 'seek to timecode' to 'seek to elapsed time'
        io.getCommandSubject()
                .ofType(SeekTimecodeCmd.class)
                .filter(vc -> vc.getValue().isValid())
                .forEach(vc -> {
                    Duration elapsedTime = timecodeToDuration(vc.getValue());
                    io.getCommandSubject().onNext(new SeekElapsedTimeCmd(elapsedTime));
                });

        // --- Add faux timecode to videoindex (if missing)
        io.getIndexObservable()
                .filter(vi -> !vi.getTimecode().isPresent() && vi.getElapsedTime().isPresent())
                .forEach(vi -> {
                    Timecode timecode = durationToTimecode(vi.getElapsedTime().get());
                    io.getIndexSubject()
                            .onNext(new VideoIndex(Optional.empty(), vi.getElapsedTime(), Optional.of(timecode)));
                });
    }

    private Timecode durationToTimecode(Duration duration) {
        // Convert to 100 fps
        return new Timecode(duration.toMillis() / 10D, 100D);
    }

    private Duration timecodeToDuration(Timecode timecode) {
        Timecode newTimecode = new Timecode(timecode.toString(), 100D);
        return Duration.ofMillis(Math.round(newTimecode.getSeconds() * 1000L));
    }

    @Override
    public void unsubscribe() {

    }
}
