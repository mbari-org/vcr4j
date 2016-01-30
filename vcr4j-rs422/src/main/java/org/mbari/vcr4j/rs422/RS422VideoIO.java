package org.mbari.vcr4j.rs422;

import org.mbari.vcr4j.VideoIO;
import rx.Observable;

/**
 * @author Brian Schlining
 * @since 2016-01-29T16:42:00
 */
public interface RS422VideoIO extends VideoIO<RS422State, RS422Error> {

    // TODO create constructor with serial ports input and output streams
    // TODO make this abstract class
    // TODO move most of RXTXVideoIO code here. Only the openSerialPort method is RXTX specific

    Observable<RS422Timecode> getTimecodeObservable();
    Observable<RS422Userbits> getUserbitsObservable();

}
