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
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.control.commands.localization.SelectLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.UpdateLocalizationsCmd;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class Issue19 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue19.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.send(new OpenCmd(uuid, url));
        var elapsedTimeMillis = 10000L;
        var elapsedTime = Duration.ofMillis(elapsedTimeMillis);
        var loc = new Localization(UUID.randomUUID(),
                "Brian was here",
                elapsedTimeMillis, 0L, 100, 200, 300, 400, "#FF3434");
        var locs = List.of(loc);
        var cmds = List.of(new AddLocalizationsCmd(uuid, locs),
                new SeekElapsedTimeCmd(elapsedTime),
                new SelectLocalizationsCmd(uuid, locs.stream().map(Localization::getUuid).toList()));

        Thread.sleep(2000);
        cmds.forEach(io::send);


        Thread.sleep(5000);
        var updatedLoc = new Localization(loc.getUuid(),
                "Brian is now over here",
                elapsedTimeMillis, 0L, 500, 500, 300, 400, "#FF3434");
        io.send(new UpdateLocalizationsCmd(uuid, List.of(updatedLoc)));
        Thread.sleep(2000);
//        io.send(new SelectLocalizationsCmd(uuid, locs.stream().map(Localization::getUuid).toList()));

    }
}
