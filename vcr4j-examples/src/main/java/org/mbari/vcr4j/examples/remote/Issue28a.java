package org.mbari.vcr4j.examples.remote;

import io.reactivex.rxjava3.schedulers.Schedulers;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.control.commands.localization.SelectLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.UpdateLocalizationsCmd;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Issue28a {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue28a.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();
        var rh = rc.getRequestHandler();

        var dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssXXX");

        rh.getLocalizationsCmdObservable()
                .ofType(AddLocalizationsCmd.class)
                .observeOn(Schedulers.io())
                .subscribe(cmd -> {
                    var locs = cmd.getValue()
                            .getLocalizations();

                    Thread.sleep(1000);
                    var concept = dateFormat.format(ZonedDateTime.now(ZoneId.of("UTC")));
                    locs.forEach(x -> x.setConcept(concept));
                    io.send(new UpdateLocalizationsCmd(uuid, locs));
                    io.send(new SelectLocalizationsCmd(uuid, locs.stream().map(Localization::getUuid).toList()));
                });


        io.send(new OpenCmd(uuid, url));

        Thread.sleep(120000);
    }
}
