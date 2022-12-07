package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.*;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestCommands01 {


    public static void main(String[] args) throws Exception {
        var prog = TestCommands01.class.getName();
        var doc = """
                Usage: %s <port> <url>
                Options:
                  -h, --help
                """.formatted(prog);

        Map<String, Object> opts = new Docopt(doc).parse(args);

        var port = Integer.parseInt((String) opts.get("<port>"));
        var url = new URL((String) opts.get("<url>"));
        var uuid = UUID.randomUUID();


        var io = new RemoteControl.Builder(uuid)
                .port(5555)
                .remotePort(port)
                .remoteHost("localhost")
                .build()
                .get();

        io.getVideoIO().getErrorObservable().subscribe(e -> System.out.printf("ERROR: %s%n", e));


        io.getVideoIO().getResponseSubject()
                .subscribe(cr -> {
                            var c = cr.command().getName();
                            var r = cr.response().getResponse();
                            if (!r.equalsIgnoreCase(c)) {
                                System.out.println("WARNING command '%s' does not equal response '%s'");
                            }
                            System.out.printf("%s --- %s%n", cr.command().getName(), cr.response().getResponse());
                        },
                        err -> System.out.println(),
                        () -> System.out.println("DONE"));

        var commands = List.of(new OpenCmd(uuid, url),
                VideoCommands.PLAY,
                VideoCommands.FAST_FORWARD,
                VideoCommands.REWIND,
                VideoCommands.PAUSE,
                VideoCommands.PLAY,
                VideoCommands.REQUEST_ELAPSED_TIME,
                VideoCommands.REQUEST_INDEX,
                VideoCommands.REQUEST_STATUS,
                VideoCommands.REQUEST_DEVICE_TYPE,
                VideoCommands.REQUEST_TIMECODE,
                VideoCommands.REQUEST_TIMESTAMP,
                RemoteCommands.FRAMEADVANCE,
                RemoteCommands.REQUEST_ALL_VIDEO_INFOS,
                RemoteCommands.REQUEST_VIDEO_INFO,
                RemoteCommands.SHOW,
                new PlayCmd(uuid, -2.0),
                new PlayCmd(uuid, -2.0),
                new ShuttleCmd(0.02),
                new ShuttleCmd(-0.02),
                new SeekElapsedTimeCmd(Duration.ofMillis(1000)),
                new PlayCmd(uuid, 0.01),
                new SeekElapsedTimeCmd(Duration.ofMillis(10000)),
                new PlayCmd(uuid, 0.01),
                new FrameCaptureCmd(uuid, UUID.randomUUID(), new File("trashme" + Instant.now() + ".png").getAbsolutePath()),
                new SeekElapsedTimeCmd(Duration.ofMillis(1000)),
                new PlayCmd(uuid, 0.01),
                new OpenCmd(uuid, url),
                RemoteCommands.CLOSE);

        Thread.sleep(1000);

        for (var cmd: commands) {
            io.getVideoIO().send(cmd);
            Thread.sleep(1000);
        }
        Thread.sleep(1000);
        io.close();
    }



}
