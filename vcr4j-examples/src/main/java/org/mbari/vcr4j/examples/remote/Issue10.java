package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.remote.control.commands.FrameAdvanceCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;

public class Issue10 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue10.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);

        for (var i = 0; i < 100; i++) {
            io.send(new FrameAdvanceCmd(uuid));
            Thread.sleep(40);
        }
        Thread.sleep(100);
        for (var i = 100; i > 0; i--) {
            io.send(new FrameAdvanceCmd(uuid, false));
            Thread.sleep(40);
        }
        rc.close();
        System.exit(0);

    }
}