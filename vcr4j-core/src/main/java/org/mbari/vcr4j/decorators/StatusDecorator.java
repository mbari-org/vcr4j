package org.mbari.vcr4j.decorators;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;

/**
 * The VideoIO simply executes the commands send. For many applications, the desired behavior
 * is to send a command to the VCR followed by a requestStatus. This class adds state requests to
 * the command observable for requests that need it. Typical usage:
 *
 * <pre>
 *     VideoIO io = new RXTXVideoIO("COM 1");
 *     StatusDecorator decorator = new StatusDecorator(io.getCommandSubject());
 *     // That's it! Some commands will automatically trigger a status request.
 *     io.send(VideoCommands.STOP);
 * </pre>
 *
 * @author Brian Schlining
 * @since 2016-03-25T11:34:00
 */
public class StatusDecorator<S extends VideoState, E extends VideoError> implements Decorator {

    private final Observer<VideoCommand> commandSubscriber;

    private Disposable disposable;

    public StatusDecorator(VideoIO<S, E> io) {
        commandSubscriber = new Observer<VideoCommand>() {
            @Override
            public void onComplete() { }

            @Override
            public void onError(Throwable throwable) { }

            @Override
            public void onNext(VideoCommand cmd) {
                if (cmd.equals(VideoCommands.FAST_FORWARD)
                        || cmd.equals(VideoCommands.PLAY)
                        || cmd.equals(VideoCommands.REWIND)
                        || cmd instanceof SeekTimecodeCmd
                        || cmd instanceof ShuttleCmd
                        || cmd.equals(VideoCommands.STOP)) {

                    io.getCommandSubject().onNext(VideoCommands.REQUEST_STATUS);

                }
            }

            @Override
            public void onSubscribe(Disposable disposable) {
                StatusDecorator.this.disposable = disposable;
            }
        };

    }

    @Override
    public void unsubscribe() {
        disposable.dispose();
    }
}
