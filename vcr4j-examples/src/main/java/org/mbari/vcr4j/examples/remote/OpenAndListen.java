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

public class OpenAndListen {

    public static void main(String[] args) throws Exception{
        var appArgs = AppArgs.parse(args, OpenAndListen.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();
        var rh = rc.getRequestHandler();

        rh.getLocalizationsCmdObservable()
                .ofType(AddLocalizationsCmd.class)
                .observeOn(Schedulers.io())
                .subscribe(cmd -> System.out.println("Added localization"));


        io.send(new OpenCmd(uuid, url));

        Thread.sleep(120000);
    }
}
