package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.RemoteCommands;
import org.mbari.vcr4j.remote.control.commands.VideoInfo;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;

public class Issue33 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue33.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.getVideoInfoObservable()
                .subscribe(xs -> {
                    if (xs.size() == 1) {
                        var head = xs.get(0);
                        doStuff(io, head);
                    }
                });


        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);
        io.send(RemoteCommands.REQUEST_VIDEO_INFO);

    }

    private static void doStuff(RVideoIO io, VideoInfo videoInfo) throws Exception {


        io.send(VideoCommands.PAUSE);
        // -- Seek to a random time in the video
        var pwd = Paths.get(".").normalize().toAbsolutePath();
        var et = (new Random()).nextInt(videoInfo.getDurationMillis().intValue());
        io.send(new SeekElapsedTimeCmd(Duration.ofMillis(et)));

        io.send(VideoCommands.PAUSE);

        // - Request index
        io.send(VideoCommands.REQUEST_INDEX);

        // -- Send a frame capture command
        var targetSpace = pwd.resolve("trashme " + et + ".png");
        io.send(new FrameCaptureCmd(videoInfo.getUuid(), UUID.randomUUID(), targetSpace.toString()));
        Thread.sleep(2000);

    }
}
