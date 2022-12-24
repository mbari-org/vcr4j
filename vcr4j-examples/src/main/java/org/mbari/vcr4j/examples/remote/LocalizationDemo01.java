package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class LocalizationDemo01 {

    private static final Logger log = LoggerFactory.getLogger(LocalizationDemo01.class);

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

        io.getVideoIO()
                .getErrorObservable()
                .subscribe(e -> log.atError().log(e::toString));

        io.getVideoIO().getResponseSubject()
                .subscribe(cr -> {
                            var c = cr.command().getName();
                            var r = cr.response().getResponse();
                            if (!r.equalsIgnoreCase(c)) {
                                log.atWarn().log("WARNING command '{}' does not equal response '{}'", c, r);
                            }
                            log.atInfo().log("OK: cmd: {} --- response: {}", c, r);
                        },
                        err -> log.atError().setCause(err).log("ERROR"),
                        () -> log.atInfo().log("DONE"));



        var locs = AppArgs.buildLocalizations(20000, 60000, 1920, 1080);

        var commands = List.of(new OpenCmd(uuid, url),
                new AddLocalizationsCmd(uuid, locs),
                VideoCommands.PLAY
                );

        Thread.sleep(1000);

        for (var cmd: commands) {
            io.getVideoIO().send(cmd);
            Thread.sleep(5000);
        }
        Thread.sleep(1000);
        io.close();
    }


}
