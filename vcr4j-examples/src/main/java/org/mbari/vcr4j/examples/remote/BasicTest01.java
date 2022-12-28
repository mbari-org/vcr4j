package org.mbari.vcr4j.examples.remote;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.docopt.Docopt;
import org.mbari.vcr4j.remote.control.RError;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicTest01 {

  private static Logger log = LoggerFactory.getLogger(BasicTest01.class);

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
    var commands = List.of(new OpenCmd(uuid, url));
    try (var io = new RVideoIO(uuid, "localhost", port)) {
      io.getErrorObservable()
        .subscribe(BasicTest01::logError);
    }



  }

  private static <A extends RError> void logError(A error) {
    var m = log.atError();
    if (error.getException().isPresent()) {
      m = m.setCause(error.getException().get());
    }
    // m.log(// logvideo command and messate);
  }
  
}
