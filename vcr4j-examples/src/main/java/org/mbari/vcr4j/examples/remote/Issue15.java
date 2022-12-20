package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.commands.FrameAdvanceCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.RemoteCommands;
import org.mbari.vcr4j.remote.control.commands.RequestVideoInfoCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;

public class Issue15 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue15.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.getVideoInfoObservable()
                        .subscribe(xs -> {
                            if (xs.size() == 1) {
                                var head = xs.get(0);
                                var locs = AppArgs.buildLocalizations(10000,
                                        head.getDurationMillis(),
                                        1280, 720);
                                var cmd = new AddLocalizationsCmd(uuid, locs);
                                io.send(cmd);
                            }
                        });

        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);
        io.send(RemoteCommands.REQUEST_VIDEO_INFO);

    }
}
