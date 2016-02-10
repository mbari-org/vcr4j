package org.mbari.vcr4j.kipro;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Brian Schlining
 * @since 2016-02-04T17:00:00
 */
public class QuadResponseParser {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public void update(String json) {

    }

    private class ConnectionID {
        private int connectionID;

        public ConnectionID(int connectionID) {
            this.connectionID = connectionID;
        }

        public int getConnectionID() {
            return connectionID;
        }
    }

}
