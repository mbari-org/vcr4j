package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureDoneCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/* 
    mvn install -DskipTests -Dgpg.skip
    cd vcr4j
    mvn exec:java -Dexec.mainClass=org.mbari.vcr4j.examples.remote.FrameCaptureDemo01 -Dexec.args="8800 file:/Users/brian/Downloads/V4003_20170301T210458.233Z_t4s4_1280_tc03560915_h264.mp4" -pl vcr4j-examples
*/
public class FrameCaptureDemo01 {
    public static void main(String[] args) throws Exception {
        String prog = FrameCaptureDemo01.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        var uuid = UUID.randomUUID();
        var io = new RemoteControl.Builder(uuid)
                .port(5000)
                .remotePort(port)
                .remoteHost("localhost")
                .whenFrameCaptureIsDone(cmd -> System.out.println("Framecapture is done " + cmd))
                .build()
                .get();

        var videoIo = io.getVideoIO();

//        var png = File.createTempFile("trashme", ".png");
//        png.deleteOnExit();
        var rnd = UUID.randomUUID().toString();
        var png1 = Paths.get("trashme-1-" + rnd + ".png").normalize().toAbsolutePath();
        var png2 = Paths.get("trashme-2-" + rnd + ".png").normalize().toAbsolutePath();
        videoIo.send(new OpenCmd(uuid, url));
        Thread.sleep(1000);
        videoIo.send(VideoCommands.PLAY);
        Thread.sleep(500);
        videoIo.send(new SeekElapsedTimeCmd(Duration.ofMillis(10000)));
        videoIo.send(VideoCommands.REQUEST_INDEX);
        videoIo.send(VideoCommands.PLAY);
        Thread.sleep(1000);
        videoIo.send(VideoCommands.REQUEST_INDEX);
        videoIo.send(new FrameCaptureCmd(uuid, UUID.randomUUID(), png1.toString()));
        Thread.sleep(1000);
        videoIo.send(new SeekElapsedTimeCmd(Duration.ofMillis(400000)));
        videoIo.send(VideoCommands.REQUEST_INDEX);
        videoIo.send(VideoCommands.PLAY);
        videoIo.send(new FrameCaptureCmd(uuid, UUID.randomUUID(), png2.toString()));
        Thread.sleep(1000);
        videoIo.send(RemoteCommands.CLOSE);
        io.close();
        System.out.println("Verifying frame captures");
        if (Files.exists(png1)) {
            System.out.println("Frame capture saved to " + png1);
            Files.deleteIfExists(png1);
        }
        else {
            System.out.println("Frame capture FAILED for " + png1);
        }
        if (Files.exists(png2)) {
            System.out.println("Frame capture saved to " + png2);
            Files.deleteIfExists(png2);
        }
        else {
            System.out.println("Frame capture FAILED for " + png2);
        }

        System.exit(0);

    }

    static void printResponse(FrameCaptureDoneCmd cmd) {
        System.out.println("Saved " + cmd.getValue().getImageLocation() + " at " + cmd.getValue().getElapsedTimeMillis() + "ms");
    }
}
