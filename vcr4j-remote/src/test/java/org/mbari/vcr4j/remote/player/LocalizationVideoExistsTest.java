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
package org.mbari.vcr4j.remote.player;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mbari.vcr4j.remote.TestUtil;
import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.mbari.vcr4j.remote.control.commands.localization.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalizationVideoExistsTest {

    private static class KnownVideoController extends NoopVideoController {
        private final UUID known;

        KnownVideoController(UUID known) {
            this.known = known;
        }

        @Override
        public boolean hasVideo(UUID videoUuid) {
            return known.equals(videoUuid);
        }
    }

    private final UUID known = UUID.randomUUID();
    private final UUID unknown = UUID.randomUUID();
    private final RxPlayerRequestHandler handler =
            new RxPlayerRequestHandler(new KnownVideoController(known), new RVideoIOLifeCycle(false));

    private void assertFailed(RResponse response) {
        assertEquals(RResponse.FAILED, response.getStatus());
        assertEquals("No video for uuid", response.getCause());
    }

    private void assertOk(RResponse response) {
        assertEquals(RResponse.OK, response.getStatus());
        assertNull(response.getCause());
    }

    @Test
    public void unknownVideoFailsAllLocalizationCommandsAndEmitsNothing() {
        var emitted = new AtomicInteger();
        handler.getLocalizationsCmdObservable().subscribe(c -> emitted.incrementAndGet());

        var locs = TestUtil.newLocalizations(1);
        assertFailed(handler.handleAddLocalizationsRequest(new AddLocalizationsCmd.Request(unknown, locs)));
        assertFailed(handler.handleUpdateLocalizationsRequest(new UpdateLocalizationsCmd.Request(unknown, locs)));
        assertFailed(handler.handleRemoveLocalizationsRequest(
                new RemoveLocalizationsCmd.Request(unknown, List.of(UUID.randomUUID()))));
        assertFailed(handler.handleSelectLocalizationsRequest(
                new SelectLocalizationsCmd.Request(unknown, List.of(UUID.randomUUID()))));
        assertFailed(handler.handleClearLocalizationsRequest(new ClearLocalizationsCmd.Request(unknown)));
        assertEquals(0, emitted.get());
    }

    @Test
    public void knownVideoOkAndEmits() {
        var emitted = new AtomicInteger();
        handler.getLocalizationsCmdObservable().subscribe(c -> emitted.incrementAndGet());

        var locs = TestUtil.newLocalizations(1);
        assertOk(handler.handleAddLocalizationsRequest(new AddLocalizationsCmd.Request(known, locs)));
        assertOk(handler.handleUpdateLocalizationsRequest(new UpdateLocalizationsCmd.Request(known, locs)));
        assertOk(handler.handleRemoveLocalizationsRequest(
                new RemoveLocalizationsCmd.Request(known, List.of(UUID.randomUUID()))));
        assertOk(handler.handleSelectLocalizationsRequest(
                new SelectLocalizationsCmd.Request(known, List.of(UUID.randomUUID()))));
        assertOk(handler.handleClearLocalizationsRequest(new ClearLocalizationsCmd.Request(known)));
        assertEquals(5, emitted.get());
    }

    @Test
    public void defaultHasVideoIsTrueForExistingImplementations() {
        assertTrue(new NoopVideoController().hasVideo(unknown));
    }
}
