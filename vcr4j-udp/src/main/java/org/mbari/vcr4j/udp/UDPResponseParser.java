package org.mbari.vcr4j.udp;

/*-
 * #%L
 * vcr4j-udp
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

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.time.Timecode;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Schlining
 * @since 2016-02-04T13:46:00
 */
public class UDPResponseParser {

    private final Subject<UDPError> errorObservable;
    private final Subject<Timecode> timecodeObservable;
    private static final System.Logger log = System.getLogger(UDPResponseParser.class.getName());

    private static final Pattern pattern = Pattern.compile("[0-2][0-9]:[0-5][0-9]:[0-5][0-9]:[0-2][0-9]");

    public UDPResponseParser() {
        PublishSubject<Timecode> s1 = PublishSubject.create();
        timecodeObservable = s1.toSerialized();

        PublishSubject<UDPError> s2 = PublishSubject.create();
        errorObservable = s2.toSerialized();
    }

    public void update(byte[] response, Optional<VideoCommand<?>> videoCommand) {
        videoCommand.ifPresent(vc -> {
            if (vc.equals(VideoCommands.REQUEST_TIMECODE) || vc.equals(VideoCommands.REQUEST_TIMESTAMP)) {
                responseToTimecode(response, videoCommand)
                        .ifPresent(timecodeObservable::onNext);
            }
        });
    }

    public Subject<UDPError> getErrorObservable() {
        return errorObservable;
    }

    public Subject<Timecode> getTimecodeObservable() {
        return timecodeObservable;
    }

    private Optional<Timecode> responseToTimecode(byte[] response, Optional<VideoCommand<?>> videoCommand) {
         /*
         * Parse the timecode response.
         */
        String result = null;

        try {
            result = new String(response, "ASCII");
        }
        catch (UnsupportedEncodingException e) {
            try {
                // Try a different encoding
                result = new String(response, "8859_1");
            }
            catch (UnsupportedEncodingException ex) {
                // result = null
                if (log.isLoggable(System.Logger.Level.ERROR)) {
                    log.log(System.Logger.Level.ERROR, "Timecode over UDP is using an unknown encoding");
                    errorObservable.onNext(new UDPError(false, true, videoCommand));
                }
            }
        }
        catch (Exception e) {
            if (log.isLoggable(System.Logger.Level.ERROR)) {
                errorObservable.onNext(new UDPError(false, true, videoCommand));
            }
        }

        Optional<Timecode> timecode = Optional.empty();
        if (result != null) {
            try {
                Matcher matcher = pattern.matcher(result);

                if (matcher.find()) {
                    timecode = Optional.of(new Timecode(matcher.group(0)));
                }
            }
            catch (Exception e) {
                if (log.isLoggable(System.Logger.Level.ERROR)) {
                    log.log(System.Logger.Level.ERROR, "Problem with parsing timecode '" + result + "'", e);
                    errorObservable.onNext(new UDPError(false, true, videoCommand));
                }
            }
        }
        return timecode;
    }
}
