package org.mbari.vcr4j.examples.jserialcomm;

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

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.jserialcomm.SerialCommVideoIO;

import java.util.Map;

public class ShuttleReverseDemo {

    public static void main(String[] args) throws Exception {
        String prog = ShuttleReverseDemo.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);
        String portName = (String) opts.get("<commport>");

        SerialCommVideoIO io = SerialCommVideoIO.open(portName);

        io.send(VideoCommands.PLAY);
        Thread.sleep(2000);
        io.send(new ShuttleCmd(-0.05D));
        Thread.sleep(4000);
        io.send(new ShuttleCmd(-0.2D));
        Thread.sleep(4000);
        io.send(new ShuttleCmd(-0.8D));
        Thread.sleep(4000);
        io.send(new ShuttleCmd(-0.05D));
        Thread.sleep(4000);
        io.send(VideoCommands.STOP);
        Thread.sleep(2000);
        System.exit(0);

    }
}
