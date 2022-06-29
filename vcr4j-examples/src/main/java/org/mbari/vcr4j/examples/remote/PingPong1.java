package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.loc.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.Localization;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.net.URL;
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
                .remoteHost("localhost")
                .build()
                .get();


        remoteControl
                .getVideoIO()
                        .send(new OpenCmd(uuid, new URL("http://m3.shore.mbari.org/videos/M3/proxy/DocRicketts/2021/11/1401/D1401_20211113T181927Z_h264.mp4")));

        Thread.sleep(100);

        var loc = new Localization(UUID.randomUUID(), "foo", 10L, 10L, 10, 20, 30, 40, "#FFFFFF");
        videoControl.getLifeCycle()
                .get()
                .get()
                .send(new AddLocalizationsCmd(uuid, List.of(loc)));

        Thread.sleep(100);

        remoteControl.getVideoIO().send(new AddLocalizationsCmd(uuid, List.of(loc)));


        Thread.sleep(100);

        remoteControl.getVideoIO().send(VideoCommands.PLAY);

        Thread.sleep(100);

        remoteControl.close();
        videoControl.close();

    }
}
