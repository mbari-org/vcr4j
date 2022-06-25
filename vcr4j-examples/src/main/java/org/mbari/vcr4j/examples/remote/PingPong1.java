package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.loc.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.Localization;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.util.List;
import java.util.UUID;

public class PingPong1 {

    public static void main(String[] args) throws Exception {

        var uuid = UUID.randomUUID();

        var videoControl = new VideoControl.Builder()
                .port(8888)
                .build()
                .get();

        var remoteControl = new RemoteControl.Builder(uuid)
                .port(5000)
                .remotePort(8888)
                .build()
                .get();



        Thread.sleep(1000);

        var loc = new Localization(UUID.randomUUID(), "foo", 10L, 10L, 10, 20, 30, 40, "#FFFFFF");
        videoControl.getLifeCycle()
                .get()
                .get()
                .send(new AddLocalizationsCmd(uuid, List.of(loc)));


        Thread.sleep(1000);
        var opt = videoControl.getLifeCycle().get();
        if (opt.isEmpty()) System.out.println("WTF");


        remoteControl.close();
        videoControl.close();

    }
}
