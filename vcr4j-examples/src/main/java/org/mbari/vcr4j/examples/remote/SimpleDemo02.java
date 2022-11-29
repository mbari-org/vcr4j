package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;


import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class SimpleDemo02 {

    public static void main(String[] args) throws Exception {
        var prog = SimpleDemo01.class.getName();
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
            .port(5000)
            .remotePort(port)
            .remoteHost("localhost")
            .build()
            .get();
        Thread.sleep(2000);
        io.getVideoIO().send(new OpenCmd(uuid, url));
        io.close();
        System.exit(0);
    }

}