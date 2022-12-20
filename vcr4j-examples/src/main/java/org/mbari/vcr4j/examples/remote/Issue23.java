package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.RemoteCommands;

public class Issue23 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue23.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);
        io.send(RemoteCommands.CLOSE);
        System.exit(0);
    }
}
