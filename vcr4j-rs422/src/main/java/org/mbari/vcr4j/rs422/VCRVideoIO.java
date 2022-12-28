package org.mbari.vcr4j.rs422;

import io.reactivex.rxjava3.core.Observable;
import org.mbari.vcr4j.VideoIO;

/**
 *
 * @author Brian Schlining
 * @since 2016-02-03T16:24:00
 */
public interface VCRVideoIO extends VideoIO<RS422State, RS422Error> {

    Observable<RS422Timecode> getTimecodeObservable();

    Observable<RS422Userbits> getUserbitsObservable();
}
