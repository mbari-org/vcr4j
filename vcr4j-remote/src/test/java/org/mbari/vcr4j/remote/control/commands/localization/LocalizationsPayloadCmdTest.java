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
package org.mbari.vcr4j.remote.control.commands.localization;

import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class LocalizationsPayloadCmdTest {

    private static SelectLocalizationsCmd cmdWith(int count) {
        var uuids = Stream.generate(UUID::randomUUID).limit(count).toList();
        return new SelectLocalizationsCmd(UUID.randomUUID(), uuids);
    }

    @Test
    public void emptyListYieldsEmptyGrouping() {
        var groups = cmdWith(0).groupedPayload(10);
        assertTrue(groups.isEmpty());
    }

    @Test
    public void listSmallerThanBatchYieldsOneGroup() {
        var groups = cmdWith(3).groupedPayload(10);
        assertEquals(1, groups.size());
        assertEquals(3, groups.get(0).size());
    }

    @Test
    public void listExactlyBatchSizeYieldsOneGroup() {
        var groups = cmdWith(10).groupedPayload(10);
        assertEquals(1, groups.size());
        assertEquals(10, groups.get(0).size());
    }

    @Test
    public void listExactMultipleYieldsEvenGroups() {
        var groups = cmdWith(30).groupedPayload(10);
        assertEquals(3, groups.size());
        groups.forEach(g -> assertEquals(10, g.size()));
    }

    @Test
    public void listNonMultipleYieldsPartialFinalGroup() {
        var groups = cmdWith(25).groupedPayload(10);
        assertEquals(3, groups.size());
        assertEquals(10, groups.get(0).size());
        assertEquals(10, groups.get(1).size());
        assertEquals(5, groups.get(2).size());
    }

    @Test
    public void groupsPreserveOrderAndCoverInput() {
        var uuids = Stream.generate(UUID::randomUUID).limit(7).toList();
        var cmd = new SelectLocalizationsCmd(UUID.randomUUID(), uuids);
        var flattened = cmd.groupedPayload(3).stream()
                .flatMap(List::stream)
                .toList();
        assertEquals(uuids, flattened);
    }
}
