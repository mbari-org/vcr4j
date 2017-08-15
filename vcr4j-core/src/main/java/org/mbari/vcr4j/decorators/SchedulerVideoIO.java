package org.mbari.vcr4j.decorators;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Decorator that moves the IO off of the currently executing thread. All commands are sent from a single
 * independent thread (not managed by any scheduler or executor). All other observables have their state
 * dealt with on the Executor or Scheduler that you provide.
 *
 * @author Brian Schlining
 * @since 2016-02-11T14:38:00
 */
public class SchedulerVideoIO<S extends VideoState, E extends VideoError> implements VideoIO<S, E>, Decorator {

    private final VideoIO io;

    private final Observable<E> errorObservable;
    private final Observable<S> stateObservable;
    private final Observable<VideoIndex> indexObservable;
    private final CommandQueue commandQueue = new CommandQueue();
    private final Subject<VideoCommand> commandSubject;
    private final Scheduler scheduler;
    private final Observer<VideoCommand> commandObserver;
    private final List<Disposable> disposables = new ArrayList<>();

    public SchedulerVideoIO(VideoIO<S, E> io, Executor executor) {
        this(io, Schedulers.from(executor));
    }

    public SchedulerVideoIO(VideoIO<S, E> io, Scheduler scheduler) {
        this.io = io;
        this.scheduler = scheduler;
        errorObservable = io.getErrorObservable().observeOn(scheduler);
        stateObservable = io.getStateObservable().observeOn(scheduler);
        indexObservable = io.getIndexObservable().observeOn(scheduler);


        commandObserver = new Observer<VideoCommand>() {
            @Override
            public void onComplete() {
                io.getCommandSubject().onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                io.getCommandSubject().onError(throwable);
            }

            @Override
            public void onNext(VideoCommand videoCommand) {
                commandQueue.send(videoCommand);
            }

            @Override
            public void onSubscribe(Disposable disposable) {
                disposables.add(disposable);
            }
        };

        PublishSubject<VideoCommand> subject = PublishSubject.create();
        commandSubject = subject.toSerialized();
        commandSubject.subscribe(commandObserver);

    }

    @Override
    public void unsubscribe() {
        disposables.forEach(Disposable::dispose);
        errorObservable.unsubscribeOn(scheduler);
        stateObservable.unsubscribeOn(scheduler);
        indexObservable.unsubscribeOn(scheduler);
        commandQueue.kill();
    }

    @Override
    public void send(VideoCommand videoCommand) {
        commandSubject.onNext(videoCommand);
    }

    @Override
    public Subject<VideoCommand> getCommandSubject() {
        return commandSubject;
    }

    @Override
    public String getConnectionID() {
        return io.getConnectionID();
    }

    @Override
    public void close() {
        io.close();
    }

    @Override
    public Observable<E> getErrorObservable() {
        return errorObservable;
    }

    @Override
    public Observable<S> getStateObservable() {
        return stateObservable;
    }

    @Override
    public Observable<VideoIndex> getIndexObservable() {
        return indexObservable;
    }


    /**
     * This manages the commands to be sent in a separate thread.
     */
    private class CommandQueue {
        final BlockingQueue<VideoCommand> pendingQueue = new LinkedBlockingQueue<>();
        final Thread thread; // All IO will be done on this thread
        AtomicBoolean isRunning = new AtomicBoolean(true);
        final Runnable runnable = () -> {
            while(isRunning.get()) {
                VideoCommand videoCommand = null;
                try {
                    videoCommand = pendingQueue.poll(3600L, TimeUnit.SECONDS);
                }
                catch (InterruptedException e) {
                    // TODO ?
                }
                if (videoCommand != null) {
                    io.getCommandSubject().onNext(videoCommand);
                }
            }
        };

        void kill() {
            isRunning.set(false);
        }

        void send(VideoCommand videoCommand) {
            pendingQueue.offer(videoCommand);
        }

        public CommandQueue() {
            thread = new Thread(runnable, SchedulerVideoIO.this.getClass().getSimpleName());
            thread.setDaemon(true);
            thread.start();
        }
    }
}
