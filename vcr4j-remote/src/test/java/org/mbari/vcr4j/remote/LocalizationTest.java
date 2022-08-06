package org.mbari.vcr4j.remote;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.loc.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalizationTest {

    private static RemoteControl remoteControl;
    private static VideoControl videoControl;

    private static UUID uuid = UUID.randomUUID();

    @BeforeClass
    public static void setup() throws Exception {

        videoControl = new VideoControl.Builder()
                .port(8888)
                .build()
                .get();

        remoteControl = new RemoteControl.Builder(uuid)
                .port(5000)
                .remotePort(8888)
                .build()
                .get();



        Thread.sleep(200);

    }

    @Test
    public void testSendOne() throws Exception {

        var success = new AtomicBoolean(false);


        var locs = TestUtil.newLocalizations(1);

        videoControl.getRequestHandler()
                .getLocalizationsCmdObservable()
                .ofType(AddLocalizationsCmd.class)
                .forEach(cmd -> success.set(cmd.getValue().getLocalizations().get(0).equals(locs.get(0))));


        remoteControl.getVideoIO().send(new AddLocalizationsCmd(uuid, locs));
        Thread.sleep(200);
        assertTrue(success.get());
    }

    @Test
    public void testSendOneHundred() throws Exception {

        var success = new AtomicBoolean(false);


        var locs = TestUtil.newLocalizations(100);

        videoControl.getRequestHandler()
                .getLocalizationsCmdObservable()
                .ofType(AddLocalizationsCmd.class)
                .forEach(cmd -> {
                    var ok = cmd.getValue().getLocalizations().size() == locs.size();
                    success.set(ok);
                });


        remoteControl.getVideoIO().send(new AddLocalizationsCmd(uuid, locs));
        Thread.sleep(200);
        assertTrue(success.get());
    }


    @AfterClass
    public static void teardown() {
        videoControl.close();
        remoteControl.close();
    }




}
