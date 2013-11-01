/*
 * @(#)DeviceType.java   2009.02.24 at 09:44:56 PST
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



package org.mbari.vcr.rs422;

import java.util.Arrays;

/**
 *
 * @author brian
 */
public enum DeviceType {

    UNKNOWN("Unknown", "Unknown", "Unknown VCR Device Type", new byte[] { 0, 0 }, true),
    SONY_DSR40P("Sony", "DSR-40P", "Sony Digital Videocassette Recorder (DVCAM) DSR-40P", new byte[] { (byte) 0x81,
            (byte) 0x30 }, false),
    SONY_DSR45P("Sony", "DSR-45P", "Sony Digital Videocassette Recorder (DVCAM) DSR-45P", new byte[] { (byte) 0x81,
            (byte) 0x31 }, false),

    /** The BF-DV600U doesn't report timecode correctly. It's consistently +40 frames assuming NTSC*/
    JVC_BRDV600U("JVC", "BR-DV600U", "JVC Video Cassette Recorder (MiniDV) BR-DV600U", new byte[] { (byte) 0xF0,
            (byte) 0x45 }, true);

    private final String description;
    private final byte[] key;
    private final String manufacturer;
    private final String model;
    private final boolean vTimecodeSupported;

    DeviceType(String manufacturer, String model, String description, byte[] key, boolean vTimecodeSupported) {
        this.manufacturer = manufacturer;
        this.description = description;
        this.model = model;
        this.key = key;
        this.vTimecodeSupported = vTimecodeSupported;
    }

    /**
     * Returns a brief text description of the VCR
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param key The byte array key as returned by the VCR when a
     * 'requestDeviceType' command is executed.
     * @return the DeviceType for a given key. If no match is found the
     * DEFAULT DeviceType is returned.
     */
    public static DeviceType getDeviceType(byte[] key) {
        DeviceType match = UNKNOWN;

        for (DeviceType deviceType : DeviceType.values()) {
            if (Arrays.equals(key, deviceType.getKey())) {
                match = deviceType;

                break;
            }
        }

        return match;
    }

    /**
     * Returns the bytes that would be returned by a VCR in response to a
     * 'requestDeviceType' command
     * @return A byte array representing the values returned by a partcular VCR
     */
    public byte[] getKey() {
        byte[] copy = new byte[key.length];

        System.arraycopy(key, 0, copy, 0, key.length);

        return copy;
    }

    /**
     * @return The manufacturers name
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @return The model number
     */
    public String getModel() {
        return model;
    }

    /**
     *
     * @return true if vcr.requestVTimecode is correctly supported by the VCR.
     *  false if it can't. If false use requestLTimecode instead.
     */
    public boolean isVTimecodeSupported() {
        return vTimecodeSupported;
    }
}
