package org.mbari.vcr4j.sharktopoda.client.localization;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2020-02-13T11:17:00
 */
public class DataGenerator {

    static Random random = new Random();

    static List<String> concepts = List.of("bat ray",
            "bat star",
            "batfish",
            "Bathochordaeinae",
            "Bathochordaeus",
            "Bathochordaeus charon",
            "Bathochordaeus mcnutti",
            "Bathochordaeus sinker",
            "Bathochordaeus stygius",
            "Bathocyroe",
            "Bathocyroe fosteri",
            "Bathocyroidae",
            "Bathophilus",
            "Bathophilus brevis",
            "Bathophilus filifer",
            "Bathophilus flemingi",
            "Bathyagoninae",
            "Bathyagonus",
            "Bathyagonus nigripinnis",
            "Bathyagonus pentacanthus",
            "bathyal",
            "Bathyalcyon",
            "Bathyalcyon robustum",
            "Bathybembix",
            "Bathybembix bairdii",
            "Bathyceradocus",
            "Bathyceradocus sp. A",
            "Bathyceramaster",
            "Bathyceramaster careyi",
            "Bathyceramaster elegans",
            "Bathycongrus",
            "Bathycongrus macrurus",
            "Bathycrinidae",
            "Bathycrinus",
            "Bathycrinus complanatus",
            "Bathycrinus equatorialis",
            "Bathyctena",
            "Bathyctena chuni",
            "Bathyctenidae",
            "Bathydorididae");


    public static Localization newLocalization() {
        var concept = concepts.get(random.nextInt(concepts.size()));
        var elapseTime = Duration.ofMillis(random.nextInt(100000));
        var localizationUuid = UUID.randomUUID();
        var x = random.nextInt(1920) + 1;
        var y = random.nextInt(1080) + 1;
        var width = random.nextInt(1920 - x + 1);
        var height = random.nextInt(1080 - y + 1);
        var duration = Duration.ofMillis(random.nextInt(20000));
        var annotationUuid = UUID.randomUUID();
        return new Localization(concept, elapseTime, localizationUuid, x, y,
                width, height, duration, annotationUuid);
    }

    public static List<Localization> newLocalizations(int n) {
        List<Localization> xs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            xs.add(newLocalization());
        }
        return xs;
    }
}
