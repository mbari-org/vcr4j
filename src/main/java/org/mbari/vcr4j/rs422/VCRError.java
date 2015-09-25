/*
 * @(#)VCRError.java   2009.02.24 at 09:44:55 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr4j.rs422;

import org.mbari.vcr4j.VCRErrorAdapter;

/**
 * <p>This is not a subclass of Error. Rather it contains the state of the
 * errors as reported by the VCR.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 1.3 $
 */
public class VCRError extends VCRErrorAdapter {

    /** SONY - Checksum error in the command sent */
    static final int CHECKSUM_ERROR = 0x04;

    /** SONY - VCR reports a serial framing error */
    static final int FRAMING_ERROR = 0x40;

    /** Everything is OK */
    static final int OK = 0x00;

    /** SONY - Overran the VCR's command buffer */
    static final int OVERRUN_ERROR = 0x20;

    /** SONY - Parity error on a command byte */
    static final int PARITY_ERROR = 0x10;

    /** SONY - VCR reports Timeout on recieving command */
    static final int TIMEOUT = 0x80;

    /** SONY - Invalid command sent to VCR */
    static final int UNDEFINED_COMMAND = 0x01;

    private int error;

    /**
     * Although this is a potentially useful method. Most programs will be getter served by the <i>getErrorMsg()</i> or <i>toString()</i> methods.
     * @return  the integer error code
     */
    @Override
    public int getError() {
        return error;
    }

    @Override
    public boolean isChecksumError() {
        return ((error & CHECKSUM_ERROR) > 0);
    }

    @Override
    public boolean isFramingError() {
        return ((error & FRAMING_ERROR) > 0);
    }

    @Override
    public boolean isOK() {
        return error == 0;
    }

    @Override
    public boolean isOverrunError() {
        return ((error & OVERRUN_ERROR) > 0);
    }

    @Override
    public boolean isParityError() {
        return ((error & PARITY_ERROR) > 0);
    }

    @Override
    public boolean isTimeout() {
        return ((error & TIMEOUT) > 0);
    }

    @Override
    public boolean isUndefinedCommand() {
        return ((error & UNDEFINED_COMMAND) > 0);
    }

    /**
     * Set the error using the constants of this class
     * @param  error
     */
    public void setError(int error) {
        this.error = error;
        notifyObservers();
    }
}
