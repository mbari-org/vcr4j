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

import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;

public class Issue15 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue15.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.getVideoInfoObservable()
                        .subscribe(xs -> {
                            if (xs.size() == 1) {
                                var head = xs.get(0);
                                var locs = AppArgs.buildLocalizations(10000,
                                        head.getDurationMillis(),
                                        1280, 720);
                                var cmd = new AddLocalizationsCmd(uuid, locs);
                                io.send(cmd);
                            }
                        });

        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);
        io.send(RemoteCommands.REQUEST_VIDEO_INFO);

    }
}
