package org.mbari.vcr4j.examples.remote;

import io.reactivex.rxjava3.schedulers.Schedulers;
import org.docopt.Docopt;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.RemoteCommands;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.control.commands.localization.RemoveLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.SelectLocalizationsCmd;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SelectionDemo01 {

    public static void main(String[] args) throws Exception {
        var prog = SelectionDemo01.class.getName();
        var doc = """
                Usage: %s <port> <url>
                Options:
                  -h, --help
                """.formatted(prog);

        Map<String, Object> opts = new Docopt(doc).parse(args);

        var port = Integer.parseInt((String) opts.get("<port>"));
        var url = new URL((String) opts.get("<url>"));
        var uuid = UUID.randomUUID();

        var random = new Random();


        var remoteControl = new RemoteControl.Builder(uuid)
                .port(5555)
                .remotePort(port)
                .remoteHost("localhost")
                .build()
                .get();

        var io = remoteControl.getVideoIO();

        io.getErrorObservable()
                .subscribe(e -> System.out.printf("ERROR: %s%n", e));


        io.getVideoInfoObservable()
                .observeOn(Schedulers.newThread())
                .subscribe(e -> {
                    if (!e.isEmpty()) {
                        var head = e.get(0);
                        var durationMillis = head.getDurationMillis();
                        var localization = new Localization(UUID.randomUUID(),
                                "Nanomia bijuga",
                                random.nextLong(0, durationMillis),
                                null,
                                random.nextInt(1920),
                                random.nextInt(1080),
                                random.nextInt(10, 100),
                                random.nextInt(10, 100),
                                "#FF2222");
                        io.send(new AddLocalizationsCmd(head.getUuid(), List.of(localization)));
//                        Thread.sleep(200);
                        io.send(VideoCommands.PAUSE);
                        io.send(new SeekElapsedTimeCmd(Duration.ofMillis(localization.getElapsedTimeMillis())));
                        io.send(new SelectLocalizationsCmd(head.getUuid(), List.of(localization.getUuid())));
//                        io.send(new RemoveLocalizationsCmd(head.getUuid(), List.of(localization.getUuid())));
                    }
                });




        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000L);
        io.send(RemoteCommands.REQUEST_VIDEO_INFO);


    }

}
