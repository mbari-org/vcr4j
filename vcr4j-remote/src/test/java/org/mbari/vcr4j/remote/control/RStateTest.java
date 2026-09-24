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
package org.mbari.vcr4j.remote.control;

import org.junit.Test;
import static org.junit.Assert.*;

public class RStateTest {

    @Test
    public void parseKnownStateNames() {
        assertEquals(RState.State.PLAYING, RState.parse("playing").getState());
        assertEquals(RState.State.PAUSED, RState.parse("paused").getState());
        assertEquals(RState.State.SHUTTLE_FORWARD, RState.parse("shuttling forward").getState());
        assertEquals(RState.State.SHUTTLE_REVERSE, RState.parse("shuttling reverse").getState());
    }

    @Test
    public void parseNotFoundPreservesSemantics() {
        var rs = RState.parse("not found");
        assertEquals(RState.State.NOT_FOUND, rs.getState());
        assertFalse(rs.isConnected());
    }

    @Test
    public void parseIsCaseInsensitive() {
        assertEquals(RState.State.PLAYING, RState.parse("PLAYING").getState());
        assertEquals(RState.State.SHUTTLE_FORWARD, RState.parse("Shuttling Forward").getState());
    }

    @Test
    public void parseUnknownNameMapsToUnknownError() {
        // "not found" is a distinct wire value; an unrecognized name must not be silently
        // collapsed into it.
        var rs = RState.parse("bogus state");
        assertEquals(RState.State.UNKNOWN_ERROR, rs.getState());
        assertNotEquals(RState.State.NOT_FOUND, rs.getState());
    }

    @Test
    public void parseNullMapsToUnknownError() {
        assertEquals(RState.State.UNKNOWN_ERROR, RState.parse(null).getState());
    }

    @Test
    public void fromRateHandlesSlowMotionForward() {
        assertEquals(RState.State.SHUTTLE_FORWARD, RState.fromRate(0.5));
    }

    @Test
    public void fromRateHandlesReverse() {
        assertEquals(RState.State.SHUTTLE_REVERSE, RState.fromRate(-1.0));
        assertEquals(RState.State.SHUTTLE_REVERSE, RState.fromRate(-0.5));
    }

    @Test
    public void fromRateHandlesPlayingAndPaused() {
        assertEquals(RState.State.PLAYING, RState.fromRate(1.0));
        assertEquals(RState.State.PAUSED, RState.fromRate(0.0));
    }

    @Test
    public void fromRateHandlesNaN() {
        assertEquals(RState.State.UNKNOWN_ERROR, RState.fromRate(Double.NaN));
    }
}
