package org.mbari.vcr4j.decorators;

/*-
 * #%L
 * vcr4j-core
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
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
        commandSubscriber = new Observer<>() {
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
        io.getCommandSubject().subscribe(commandSubscriber);

    }

    @Override
    public void unsubscribe() {
        disposable.dispose();
    }
}
