package org.mbari.vcr4j.kipro;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Brian Schlining
 * @since 2016-02-08T11:15:00
 */
public class JSONParseDemo {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public static void main(String[] args) {

        String json = "[\n" +
                "{\n" +
                " \"int_value\": \"0\",\n" +
                " \"last_config_update\": \"0\",\n" +
                " \"param_id\": \"eParamID_DisplayTimecode\",\n" +
                " \"param_type\": \"12\",\n" +
                " \"str_value\": \"00:02:51:27\"\n" +
                " },\n" +
                " {\n" +
                " \"int_value\": \"0\",\n" +
                " \"last_config_update\": \"0\",\n" +
                " \"param_id\": \"eParamID_InputTimecode\",\n" +
                " \"param_type\": \"12\",\n" +
                " \"str_value\": \"00:00:00:00\"\n" +
                "}\n" +
                "]";

        QuadConfigEvent[] event = gson.fromJson(json, QuadConfigEvent[].class);
        System.out.println(event);

        String json1 = gson.toJson(event);
        System.out.printf(json1);

    }
}
