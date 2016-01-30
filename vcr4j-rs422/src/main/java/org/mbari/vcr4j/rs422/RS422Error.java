package org.mbari.vcr4j.rs422;

import java.util.Optional;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoCommand;

public class RS422Error implements VideoError {

    /** SONY - Checksum error in the command sent */
    public static final int CHECKSUM_ERROR = 0x04;

    /** SONY - VCR reports a serial framing error */
    public static final int FRAMING_ERROR = 0x40;

    /** Everything is OK */
    public static final int OK = 0x00;

    /** SONY - Overran the VCR's command buffer */
    public static final int OVERRUN_ERROR = 0x20;

    /** SONY - Parity error on a command byte */
    public static final int PARITY_ERROR = 0x10;

    /** SONY - VCR reports Timeout on recieving command */
    public static final int TIMEOUT = 0x80;

    /** SONY - Invalid command sent to VCR */
    public static final int              UNDEFINED_COMMAND = 0x01;
    private final int                    error;
    private final Optional<VideoCommand> videoCommand;

    public RS422Error(int error, Optional<VideoCommand> videoCommand) {
        this.error        = error;
        this.videoCommand = videoCommand;
    }

    public boolean isChecksumError() {
        return ((error & CHECKSUM_ERROR) > 0);
    }

    /**
     * Although this is a potentially useful method. Most programs will be getter served by the <i>getErrorMsg()</i> or <i>toString()</i> methods.
     * @return  the integer error code
     */
    public int getError() {
        return error;
    }

    /**
     * @return true if an error condition occurred in response to a command
     */
    @Override
    public boolean hasError() {
        return error != OK;
    }

    public boolean isFramingError() {
        return ((error & FRAMING_ERROR) > 0);
    }

    public boolean isOK() {
        return error == 0;
    }

    public boolean isOverrunError() {
        return ((error & OVERRUN_ERROR) > 0);
    }

    public boolean isParityError() {
        return ((error & PARITY_ERROR) > 0);
    }

    public boolean isTimeout() {
        return ((error & TIMEOUT) > 0);
    }

    public boolean isUndefinedCommand() {
        return ((error & UNDEFINED_COMMAND) > 0);
    }

    /**
     * @return The command that triggered the error
     */
    @Override
    public Optional<VideoCommand> getVideoCommand() {
        return videoCommand;
    }
}
