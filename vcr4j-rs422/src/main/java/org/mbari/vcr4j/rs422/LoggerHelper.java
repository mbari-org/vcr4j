package org.mbari.vcr4j.rs422;

/*-
 * #%L
 * vcr4j-rs422
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

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.rs422.util.NumberUtilities;

/**
 * @author Brian Schlining
 * @since 2016-02-03T16:17:00
 */
public class LoggerHelper {

    private final System.Logger log;

    public LoggerHelper(System.Logger log) {
        this.log = log;
    }

    public void logCommand(byte[] bytes, VideoCommand<?> videoCommand) {
        log.log(System.Logger.Level.DEBUG, () -> "[0x" + NumberUtilities.toHexString(bytes) + "] >>> VCR (" + videoCommand.getName() + ")");
    }

    public void logResponse(byte[] cmd, byte[] data, byte[] checksum) {
        if (log.isLoggable(System.Logger.Level.DEBUG)) {

            /*
             * Munge it all into a single byte array
             */
            int dataLength = (data == null) ? 0 : data.length;
            final byte[] c = new byte[cmd.length + dataLength + 1];

            System.arraycopy(cmd, 0, c, 0, cmd.length);

            if (data != null) {
                System.arraycopy(data, 0, c, cmd.length, data.length);
            }

            c[c.length - 1] = checksum[0];

            log.log(System.Logger.Level.DEBUG, "[0x" + NumberUtilities.toHexString(c) + "] <<< VCR");
        }
    }
}
