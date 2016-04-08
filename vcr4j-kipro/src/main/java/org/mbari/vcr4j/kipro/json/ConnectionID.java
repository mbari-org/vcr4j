package org.mbari.vcr4j.kipro.json;

import com.google.gson.Gson;

/**
 * @author Brian Schlining
 * @since 2016-02-10T11:15:00
 */
public class ConnectionID {

    private final int connectionid;
    private static final Gson gson = Constants.GSON;
    public static ConnectionID NO_CONNECTION_ID = new ConnectionID(0);

    public ConnectionID(int connectionid) {
        this.connectionid = connectionid;
    }

    public int getConnectionid() {
        return connectionid;
    }

    public static ConnectionID fromJSON(String json) {
        return  gson.fromJson(json, ConnectionID.class);
    }

    public String toJSON() {
        return gson.toJson(this);
    }

    public static String buildRequest(String httpAddress) {
        return httpAddress + "config?action=connect";
    }

}
