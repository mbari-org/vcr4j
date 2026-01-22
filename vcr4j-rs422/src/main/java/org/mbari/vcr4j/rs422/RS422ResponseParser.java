/*
 * @(#)RS422ResponseParser.java   by Brian Schlining
 * 
 * Copyright (c) 2016 Monterey Bay Aquarium Research Institute
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import java.util.Arrays;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.rs422.util.NumberUtilities;


public class RS422ResponseParser {

    public static final byte[] ACK = { 0x10, 0x01 };
    public static final byte[] NACK = { 0x11, 0x12 };
    private final Subject<RS422Error> errorObservable;
    private final Subject<RS422State> statusObservable;
    private final Subject<RS422Timecode> timecodeObservable;
    private final Subject<RS422Userbits> userbitsObservable;

    public RS422ResponseParser() {
        PublishSubject<RS422Error> s1 = PublishSubject.create();
        errorObservable = s1.toSerialized();

        PublishSubject<RS422State> s2 = PublishSubject.create();
        statusObservable = s2.toSerialized();

        PublishSubject<RS422Timecode> s3 = PublishSubject.create();
        timecodeObservable = s3.toSerialized();

        PublishSubject<RS422Userbits> s4 = PublishSubject.create();
        userbitsObservable = s4.toSerialized();
    }

    /**
     * The last byte in a command block is the checksum, i.e. the lower eight
     * bits of the sum of the other bytes in the command block
     * @param command The byte array to to calculate a checksum
     * @return The checksum value of the command
     */
    public static byte calculateChecksum(byte[] command) {
        int temp = 0;

        for (int i = 0; i < command.length; i++) {
            temp += command[i];
        }

        return ((byte) temp);
    }

    public void update(byte[] command, byte[] cmd, byte[] data, byte[] checksum) {
        update(command, cmd, data, checksum);
    }

    /**
     * Update the state objects associated with the VCR's response
     * @param command The command that was sent to the VCR
     * @param cmd The command portion of the VCR's reply
     * @param data The data portion of the VCR's reply
     * @param checksum The checksum of the VCR's reply
     * @param videoCommand The video command
     */
    public void update(byte[] command, byte[] cmd, byte[] data, byte[] checksum, VideoCommand videoCommand) {
        byte commandChecksum = calculateChecksum(command);

        // Make sure the checksum matches corectly. The VCRError for bad checksums
        // is set in the isCheckSum OK method.
        if (!isChecksumOK(cmd, data, checksum)) {
            errorObservable.onNext(new RS422Error(RS422Error.CHECKSUM_ERROR, videoCommand));
        }
        else if (isAck(cmd)) {
            errorObservable.onNext(new RS422Error(RS422Error.OK, videoCommand));
        }
        else if (RS422Timecode.isTimecodeReply(cmd)) {
            timecodeObservable.onNext(new RS422Timecode(data));
        }
        else if (RS422Userbits.isUserbitsReply(cmd)) {
            userbitsObservable.onNext(new RS422Userbits(data));
        }
        else if (RS422State.isStatusReply(cmd)) {
            statusObservable.onNext(new RS422State(NumberUtilities.toLong(data)));
        }
        else if (isNack(cmd)) {

            // If NACK the Reply should set the VCRError bits only
            int nackData = (int) data[0];
            errorObservable.onNext(new RS422Error(nackData, videoCommand));
        }
    }

    /**
     * Checks to see if the reply is an ack (acknowledgement)
     * @param cmd byte array of command.
     * @return True if the reply is ACK
     */
    public static boolean isAck(byte[] cmd) {
        return (Arrays.equals(cmd, ACK));
    }

    /**
     * Checks the checksum in the reply with the calculated checksum. Sets
     * the appropriate error status in the vCRStatus object.
     * @param cmd the command
     * @param data the data
     * @param checksum checksum to valided cmd/data against
     *
     * @return
     */
    private boolean isChecksumOK(byte[] cmd, byte[] data, byte[] checksum) {
        boolean OK;

        if (cmd == null) {
            OK = false;
        }
        else {

            // Put the Command block received from the VCR into a single byte array
            // so that we can calculate the
            byte[] cmdBlock;

            if ((data == null)
                    || (data.length == 0)) {
                cmdBlock = new byte[cmd.length];
            }
            else {
                cmdBlock = new byte[cmd.length + data.length];
            }

            for (int i = 0; i < cmd.length; i++) {
                cmdBlock[i] = cmd[i];
            }

            if ((data != null)
                    && (data.length > 0)) {
                for (int i = 2; i < cmdBlock.length; i++) {
                    cmdBlock[i] = data[i - 2];
                }
            }

            // Compare the checksums
            byte checksum2 = calculateChecksum(cmdBlock);

            OK = checksum[0] == checksum2;
        }

        return OK;
    }

    /**
     * Checks to see if the reply is a nack (i.e error)
     * @param cmd the cmd we're checking to see if it's a NACK
     * @return True if the reply is NACK
     */
    public static boolean isNack(byte[] cmd) {
        return (Arrays.equals(cmd, NACK));
    }

    public Subject<RS422Error> getErrorObservable() {
        return errorObservable;
    }

    public Subject<RS422State> getStatusObservable() {
        return statusObservable;
    }

    public Subject<RS422Timecode> getTimecodeObservable() {
        return timecodeObservable;
    }

    public Subject<RS422Userbits> getUserbitsObservable() {
        return userbitsObservable;
    }
}
