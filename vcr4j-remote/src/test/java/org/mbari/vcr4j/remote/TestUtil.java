package org.mbari.vcr4j.remote;

/*-
 * #%L
 * vcr4j-remote
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

import org.mbari.vcr4j.remote.control.commands.localization.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestUtil {

    private static final int maxWidth = 1920;
    private static final int maxHeight = 1080;

    public static List<Localization> newLocalizations(int n) {
        var random = new Random();
        var locs = new ArrayList<Localization>();

        for (int i = 0; i < n; i++) {
            var x = random.nextInt(maxWidth);
            var y = random.nextInt(maxHeight);
            var w = random.nextInt(maxWidth);
            var h = random.nextInt(maxHeight);
            var width = Math.min(w, maxWidth - x);
            var height = Math.min(h, maxHeight - y);
            var concept = randomAlphaNumericString(random.nextInt(40) + 5);

            var loc = new Localization(UUID.randomUUID(),
                    concept,
                    1000L * i,
                    0L,
                    x, y, width, height, "#AAAAAA");
            locs.add(loc);
        }
        return locs;
    }

    public static String randomAlphaNumericString(int n) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = n;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
