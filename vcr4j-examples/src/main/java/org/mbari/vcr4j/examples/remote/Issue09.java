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

import java.util.List;
import java.util.UUID;

public class Issue09 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue09.class.getName());
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
