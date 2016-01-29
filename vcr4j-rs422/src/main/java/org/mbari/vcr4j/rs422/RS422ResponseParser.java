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

import java.util.Arrays;
import java.util.Optional;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.commands.VideoCommand;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RS422ResponseParser {

    public static final byte[] ACK = { 0x10, 0x01 };
    public static final byte[] NACK = { 0x11, 0x12 };
    private final Subject<RS422Error, RS422Error> errorObservable = new SerializedSubject<>(PublishSubject.create());
    private final Subject<RS422State, RS422State> statusObservable = new SerializedSubject<>(PublishSubject.create());
    private final Subject<RS422Timecode, RS422Timecode> timecodeObservable =
        new SerializedSubject<>(PublishSubject.create());
    private final Subject<RS422Userbits, RS422Userbits> userbitsObservable =
        new SerializedSubject<>(PublishSubject.create());

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
        update(command, cmd, data, checksum, Optional.empty());
    }


    /**
     * Update the state objects associated with the VCR's response
     * @param command The command that was sent to the VCR
     * @param cmd The command portion of the VCR's reply
     * @param data The data portion of the VCR's reply
     * @param checksum The checksum of the VCR's reply
     *
     */
    public void update(byte[] command, byte[] cmd, byte[] data, byte[] checksum, Optional<VideoCommand> videoCommand) {
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
     * @return True if the reply is ACK
     */
    public static boolean isAck(byte[] cmd) {
        return (Arrays.equals(cmd, ACK));
    }

    /**
     * Checks the checksum in the reply with the calculated checksum. Sets
     * the appropriate error status in the vCRStatus object.
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
     * @return True if the reply is NACK
     */
    public static boolean isNack(byte[] cmd) {
        return (Arrays.equals(cmd, NACK));
    }

    public Subject<RS422Error, RS422Error> getErrorObservable() {
        return errorObservable;
    }

    public Subject<RS422State, RS422State> getStatusObservable() {
        return statusObservable;
    }

    public Subject<RS422Timecode, RS422Timecode> getTimecodeObservable() {
        return timecodeObservable;
    }

    public Subject<RS422Userbits, RS422Userbits> getUserbitsObservable() {
        return userbitsObservable;
    }
}
