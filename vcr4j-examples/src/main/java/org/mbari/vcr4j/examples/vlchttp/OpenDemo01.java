package org.mbari.vcr4j.examples.vlchttp;

import org.docopt.Docopt;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.SchedulerVideoIO;
import org.mbari.vcr4j.examples.sharktopoda.SimpleDemo01;
import org.mbari.vcr4j.vlc.http.VlcHttpVideoIO;
import org.mbari.vcr4j.vlc.http.commands.OpenCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

public class OpenDemo01 {

    public static void main(String[] args) throws Exception {
        String prog = SimpleDemo01.class.getName();
        String doc = "Usage: " + prog + " <username> <password> <port> <url>\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String username = (String) opts.get("<username>");
        String pasword = (String) opts.get("<password>");
        Integer port = Integer.parseInt((String) opts.get("<port>"));
        String mrl = (String) opts.get("<url>");

        Logger log = LoggerFactory.getLogger(OpenDemo01.class);

        VlcHttpVideoIO io = new VlcHttpVideoIO(username, pasword, port, UUID.randomUUID());

//        SchedulerVideoIO videoIO = new SchedulerVideoIO(io, Executors.newSingleThreadExecutor());
//        new LoggingDecorator(videoIO);
        io.getCommandSubject()
                .subscribe(v -> {}, t -> log.error("Failed!", t), () -> {});
        io.send(new OpenCmd(mrl));
        Thread.sleep(3000);

    }
}
