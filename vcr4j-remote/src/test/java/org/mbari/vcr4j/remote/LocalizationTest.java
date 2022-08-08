package org.mbari.vcr4j.remote;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.loc.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.RemoveLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.loc.UpdateLocalizationsCmd;
import org.mbari.vcr4j.remote.player.VideoControl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void testDifferentSizeAddRequestsR2V() throws  Exception {
        for (var n : List.of(1, 10, 100, 567)) {
            var locs = TestUtil.newLocalizations(n);
            var countDown = new AtomicInteger(locs.size());

            var d = videoControl.getRequestHandler()
                    .getLocalizationsCmdObservable()
                    .ofType(AddLocalizationsCmd.class)
                    .forEach(cmd -> {
                        countDown.addAndGet(-cmd.getValue().getLocalizations().size());
                    });


            remoteControl.getVideoIO().send(new AddLocalizationsCmd(uuid, locs));
            Thread.sleep(200);
            assertEquals(0, countDown.get());
            d.dispose();
        }
    }

    @Test
    public void testDifferentSizeAddRequestsV2R() throws  Exception {
        for (var n : List.of(1, 10, 100, 567)) {
            var locs = TestUtil.newLocalizations(n);
            var countDown = new AtomicInteger(locs.size());

            var d = remoteControl.getRequestHandler()
                    .getLocalizationsCmdObservable()
                    .ofType(AddLocalizationsCmd.class)
                    .forEach(cmd -> {
                        countDown.addAndGet(-cmd.getValue().getLocalizations().size());
                    });

            videoControl
                    .getLifeCycle()
                    .get()
                    .get()
                    .send(new AddLocalizationsCmd(uuid, locs));
            Thread.sleep(200);
            assertEquals(0, countDown.get());
            d.dispose();
        }
    }


    @Test
    public void testDifferentSizeRemoveRequestsR2V() throws  Exception {
        for (var n : List.of(1, 10, 100, 567)) {
            var locs = TestUtil.newLocalizations(n);
            var countDown = new AtomicInteger(locs.size());

            var d = videoControl.getRequestHandler()
                    .getLocalizationsCmdObservable()
                    .ofType(RemoveLocalizationsCmd.class)
                    .forEach(cmd -> {
                        countDown.addAndGet(-cmd.getValue().getLocalizations().size());
                    });

            remoteControl.getVideoIO().send(RemoveLocalizationsCmd.fromLocalizations(uuid, locs));
            Thread.sleep(200);
            assertEquals(0, countDown.get());
            d.dispose();
        }
    }

    @Test
    public void testDifferentSizeRemoveRequestsV2R() throws  Exception {
        for (var n : List.of(1, 10, 100, 567)) {
            var locs = TestUtil.newLocalizations(n);
            var countDown = new AtomicInteger(locs.size());

            var d = remoteControl.getRequestHandler()
                    .getLocalizationsCmdObservable()
                    .ofType(RemoveLocalizationsCmd.class)
                    .forEach(cmd -> {
                        countDown.addAndGet(-cmd.getValue().getLocalizations().size());
                    });

            videoControl.getLifeCycle()
                    .get()
                    .get()
                    .send(RemoveLocalizationsCmd.fromLocalizations(uuid, locs));
            Thread.sleep(200);
            assertEquals(0, countDown.get());
            d.dispose();
        }
    }

    @Test
    public void testDifferentSizeUpdateRequestsR2V() throws  Exception {
        for (var n : List.of(1, 10, 100, 567)) {
            var locs = TestUtil.newLocalizations(n);
            var countDown = new AtomicInteger(locs.size());

            var d = videoControl.getRequestHandler()
                    .getLocalizationsCmdObservable()
                    .ofType(UpdateLocalizationsCmd.class)
                    .forEach(cmd -> {
                        countDown.addAndGet(-cmd.getValue().getLocalizations().size());
                    });

            remoteControl.getVideoIO().send(new UpdateLocalizationsCmd(uuid, locs));
            Thread.sleep(200);
            assertEquals(0, countDown.get());
            d.dispose();
        }
    }

    @Test
    public void testDifferentSizeUpdateRequestsV2R() throws  Exception {
        for (var n : List.of(1, 10, 100, 567)) {
            var locs = TestUtil.newLocalizations(n);
            var countDown = new AtomicInteger(locs.size());

            var d = remoteControl.getRequestHandler()
                    .getLocalizationsCmdObservable()
                    .ofType(UpdateLocalizationsCmd.class)
                    .forEach(cmd -> {
                        countDown.addAndGet(-cmd.getValue().getLocalizations().size());
                    });

            videoControl.getLifeCycle()
                    .get()
                    .get()
                    .send(new UpdateLocalizationsCmd(uuid, locs));
            Thread.sleep(200);
            assertEquals(0, countDown.get());
            d.dispose();
        }
    }



    @AfterClass
    public static void teardown() {
        videoControl.close();
        remoteControl.close();
    }

}
