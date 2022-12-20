package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.control.commands.localization.SelectLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.UpdateLocalizationsCmd;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class Issue19 {

    public static void main(String[] args) throws Exception {
        var appArgs = AppArgs.parse(args, Issue19.class.getName());
        var rc = appArgs.remoteControl();
        var uuid = appArgs.getVideoUuid();
        var url = appArgs.url();
        var io = rc.getVideoIO();

        io.send(new OpenCmd(uuid, url));
        var elapsedTimeMillis = 10000L;
        var elapsedTime = Duration.ofMillis(elapsedTimeMillis);
        var loc = new Localization(UUID.randomUUID(),
                "Brian was here",
                elapsedTimeMillis, 0L, 100, 200, 300, 400, "#FF3434");
        var locs = List.of(loc);
        var cmds = List.of(new AddLocalizationsCmd(uuid, locs),
                new SeekElapsedTimeCmd(elapsedTime),
                new SelectLocalizationsCmd(uuid, locs.stream().map(Localization::getUuid).toList()));

        Thread.sleep(2000);
        cmds.forEach(io::send);


        Thread.sleep(5000);
        var updatedLoc = new Localization(loc.getUuid(),
                "Brian is now over here",
                elapsedTimeMillis, 0L, 500, 500, 300, 400, "#FF3434");
        io.send(new UpdateLocalizationsCmd(uuid, List.of(updatedLoc)));
        Thread.sleep(2000);
//        io.send(new SelectLocalizationsCmd(uuid, locs.stream().map(Localization::getUuid).toList()));

    }
}
