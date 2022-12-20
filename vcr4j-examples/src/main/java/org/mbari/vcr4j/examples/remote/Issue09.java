package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.RemoteCommands;

import java.util.List;
import java.util.UUID;

public class Issue09 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, TestCommands01.class.getName());
        var io = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();

        var uuid2 = UUID.randomUUID();

        var commands = List.of(new OpenCmd(uuid, url),
                new OpenCmd(uuid2, url),
                RemoteCommands.REQUEST_ALL_VIDEO_INFOS);


        Thread.sleep(1000);

        for (var cmd: commands) {
            io.getVideoIO().send(cmd);
            Thread.sleep(1000);
        }
        Thread.sleep(1000);
        io.close();
        System.exit(0);

    }
}
