package org.mbari.vcr4j.examples.udp;

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

import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.udp.TimeServer;
import org.mbari.vcr4j.udp.UDPVideoIO;

/**
 * @author Brian Schlining
 * @since 2016-02-04T15:07:00
 */
public class UDPClientServerDemo {

    public static void main(String[] args) throws Exception{


        // --- Start up our demo server that just produces local time as a timecode
        TimeServer timeServer = new TimeServer(9000);
        timeServer.start();

        // --- Configure the UPDVideoIO
        UDPVideoIO io = new UDPVideoIO("localhost", 9000);
        LoggingDecorator decorator = new LoggingDecorator<>(io);
        VideoController controller = new VideoController(io); // Wrap io with a standard control

        controller.requestStatus();
        controller.requestTimecode();
        controller.requestTimestamp();
        controller.play();
        Thread.sleep(2000);
        controller.requestStatus();
        Thread.sleep(100);
        for (int i = 0; i < 10; i++) {
            controller.requestTimecode();
            Thread.sleep(400);
        }

        controller.stop();  // Does nothing as our UDP client just reports timecode. NO VCR Control
        controller.requestStatus();
        controller.requestTimecode();
        io.close();
        timeServer.stop();
        System.exit(0);

    }
}
