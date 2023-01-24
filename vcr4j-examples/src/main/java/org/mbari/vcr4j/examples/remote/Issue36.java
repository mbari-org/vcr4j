package org.mbari.vcr4j.examples.remote;

import io.reactivex.rxjava3.schedulers.Schedulers;
import org.docopt.Docopt;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.commands.RemoteCommands;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/*
mvn exec:java -Dexec.mainClass=org.mbari.vcr4j.examples.remote.Issue36 -Dexec.args="8800 /Users/brian/Desktop/VARS_Test/V4003_20170301T210458.233Z_t4s4_1280_tc03560915_h264.mp4 /Users/brian/Desktop/VARS_Test/D1305_20201030T174418Z_h264.mp4 /Users/brian/Desktop/VARS_Test/D1305_20201030T175912Z_prores.mov" -pl vcr4j-examples
 */
public class Issue36 {
    private static final Logger log = LoggerFactory.getLogger(Issue36.class);

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private record Args(int port, List<URL> urls) {}

    private static Optional<URL> toUrl(String s) {
        var corrected = (s.startsWith("http") || s.startsWith("file:")) ?
                s : "file:" + s;
        try {
            return Optional.of(new URL(corrected));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Args parse(String[] args) throws Exception {
        var doc = """
                Usage: %s <port> <url>...
                Options:
                  -h, --help
                """.formatted(Issue36.class.getName());

        Map<String, Object> opts = new Docopt(doc).parse(args);

        var port = Integer.parseInt((String) opts.get("<port>"));
        var urls = ((List<String>) opts.get("<url>"))
                .stream()
                .flatMap(s -> toUrl(s).stream())
                .toList();
        return new Args(port, urls);
    }

    public static void run(int port, List<URL> urls) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (urls == null || urls.isEmpty()) {
            return;
        }

        var uuid = UUID.randomUUID();
        var rc = new RemoteControl.Builder(uuid)
                .port(5555)
                .remotePort(port)
                .remoteHost("localhost")
                .build()
                .get();
        var io = rc.getVideoIO();
        var rh = rc.getRequestHandler();

        io.getErrorObservable()
                .subscribe(e -> log.atWarn().log("ERROR: " + e));

        var scheduler = Schedulers.single();
        var count = new AtomicInteger(3);

        rh.getLocalizationsCmdObservable()
                .ofType(AddLocalizationsCmd.class)
                .observeOn(scheduler)
                .subscribe(cmd -> {
                    log.atInfo().log("Adding " + cmd.getValue().getLocalizations().size() + " localizations");
                    var remaining = count.decrementAndGet();
                    if (remaining == 0) {
                        executor.submit(() -> io.send(RemoteCommands.CLOSE));
                        executor.submit(rc::close);
                        if (urls.size() > 1) {
                            executor.submit(() -> run(port, urls.subList(1, urls.size())));
                        }
                    }
                });

        var url = urls.get(0);
        log.atInfo().log("Opening " + url);
        io.send(new OpenCmd(uuid, url));

    }

    public static void main(String[] args) throws Exception {

        var xs = parse(args);
        executor.submit(() -> run(xs.port(), xs.urls()));

        Thread.sleep(120000);
    }
}
