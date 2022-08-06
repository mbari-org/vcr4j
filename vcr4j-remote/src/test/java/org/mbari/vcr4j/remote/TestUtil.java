package org.mbari.vcr4j.remote;

import org.mbari.vcr4j.remote.control.commands.loc.Localization;

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
