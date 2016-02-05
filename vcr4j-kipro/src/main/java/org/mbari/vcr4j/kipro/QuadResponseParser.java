package org.mbari.vcr4j.kipro;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Brian Schlining
 * @since 2016-02-04T17:00:00
 */
public class QuadResponseParser {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();


    public void update(String json) {

    }

}
