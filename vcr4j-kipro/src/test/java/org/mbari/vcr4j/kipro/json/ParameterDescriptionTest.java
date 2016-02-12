package org.mbari.vcr4j.kipro.json;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2016-02-11T09:05:00
 */
public class ParameterDescriptionTest {

    private final URL jsonURL = getClass().getResource("/json/parameter-descriptors-beautified.json");

    @Test
    public void testParse() {
        try (Reader reader = new InputStreamReader(jsonURL.openStream())){
            ParameterDescription[] pds = Constants.GSON.fromJson(reader, ParameterDescription[].class);
            assertTrue("Found the wrong number of parameter descriptions", pds.length == 204);
        }
        catch (IOException e) {
            fail("An exception was thrown. Exception: " + e);
        }


    }
}
