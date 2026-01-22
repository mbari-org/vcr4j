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
import org.mbari.vcr4j.commands.VideoCommands;
//import org.mbari.vcr4j.examples.sharktopoda.MultiOpenDemo01;
//import org.mbari.vcr4j.examples.sharktopoda.VideoInfoTest;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MultiOpenDemo1 {

    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(MultiOpenDemo1.class);

        String prog = MultiOpenDemo1.class.getName();
        String doc = "Usage: " + prog + " <port> <url>...\n" +
                "Options:\n" +
                "  -h, --help";

        Map<String, Object> opts = new Docopt(doc).parse(args);

        var port = Integer.parseInt((String) opts.get("<port>"));
        var urls = ((List<String>) opts.get("<url>")).stream()
                .map(s -> {
                    Optional<URL> url;
                    try {
                        url = Optional.of(new URL(s));
                    }
                    catch (MalformedURLException e) {
                        url = Optional.empty();
                    }
                    return url;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        long interval = urls.size() * 1000L;
        for (URL url: urls) {
            log.info("Opening " + url);
            Thread t = new Thread(() -> {
                try {
                    var uuid = UUID.randomUUID();
                    var io = new RVideoIO(uuid, "localhost", port);
                    io.getVideoInfoObservable()
                                    .forEach(System.out::println);

                    io.send(new OpenCmd(uuid, url));
                    io.send(new RequestVideoInfoCmd());
                    Thread.sleep(interval + 2000);
                    io.send(VideoCommands.PLAY);
                    Thread.sleep(interval);
                    for (int i = 1; i < 6; i++) {
                        double rate = 1 - (1D / ((Integer) i).doubleValue());
                        System.out.println("----" + rate);
                        io.send(new ShowCmd(uuid));
                        io.send(new PlayCmd(uuid, rate));
                        Thread.sleep(interval);
                    }
                    Thread.sleep(interval);
                    io.send(VideoCommands.STOP);
                    Thread.sleep(interval);
                    io.send(new CloseCmd(uuid));
                    io.close();
                }
                catch (Exception e) {
                    log.error("Thread died", e);
                }

            });
            t.setDaemon(false);
            t.start();
        }


    }
}
