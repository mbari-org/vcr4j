package org.mbari.vcr4j.remote;

/*-
 * #%L
 * vcr4j-remote
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.RemoveLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.UpdateLocalizationsCmd;
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
        for (var n : List.of(1, 10, 100, 567, 5011)) {
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
        for (var n : List.of(1, 10, 100, 567, 5011)) {
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
        for (var n : List.of(1, 10, 100, 567, 5011)) {
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
        for (var n : List.of(1, 10, 100, 567, 5011)) {
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
        for (var n : List.of(1, 10, 100, 567, 5011)) {
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
        for (var n : List.of(1, 10, 100, 567, 5011)) {
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
