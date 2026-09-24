/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.rs422.decorators;

import org.mbari.vcr4j.rs422.RS422Timecode;
import org.mbari.vcr4j.rs422.util.NumberUtilities;

/**
 * @author Brian Schlining
 * @since 2016-02-03T12:13:00
 */
public class RS422TimecodeAsString {
    private final String string;

    public RS422TimecodeAsString(RS422Timecode timecode) {
        string = "{name:'RS422Timecode',class='" + timecode.getClass().getName() +
                "',timecode='" + timecode.getTimecode() + "',bytes=0x" +
                NumberUtilities.toHexString(timecode.getTimecodeBytes()) + "}";
    }

    @Override
    public String toString() {
        return string;
    }
}
