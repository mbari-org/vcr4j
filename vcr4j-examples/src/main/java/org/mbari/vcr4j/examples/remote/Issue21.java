package org.mbari.vcr4j.examples.remote;

/*-
 * #%L
 * vcr4j-examples
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

import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.commands.RemoteCommands;


import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;

public class Issue21 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue21.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        var pwd = Paths.get(".").normalize().toAbsolutePath();


        io.getVideoInfoObservable()
                .subscribe(xs -> {
                    if (xs.size() == 1) {
                        var head = xs.get(0);
                        var et = (new Random()).nextInt(head.getDurationMillis().intValue());
                        var cmd = new SeekElapsedTimeCmd(Duration.ofMillis(et));
                        io.send(cmd);

                        var targetSpace = pwd.resolve("trashme " + et + ".png");
                        io.send(new FrameCaptureCmd(uuid, UUID.randomUUID(), targetSpace.toString()));
                        Thread.sleep(2000);

//                        var targetNoSpace = pwd.resolve("trashme-" + et + ".png");
//                        io.send(new FrameCaptureCmd(uuid, UUID.randomUUID(), targetNoSpace.toString()));


                    }
                });



        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);
        io.send(RemoteCommands.REQUEST_VIDEO_INFO);
    }
}
