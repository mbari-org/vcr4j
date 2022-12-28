package org.mbari.vcr4j.examples.remote;

import io.reactivex.rxjava3.schedulers.Schedulers;
import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.control.commands.localization.SelectLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.UpdateLocalizationsCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Issue31 {

    private static final Logger log = LoggerFactory.getLogger(Issue31.class);

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue28a.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();
        var rh = rc.getRequestHandler();

        var dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssXXX");

        var elapsedTimeMillis = 10000L;
        var locs = new CopyOnWriteArrayList<>(AppArgs.buildLocalizations(10, 60000, 1280, 720));
        locs.forEach(x -> x.setElapsedTimeMillis(elapsedTimeMillis));

        var scheduler = Schedulers.single();

        rh.getLocalizationsCmdObservable()
                .ofType(SelectLocalizationsCmd.class)
                .observeOn(scheduler)
                .subscribe(cmd -> {
                    var uuids = cmd.getValue()
                            .getLocalizations();
                    var s = uuids.stream()
                            .map(Objects::toString)
                            .collect(Collectors.joining(", "));
                    log.atInfo().log("Sharktopoda selected " + s);

                    var concept = dateFormat.format(ZonedDateTime.now(ZoneId.of("UTC")));
                    var updatedLocs = uuids.stream()
                            .flatMap(key ->
                                    locs.stream()
                                            .filter(x -> x.getUuid().equals(key))
                                            .findFirst()
                                            .stream()
                                            .peek(x -> x.setConcept(concept)))
                            .toList();
                    io.send(new UpdateLocalizationsCmd(uuid, updatedLocs));
                    io.send(new SelectLocalizationsCmd(uuid, uuids));
                });

        rh.getLocalizationsCmdObservable()
                .ofType(AddLocalizationsCmd.class)
                .observeOn(scheduler)
                .subscribe(cmd -> {
                    log.atInfo().log("Adding " + cmd.getValue().getLocalizations().size());
                    locs.addAll(cmd.getValue().getLocalizations());
                });


        io.send(new OpenCmd(uuid, url));
        Thread.sleep(2000);
        io.send(new AddLocalizationsCmd(uuid, locs));
        io.send(new SelectLocalizationsCmd(uuid, locs.stream().map(Localization::getUuid).toList()));
        io.send(new SeekElapsedTimeCmd(Duration.ofMillis(elapsedTimeMillis)));


        Thread.sleep(120000);
    }
}
