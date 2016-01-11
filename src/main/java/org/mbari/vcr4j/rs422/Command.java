/*
 * @(#)Command.java   2009.02.24 at 09:44:56 PST
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

/**
 *
 * @author brian
 */
public enum Command {

    /** Request the type of VCR (i.e. Device) */
    DEVICE_TYPE_REQUEST("Request device type", new byte[] { 0x00, 0x11 }),
    EJECT_TAPE("Eject tape", new byte[] { 0x20, 0x0f }), FAST_FWD("Fast forward", new byte[] { 0x20, 0x10 }),
    GET_CUEUP_STATUS("Request cueup status", new byte[] { 0x61, 0x20, 0x21 }),
    GET_LTIMECODE("Request longitudinal timecode", new byte[] { 0x61, 0x0c, 0x01 }),
    //GET_LUBTIMECODE("Request longitudinal userbits", new byte[] { 0x61, 0x0c, 0x0F }),
    GET_LUBTIMECODE("Request longitudinal userbits", new byte[] { 0x61, 0x0c, 0x10 }),
    GET_STATUS("Request status", new byte[] { 0x61, 0x20, 0x03 }),

    /**
     * This is a special command supplied by sony that returns the best source
     * of the timecode (VITC at slwo speeds, LTC at high speeds)
     */
    GET_TIMECODE("Request timecode", new byte[] { 0x61, 0x0c, 0x03 }),

    /** Get Timer-1 timecode command */
    GET_TIMECODE1("Request timecode 1", new byte[] { 0x61, 0x0c, 0x04 }),

    /** Get Timer-2 timecode command */
    GET_TIMECODE2("Request timecode2", new byte[] { 0x61, 0x0c, 0x08 }),

    /** Get vertical timecode command */
    GET_VTIMECODE("Request vertical timecode", new byte[] { 0x61, 0x0c, 0x02 }),

    /** Get vertical Userbits timecode command */
    //GET_VUBTIMECODE("Request vertical userbits", new byte[] { 0x61, 0x0c, 0x10 }),
    GET_VUBTIMECODE("Request vertical userbits", new byte[] { 0x61, 0x0c, 0x20 }),
    LOCAL_DISABLE("Local disable", new byte[] { 0x00, 0x0c }), LOCAL_ENABLE("Local enable", new byte[] { 0x00, 0x1d }),

    /**
     * Pause - this was passed to me from Danelle Cline for the JVC VCR. Not
     * sure if it works with Sony's. Not yet sure how to resume.
     */
    PAUSE("Pause", new byte[] { 0x21, 0x13, 0x00 }),

    /** Play command */
    PLAY_FWD("Play", new byte[] { 0x20, 0x01 }),

    /** preset time code command */
    PRESET_TIMECODE("Preset timecode", new byte[] {
        0x44, 0x04, 0x00, 0x00, 0x00, 0x00
    }),

    /** get cue up status command */
    PRESET_USERBITS("Preset userbits", new byte[] {
        0x44, 0x05, 0x00, 0x00, 0x00, 0x00
    }),

    /** Record command */
    RECORD("Record", new byte[] { 0x20, 0x02 }),

    /** Release tape command */
    RELEASE_TAPE("Release tape", new byte[] { 0x20, 0x04 }),

    /** Rewind command */
    REWIND("Rewind", new byte[] { 0x20, 0x20 }),

    /**
     * Shuttle forward command. Last byte should be modified to provide the
     * shuttle speed (byte value between 0 (stopped) and 255 (very fast))
     */
    SHUTTLE_FWD("Shuttle forward", new byte[] { 0x21, 0x13, 0x00 }),

    /**
     * Shuttle reverse command. Last byte should be modified to provide the
     * shuttle speed (byte value between 0 (stopped) and 255 (very fast))
     */
    SHUTTLE_REV("Shuttle reverse", new byte[] { 0x21, 0x23, 0x00 }),

    /**
     * Seek time code command. Last four bytes need to be modified to
     * specify the timecode to seek to. Refer to the VCR documentation for the format of the timecode bytes.
     */
    SONY_SEEK_TIMECODE("Seek timecode", new byte[] {
        0x24, 0x31, 0x00, 0x00, 0x00, 0x00
    }),

    /** Stop command */
    STOP("Stop", new byte[] { 0x20, 0x00 });

    private final byte[] bytes;
    private final String description;

    Command(String description, byte[] bytes) {
        this.description = description;
        this.bytes = bytes;
    }

    /**
     * Retrive the byte array used to execute a particular command
     * @return A copy of the byte array representing the command. Feel free to munge away at
     *      this array as it won't effect the source. An extra byte is tacked on the end in
     *      order to store a checksum. (e.g. rewind will be returned as {0x20, 0x20, 0x00}).
     *      You still have to do the checksum calculation yourself since some commands contain
     *      user specified bytes.
     */
    public byte[] getBytes() {
        byte[] copy = new byte[bytes.length + 1];

        System.arraycopy(bytes, 0, copy, 0, bytes.length);

        return copy;
    }

    public String getDescription() {
        return description;
    }
}
