package org.mbari.vcr4j.examples.purejavacomm;

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
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.purejavacomm.PJCVideoIO;
import org.mbari.vcr4j.rs422.decorators.RS422LoggingDecorator;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brian Schlining
 * @since 2016-02-03T14:11:00
 */
public class SimpleDemo02 {

    public static void main(String[] args) throws Exception {
        String prog = SimpleDemo02.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String portName = (String) opts.get("<commport>");

        PJCVideoIO io = PJCVideoIO.open(portName); // Open serial port
        LoggingDecorator loggingDecorator = new RS422LoggingDecorator(io); // Log everything

        // This decorator starts a lot of chatter with the device. Useful
        // for keeping timecode and status in sync with a VCR
        VCRSyncDecorator syncDecorator = new VCRSyncDecorator(io);

        VideoController controller = new VideoController(io); // Wrap io with a standard control
        controller.play();
        controller.requestStatus();

        // Wait until video starts playing
        AtomicBoolean wait = new AtomicBoolean(true);
        io.getStateObservable()
                .filter(state -> state.isPlaying() && !state.isStandingBy())
                .take(1)
                .forEach(state -> wait.set(false));
        while (wait.get()) {
            Thread.sleep(200);
        }

        controller.requestTimecode();
        Thread.sleep(4000);
        controller.stop();
        Thread.sleep(1000);
        io.close();
        System.exit(0);

    }
}
