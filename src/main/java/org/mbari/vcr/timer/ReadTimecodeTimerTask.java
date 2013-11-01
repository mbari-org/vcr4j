/*
 * @(#)ReadTimecodeTimerTask.java   2009.02.24 at 09:44:55 PST
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



package org.mbari.vcr.timer;

import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRState;
import org.mbari.vcr.rs422.DeviceType;
import org.mbari.vcr.rs422.VCR;
import org.mbari.vcr.rs422.VCRReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TimerTask that reads the VTimeCode if the vcr is playing, otherwise it
 * requests the LTimeCode
 *
 * @author brian
 */
public class ReadTimecodeTimerTask extends VCRTimerTask {

    private static final Logger log = LoggerFactory.getLogger(ReadTimecodeTimerTask.class);
    private DeviceType deviceType;

    /**
     * Constructs ...
     */
    public ReadTimecodeTimerTask() {
        super();
    }

    public void run() {
        IVCR vcr = getVcr();


        /*
         * LTimeCode is on the audio track. It's slightly less accurate than
         * the VTimeCode, however, the VTimeCode can't be read except when
         * the VCR is playing. So use the LTimeCode for everything but
         * play mode.
         */
        if (vcr != null) {
            IVCRState state = vcr.getVcrState();

            if (state != null) {
                if ((deviceType != null) && deviceType.isVTimecodeSupported() && state.isPlaying()) {
                    vcr.requestVTimeCode();
                }
                else {
                    vcr.requestLTimeCode();
                }
            }
        }
    }

    @Override
    public void setVcr(IVCR vcr) {
        super.setVcr(vcr);
        deviceType = DeviceType.UNKNOWN;

        // If we're using rs422, sniff the type of device we're talking too.
        if (vcr instanceof VCR) {
            synchronized (vcr) {
                vcr.requestDeviceType();

                VCRReply vcrReply = (VCRReply) vcr.getVcrReply();

                deviceType = DeviceType.getDeviceType(vcrReply.getData());
            }

            if (log.isDebugEnabled()) {
                log.debug("Found VCR DeviceType = " + deviceType.getDescription());
            }
        }

    }
}
