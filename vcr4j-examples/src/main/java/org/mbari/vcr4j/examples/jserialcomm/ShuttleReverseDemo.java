package org.mbari.vcr4j.examples.jserialcomm;

import org.docopt.Docopt;
import org.mbari.vcr4j.commands.ShuttleCmd;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.jserialcomm.SerialCommVideoIO;

import java.util.Map;

public class ShuttleReverseDemo {

    public static void main(String[] args) throws Exception {
        String prog = ShuttleReverseDemo.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);
        String portName = (String) opts.get("<commport>");

        SerialCommVideoIO io = SerialCommVideoIO.open(portName);

        io.send(VideoCommands.PLAY);
        Thread.sleep(2000);
        io.send(new ShuttleCmd(-0.05D));
        Thread.sleep(4000);
        io.send(new ShuttleCmd(-0.2D));
        Thread.sleep(4000);
        io.send(new ShuttleCmd(-0.8D));
        Thread.sleep(4000);
        io.send(new ShuttleCmd(-0.05D));
        Thread.sleep(4000);
        io.send(VideoCommands.STOP);
        Thread.sleep(2000);
        System.exit(0);

    }
}
