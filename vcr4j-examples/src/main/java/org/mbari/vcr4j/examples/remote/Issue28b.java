package org.mbari.vcr4j.examples.remote;

import org.mbari.vcr4j.commands.SeekElapsedTimeCmd;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.mbari.vcr4j.remote.control.commands.localization.AddLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.mbari.vcr4j.remote.control.commands.localization.SelectLocalizationsCmd;
import org.mbari.vcr4j.remote.control.commands.localization.UpdateLocalizationsCmd;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class Issue28b {

    public static void main(String[] args) throws Exception {
        var url = new URL("http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T210458.233Z_t4s4_1280_tc03560915_h264.mp4");
        var uuid = UUID.fromString("5dc0e157-5802-4844-8a7f-71d449a9275e");

        var remoteControl = new RemoteControl.Builder(uuid)
                .port(5555)
                .remotePort(8800)
                .remoteHost("localhost")
                .build()
                .get();

        var io = remoteControl.getVideoIO();

        io.send(new OpenCmd(uuid, url));
        Thread.sleep(3000);

        var localization = new Localization(
                UUID.fromString("e574ba65-44c2-4758-b558-35bbf9a531ba"),
                "initial concept", 60000L, 0L, 100, 200, 148, 144, "#00FFFF");
        var xs = List.of(localization);

        io.send(new SeekElapsedTimeCmd(Duration.ofMillis(60000)));
        io.send(new AddLocalizationsCmd(uuid, xs));
        io.send(new SelectLocalizationsCmd(uuid, xs.stream().map(Localization::getUuid).toList()));
        Thread.sleep(1000);

        localization.setConcept("20221220T220139Z");
        io.send(new UpdateLocalizationsCmd(uuid, xs));
        io.send(new SelectLocalizationsCmd(uuid, xs.stream().map(Localization::getUuid).toList()));
        Thread.sleep(200);
        io.close();


    }
}
