package org.mbari.vcr4j.remote;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.RemoteCommands;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.util.UUID;

public class PingTest {


    @Test
    public void testPing() throws Exception {

        var videoControl = new VideoControl.Builder()
                .port(8989)
                .build()
                .get();

        var remoteControl = new RemoteControl.Builder(UUID.randomUUID())
                .port(5002)
                .remotePort(8989)
                .build()
                .get();
        Thread.sleep(200);
        remoteControl.getVideoIO().send(RemoteCommands.PING);
        Thread.sleep(200);
        videoControl.getLifeCycle()
                .get()
                .get()
                .send(RemoteCommands.PING);
        Thread.sleep(200);
        remoteControl.close();
    }




}
