package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.commands.RemoteCommands;

// https://github.com/mbari-org/vars-feedback/issues/39
public class Issue39 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue39.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.send(new OpenCmd(uuid, url));

        Thread.sleep(2000);

        for (int i = 0; i < 1000; i++) {
            io.send(RemoteCommands.FRAMEADVANCE);
            Thread.sleep(60);
        }
    }
}
