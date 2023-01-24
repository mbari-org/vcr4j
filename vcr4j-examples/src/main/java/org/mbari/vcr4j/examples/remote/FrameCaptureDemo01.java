package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.FrameCaptureCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.RemoteCommands;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

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
                .whenFrameCaptureIsDone(cmd -> System.out.println("DONE: " + cmd))
                .build()
                .get();

        var videoIo = io.getVideoIO();

//        var png = File.createTempFile("trashme", ".png");
//        png.deleteOnExit();
        var png = new File("trashme.png");
        var png2 = new File("trashme2.png");
        videoIo.send(new OpenCmd(uuid, url));
        videoIo.send(VideoCommands.PLAY);
        Thread.sleep(500);
        videoIo.send(new SeekElapsedTimeCmd(Duration.ofMillis(10000)));
        videoIo.send(VideoCommands.REQUEST_INDEX);
        videoIo.send(VideoCommands.PLAY);
        Thread.sleep(1000);
        videoIo.send(VideoCommands.REQUEST_INDEX);
        videoIo.send(new FrameCaptureCmd(uuid, UUID.randomUUID(), png.getAbsolutePath()));
        Thread.sleep(1000);
        videoIo.send(new SeekElapsedTimeCmd(Duration.ofMillis(400000)));
        videoIo.send(VideoCommands.REQUEST_INDEX);
        videoIo.send(VideoCommands.PLAY);
        videoIo.send(new FrameCaptureCmd(uuid, UUID.randomUUID(), png2.getAbsolutePath()));
        Thread.sleep(1000);
//        videoIo.send(RemoteCommands.CLOSE);
        io.close();

        System.exit(0);

    }
}
