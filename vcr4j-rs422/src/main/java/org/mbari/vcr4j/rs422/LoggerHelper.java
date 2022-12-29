package org.mbari.vcr4j.rs422;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.rs422.util.NumberUtilities;
import org.slf4j.Logger;

/**
 * @author Brian Schlining
 * @since 2016-02-03T16:17:00
 */
public class LoggerHelper {

    private final Logger log;

    public LoggerHelper(Logger log) {
        this.log = log;
    }

    public void logCommand(byte[] bytes, VideoCommand<?> videoCommand) {
        if (log.isDebugEnabled()) {
            log.debug("[0x" + NumberUtilities.toHexString(bytes) + "] >>> VCR (" + videoCommand.getName() + ")");
        }
    }

    public void logResponse(byte[] cmd, byte[] data, byte[] checksum) {
        if (log.isDebugEnabled()) {

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

            log.debug("[0x" + NumberUtilities.toHexString(c) + "] <<< VCR");
        }
    }
}
