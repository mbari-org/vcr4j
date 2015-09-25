package org.mbari.vcr4j.rs422;

import org.mbari.vcr4j.IVCR;
import org.mbari.comm.BadPortException;

import java.io.IOException;
import org.mbari.movie.Timecode;

/**
 * <pre>
brian@kuroshio:~/workspace/vcr4j/target/vcr4j-1.6-SNAPSHOT
>> testvcr /dev/tty.PL2303-0000201A
-- VCR TEST CHECK LIST:
--   1. Do you have a serial port?
        If you're using a USB/RS232 adapter, did you install the correct driver?
--   2. Is the serial cable plugged into your computer?
--   3. Is the serial cable plugged into your VCR?
--   4. Do you need an RS232/RS422 converter?  (Check your VCR's documenation)
        If so are you using one?
--   5. Is the remote switch on your VCR toggled to 'remote'?
-- Attempting to connect to VCR using serial port: /dev/tty.PL2303-0000201A
WARNING:  RXTX Version mismatch
	Jar version = RXTX-2.2pre1
	native lib Version = RXTX-2.2pre2
-- SUCCESS!! Connected to VCR on port /dev/tty.PL2303-0000201A
-- Beginning VCR tests
-- Current timecode is 01:09:38:19
-- Sending 'PLAY' command
-- Current timecode is 01:09:38:19
-- Current Vtimecode is 01:09:38:19
-- Current Ltimecode is 01:09:38:19
-- Sending 'STOP' command
-- Current timecode is 01:09:43:22
-- Sending 'FAST FORWARD' command
-- Current timecode is 01:13:02:17
-- Sending 'REWIND' command
-- Current timecode is 01:13:07:12
-- Current timecode is 01:10:48:17
-- Sending 'STOP' command
-- Current timecode is 01:10:26:21
-- Returning to starting timecode of 01:09:38:19
2009-06-24 11:01:54,423 [main] INFO  org.mbari.vcr4j.rs422.VCR  - Closing serial port:/dev/tty.PL2303-0000201A
-- VCR tests are completed
 * </pre>
 */
public class TestRunner {

    

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 1) {
            System.out.println("---- FAIL!! You need to provide the name of the serial port");
        }

        System.out.println("-- VCR TEST CHECK LIST:");
        System.out.println("--   1. Do you have a serial port?\n        " +
                "If you're using a USB/RS232 adapter, did you install the correct driver?");
        System.out.println("--   2. Is the serial cable plugged into your computer?");
        System.out.println("--   3. Is the serial cable plugged into your VCR?");
        System.out.println("--   4. Do you need an RS232/RS422 converter?  (Check your VCR's documenation)\n        " +
                "If so are you using one?");
        System.out.println("--   5. Is the remote switch on your VCR toggled to 'remote'?");

        String portName = args[0];

        // Connect using the port
        System.out.println("-- Attempting to connect to VCR using serial port: " + portName);

        IVCR vcr = null;
        try {
            //vcr = new org.mbari.vcr4j.purejavacomm.VCR(portName);
            vcr = new VCR(portName);
            System.out.println("-- SUCCESS!! Connected to VCR on port " + portName);
        }
        catch (BadPortException e) {
            System.out.println("---- FAIL!! Unable to connect to a VCR on port " + portName);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e) {
            System.out.println("---- FAIL!! Unable to get I/O connection to a VCR on port " + portName);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (vcr == null) {
            System.out.println("---- FAIL!! VCR connection was not created");
        }
        else {
            System.out.println("-- Beginning VCR tests");
            vcr.requestStatus();
            vcr.requestTimeCode();
            Timecode startTimecode = new Timecode(vcr.getVcrTimecode().getTimecode());
            System.out.println("-- Current timecode is " + vcr.getVcrTimecode().getTimecode());
            System.out.println("-- Sending 'PLAY' command ");
            vcr.play();
            vcr.requestStatus();
            if (!vcr.getVcrState().isPlaying()) {
                System.out.println("---- FAIL!! VCR does not report it's status as 'playing'");
            }
            vcr.requestTimeCode();
            System.out.println("-- Current timecode is " + vcr.getVcrTimecode().getTimecode());
            vcr.requestVTimeCode();
            System.out.println("-- Current Vtimecode is " + vcr.getVcrTimecode().getTimecode());
            vcr.requestLTimeCode();
            System.out.println("-- Current Ltimecode is " + vcr.getVcrTimecode().getTimecode());
            Thread.sleep(5000);
            System.out.println("-- Sending 'STOP' command ");
            vcr.stop();
            vcr.requestStatus();
            if (!vcr.getVcrState().isStopped()) {
                System.out.println("---- FAIL!! VCR does not report it's status as 'stopped'");
            }
            vcr.requestLTimeCode();
            vcr.requestLUserbits();
            vcr.requestStatus();
            vcr.requestUserbits();
            vcr.requestVTimeCode();
            vcr.requestVUserbits();
            System.out.println("-- Current timecode is " + vcr.getVcrTimecode().getTimecode());
            System.out.println("-- Sending 'FAST FORWARD' command ");
            vcr.fastForward();
            vcr.requestStatus();
            if (!vcr.getVcrState().isFastForwarding()) {
                System.out.println("---- FAIL!! VCR does not report it's status as 'fast-forwarding'");
            }
            Thread.sleep(5000);
            vcr.requestTimeCode();
            System.out.println("-- Current timecode is " + vcr.getVcrTimecode().getTimecode());
            System.out.println("-- Sending 'REWIND' command ");
            vcr.rewind();
            if (!vcr.getVcrState().isRewinding()) {
                System.out.println("---- FAIL!! VCR does not report it's status as 'rewinding'");
            }
            vcr.requestTimeCode();
            System.out.println("-- Current timecode is " + vcr.getVcrTimecode().getTimecode());
            Thread.sleep(5000);
            vcr.requestTimeCode();
            System.out.println("-- Current timecode is " + vcr.getVcrTimecode().getTimecode());
            System.out.println("-- Sending 'STOP' command ");
            vcr.stop();
            Thread.sleep(500);
            if (!vcr.getVcrState().isStopped()) {
                System.out.println("---- FAIL!! VCR does not report it's status as 'stopped'");
            }
            vcr.requestTimeCode();
            System.out.println("-- Current timecode is " + vcr.getVcrTimecode().getTimecode());
            System.out.println("-- Returning to starting timecode of " + startTimecode);
            vcr.seekTimecode(startTimecode);
            vcr.disconnect();
        }
        System.out.println("-- VCR tests are completed");

        System.exit(0);

        

    }
}
