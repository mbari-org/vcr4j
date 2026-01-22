package org.mbari.vcr4j.examples.remote;

/*-
 * #%L
 * vcr4j-examples
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
