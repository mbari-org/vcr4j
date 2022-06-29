package org.mbari.vcr4j.examples.remote;

import org.docopt.Docopt;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class SimpleDemo01 {

    public static void main(String[] args) throws Exception {
        String prog = org.mbari.vcr4j.examples.sharktopoda.SimpleDemo01.class.getName();
        String doc = "Usage: " + prog + " <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        Integer port = Integer.parseInt((String) opts.get("<port>"));
        URL url = new URL((String) opts.get("<url>"));

        var uuid = UUID.randomUUID();
        var io = new RemoteControl.Builder(uuid)
                .port(5000)
                .remotePort(8888)
                .build()
                .get();

        io.getVideoIO().send(new OpenCmd(uuid, url));

        Thread.sleep(2000);
        io.close();


    }
}
