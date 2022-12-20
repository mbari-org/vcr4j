package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URL;
import java.util.Map;
import java.util.UUID;

public record AppArgs(RemoteControl remoteControl, URL url) {

    private static final Logger log = LoggerFactory.getLogger(AppArgs.class);

    public UUID getVideoUuid() {
        return remoteControl.getVideoIO().getUuid();
    }


    public static AppArgs parse(String[] args, String className) throws Exception {
        var doc = """
                Usage: %s <port> <url>
                Options:
                  -h, --help
                """.formatted(className);

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
                .subscribe(e -> log.atWarn().log("ERROR: " + e));

        return new AppArgs(io, url);
    }


}
