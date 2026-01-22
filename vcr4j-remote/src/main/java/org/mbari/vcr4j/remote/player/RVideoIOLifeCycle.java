package org.mbari.vcr4j.remote.player;

/*-
 * #%L
 * vcr4j-remote
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

import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.remote.control.RVideoIO;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This manages the life cycle of a {@link RVideoIO} that is created by the video player in
 * response to {@link org.mbari.vcr4j.remote.control.commands.ConnectCmd}s sent by a
 * {@link org.mbari.vcr4j.remote.control.RemoteControl}. It is used to send
 * commands from the video player to the remote control.
 *
 * This is normally manged by the {@link VideoControl} and shouldn't need to be called directly
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RVideoIOLifeCycle {

//    private static final Logger log = LoggerFactory.getLogger(RVideoIOLifeCycle.class);
    private static final System.Logger log = System.getLogger(RVideoIOLifeCycle.class.getName());

    private AtomicReference<RVideoIO> videoIO = new AtomicReference<>();

    private boolean withLogging = false;

    public RVideoIOLifeCycle(boolean withLogging) {
        this.withLogging = withLogging;
    }

    public Optional<RVideoIO> connect(UUID uuid, String host, int port) {

        log.log(System.Logger.Level.DEBUG, "Opening connection to " + host + ":" + port);
        var io = videoIO.updateAndGet(old -> {
            try {
                if (old != null) {
                    old.close();
                }
            }
            catch (Exception e) {
                log.log(System.Logger.Level.WARNING, "Failed to close RVideoIO", e);
            }

            try {
                var videoIo = new RVideoIO(uuid, host, port);
                if (withLogging) {
                    new LoggingDecorator<>(videoIo);
                }
                return videoIo;
            }
            catch (Exception e) {
                log.log(System.Logger.Level.ERROR, "Failed to open RVideoIO connecting to " + host + ":" + port, e);
                return null;
            }
        });
        return Optional.ofNullable(io);

    }

    public Optional<RVideoIO> get() {
        return Optional.ofNullable(videoIO.get());
    }

    public void disconnect() {
        log.log(System.Logger.Level.INFO, "Found " + videoIO.get());
        videoIO.updateAndGet(old -> {
            try {
                if (old != null) {
                    old.close();
                }
                else {
                    log.log(System.Logger.Level.DEBUG, "No port was open");
                }
            }
            catch (Exception e) {
                log.log(System.Logger.Level.WARNING, "Failed to close RVideoIO", e);
            }
            return null;
        });
    }

}
