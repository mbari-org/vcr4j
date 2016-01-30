package org.mbari.vcr4j.rs422.decorators;

import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;
import rx.subjects.Subject;


/**
 * The VideoIO simply executes the commands send. For many applications, the desired behavior
 * is to send a command to the VCR followed by a requestStatus. This class adds state requests to
 * the command observable for requests that need it. Typical usage:
 *
 * <pre>
 *     VideoIO io = new RXTXVideoIO("COM 1");
 *     RS422StatusDecorator decorator = new RS422StatusDecorator(io.getCommandSubject());
 *     // That's it! Some commands will automatically trigger a status request.
 *     io.send(VideoCommands.STOP);
 *
 * </pre>
 *
 * @author Brian Schlining
 * @since 2016-01-29T09:55:00
 */
public class RS422StatusDecorator {

    private final Subject<VideoCommand, VideoCommand> commandSubject;

    public RS422StatusDecorator(Subject<VideoCommand, VideoCommand> commandSubject) {
        this.commandSubject = commandSubject;
        commandSubject.subscribe(this::decorate);
        commandSubject.onNext(VideoCommands.REQUEST_TIMECODE);
        commandSubject.onNext(VideoCommands.REQUEST_STATUS);
    }

    private void decorate(VideoCommand cmd) {
        if (cmd.equals(RS422VideoCommands.EJECT)
                || cmd.equals(VideoCommands.FAST_FORWARD)
                || cmd.equals(VideoCommands.PLAY)
                || cmd.equals(RS422VideoCommands.RECORD)
                || cmd.equals(RS422VideoCommands.RELEASE_TAPE)
                || cmd.equals(VideoCommands.REWIND)
                || cmd instanceof SeekTimecodeCmd
                || cmd instanceof ShuttleCmd
                || cmd.equals(VideoCommands.STOP)) {

            commandSubject.onNext(VideoCommands.REQUEST_STATUS);

        }
    }
}
