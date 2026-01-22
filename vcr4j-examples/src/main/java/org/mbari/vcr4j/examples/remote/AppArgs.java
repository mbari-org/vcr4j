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

import org.docopt.Docopt;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.localization.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URL;
import java.util.*;

public record AppArgs(RemoteControl remoteControl, URL url) {

    private static final Logger log = LoggerFactory.getLogger(AppArgs.class);

    public UUID getVideoUuid() {
        return remoteControl.getVideoIO().getUuid();
    }


    public static AppArgs parse(String[] args, String className) throws Exception {
        var doc = """
                Usage: %s <port> <url>
                Options:
                  -h, --help
                """.formatted(className);

        Map<String, Object> opts = new Docopt(doc).parse(args);

        var port = Integer.parseInt((String) opts.get("<port>"));
        var url = new URL((String) opts.get("<url>"));
        var uuid = UUID.randomUUID();

        var io = new RemoteControl.Builder(uuid)
                .port(5555)
                .remotePort(port)
                .remoteHost("localhost")
                .build()
                .get();

        io.getVideoIO()
                .getErrorObservable()
                .subscribe(e -> log.atWarn().log("ERROR: " + e));

        return new AppArgs(io, url);
    }


    public static List<Localization> buildLocalizations(int n,
                                                         long durationMillis,
                                                         int width,
                                                         int height) {
        var random = new Random();
        var locs = new ArrayList<Localization>();
        for (var i = 0; i < n; i++) {
            var color = String.format("#%06x", random.nextInt(0xFFFFFF + 1));
            var loc = new Localization(UUID.randomUUID(), "foo-" + i,
                    random.nextLong(0, durationMillis),
                    random.nextLong(0, 30000),
                    random.nextInt(0, width - 100),
                    random.nextInt(0, height - 100),
                    random.nextInt(0, 100),
                    random.nextInt(0, 100),
                    color);
            locs.add(loc);
        }
        return locs;
    }


}
