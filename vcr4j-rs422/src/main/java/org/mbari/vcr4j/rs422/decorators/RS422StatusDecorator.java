package org.mbari.vcr4j.rs422.decorators;


import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.rs422.VCRVideoIO;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;


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
public class RS422StatusDecorator implements Decorator {

    private final Observer<VideoCommand<?>> commandSubscriber;

    private Disposable disposable;

    public RS422StatusDecorator(VCRVideoIO io) {

        commandSubscriber = new Observer<>() {
            @Override
            public void onComplete() { }

            @Override
            public void onError(Throwable throwable) { }

            @Override
            public void onNext(VideoCommand cmd) {
                if (cmd.equals(RS422VideoCommands.EJECT)
                        || cmd.equals(VideoCommands.FAST_FORWARD)
                        || cmd.equals(VideoCommands.PLAY)
                        || cmd.equals(RS422VideoCommands.RECORD)
                        || cmd.equals(RS422VideoCommands.RELEASE_TAPE)
                        || cmd.equals(VideoCommands.REWIND)
                        || cmd instanceof SeekTimecodeCmd
                        || cmd instanceof ShuttleCmd
                        || cmd.equals(VideoCommands.STOP)) {

                    io.getCommandSubject().onNext(VideoCommands.REQUEST_STATUS);

                }
            }

            @Override
            public void onSubscribe(Disposable disposable) {
                RS422StatusDecorator.this.disposable = disposable;
            }
        };
    }


    @Override
    public void unsubscribe() {
        disposable.dispose();
    }
}
