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
import org.mbari.vcr4j.commands.SeekTimecodeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.decorators.VideoCommandAsString;
import org.mbari.vcr4j.decorators.VideoIndexAsString;
import org.mbari.vcr4j.decorators.VideoStateAsString;
import org.mbari.vcr4j.jserialcomm.SerialCommVideoIO;
import org.mbari.vcr4j.rs422.decorators.RS422ErrorAsString;
import org.mbari.vcr4j.rs422.util.NumberUtilities;
import org.mbari.vcr4j.time.FrameRates;
import org.mbari.vcr4j.time.Timecode;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

public class ShuttleDemo01 {
    private PrintStream stream;
    private SerialCommVideoIO io;

    public ShuttleDemo01(SerialCommVideoIO io, PrintStream stream) {
        this.stream = stream;
        this.io = io;

        // --- Watch the observables that are available to all VideoIO objects and print everything
        io.getIndexObservable()
                .subscribe(index -> stream.print(new VideoIndexAsString(index).toString()  + "\n"));
        io.getStateObservable()
                .subscribe(state -> stream.print(new VideoStateAsString(state).toString() + "\n"));
        io.getCommandSubject()
                .subscribe(videoCommand -> stream.print(new VideoCommandAsString(videoCommand) + "\n"));
        // We don't need to see ALL errors as a new error state is created on every ACK from the device.
        // Only print them if they change. Note, although the errorObserver is available to from all
        // VideoIO objects, the code below is adapted specifically to RS422.
        io.getErrorObservable().distinctUntilChanged()
                .subscribe(error -> stream.print(new RS422ErrorAsString(error)  + "\n"));

        // --- Watch the observables specific to RS422 and print everything
        io.getTimecodeObservable()
                .subscribe(timecode -> stream.print("{timecode:'" + timecode + "'}\n"));
        io.getUserbitsObservable()
                .subscribe(userbits -> stream.print("{userbits: 0x" +
                        NumberUtilities.toHexString(userbits.getUserbits()) + "}\n"));

    }

    /**
     * Run a set program for a specified number of iterations.
     * @param n Number of loop iterations
     */
    public void apply(int n) {

        // --- Start by playing the vcr for 1/2 sec per iteration
        io.send(VideoCommands.PLAY);
        requestStatusAndWait(n * 500);

        // --- Shuttle forward at increasing speeds. Shuttle forward rates are
        // 0 < rate <= 1
        for (int i = 1; i <= n; i++ ) {
            double rate = 1 - (1 / (double) i);
            io.send(new ShuttleCmd(rate));
            requestStatusAndWait(3000L * (long) rate);
        }

        // --- Shuttle reverse at increasing speeds. Shuttle reverse rates are
        // -1 <= rate < 0
        for (int i = 1; i <= n; i++ ) {
            double rate = -(1 - 1 / (double) i);
            io.send(new ShuttleCmd(rate));
            requestStatusAndWait(3000L * (long) rate);
        }

        // --- For each iteration we are going to seek forward to a timecode 3 seconds ahead.
        //     In order to do this, we have to monitor when the device is stopped again before
        //     we issue the next seek command.
        for (int i = 0; i < n; i++ ) {
            io.getTimecodeObservable().take(1).forEach(tc -> {
                // The timecode from the observable is incomplete and missing the framerate.
                // All of our RS422 devices at MBARI use NTSC framerates so we just create
                // a copy with the correct frame rate.
                Timecode currentTC = new Timecode(tc.getTimecode().toString(), FrameRates.NTSC);

                // Skip ahead 3 seconds
                Timecode nextTC = new Timecode(currentTC.getFrames() + FrameRates.NTSC * 3, FrameRates.NTSC);
                io.send(new SeekTimecodeCmd(nextTC));

                // Wait until the VCR is stopped. Which means it's reached the correct timecode
                io.getStateObservable()
                        .takeWhile(state -> !state.isStopped())
                        .subscribe(state -> requestStatusAndWait(200));
            });

            // Request the current timecode before staring the next loop iteration
            io.send(VideoCommands.REQUEST_TIMECODE);
        }

        // Make sure we stop the VCR when we're done.
        io.send(VideoCommands.STOP);
    }


    private void requestStatusAndWait(long wait) {
        try {
            io.send(VideoCommands.REQUEST_STATUS);
            Thread.sleep(wait);
        }
        catch (Exception e) {
            stream.print("Thread got interrupted: " + e);
        }
    }


    public static void main(String[] args) {

        String prog = ShuttleDemo01.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help\n" +
                "  -l LOG_FILE, --logfile LOG_FILE A file to log output to\n" +
                "  -n NUM_ITER, --number-iterations NUM_ITER  Number of iterations for each stage [default: 5]\n";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        String portName = (String) opts.get("<commport>");
        int numIter = Integer.parseInt((String) opts.get("--number-iterations"));

        // Stream is just where we're writing the output from the demo
        PrintStream stream;
        try {
            String logfile = (String) opts.get("--logfile");
            stream = new PrintStream(new File(logfile));
        }
        catch (Exception e) {
            stream = System.out;
        }

        // --- Pay attention to these next to lines. This is how to connect to a RS422 device using RXTX
        SerialCommVideoIO io = SerialCommVideoIO.open(portName);

        // --- Run our demo
        ShuttleDemo01 demo = new ShuttleDemo01(io, stream);
        demo.apply(numIter);

        // --- Remember to close your io when done. This closes all Observables and the serial port
        io.close();

    }
}
