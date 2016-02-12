package org.mbari.vcr4j.kipro.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Brian Schlining
 * @since 2016-02-10T11:14:00
 */
public class Constants {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    /**
     * ePramams that we care about.
     */
    public static class EParams {
        public static String DISPLAY_TIMECODE = "eParamID_DisplayTimecode";

    }
}
