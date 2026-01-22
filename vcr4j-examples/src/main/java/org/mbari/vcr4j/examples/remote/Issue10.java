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

import org.mbari.vcr4j.remote.control.commands.FrameAdvanceCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;

public class Issue10 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue10.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);

        for (var i = 0; i < 100; i++) {
            io.send(new FrameAdvanceCmd(uuid));
            Thread.sleep(40);
        }
        Thread.sleep(100);
        for (var i = 100; i > 0; i--) {
            io.send(new FrameAdvanceCmd(uuid, false));
            Thread.sleep(40);
        }
        rc.close();
        System.exit(0);

    }
}
