package org.mbari.vcr4j.remote.player;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.mbari.vcr4j.remote.control.RVideoIO;
import org.mbari.vcr4j.remote.control.commands.NoopCmd;
import org.mbari.vcr4j.remote.control.commands.RResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer {

    private static final Logger log = LoggerFactory.getLogger(PlayerIO.class);
    private static final Gson gson = RVideoIO.GSON;

    private final int port;
    private final RequestHandler requestHandler;

    private final String secret;

    private Javalin app;

    public TcpServer(int port, String secret, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        this.secret = secret;
        init();
    }

    private void init() {
        app = Javalin.create().start(port);
        app.post("/", this::handleContext);
    }

    private String handleContext(Context ctx) {
        var apiKey = ctx.header("X-API-Key");
        var body = ctx.body();
        var simpleRequest = gson.fromJson(body, SimpleRequest.class);
        simpleRequest.setRaw(body);
        if (secret.equals(apiKey)) {
            try {
                var response = requestHandler.composeResponse(simpleRequest);
                return gson.toJson(response);
            }
            catch (Exception e) {
                var response = requestHandler.handleError(simpleRequest, e);
                return gson.toJson(response);
            }
        }
        else {
            var response = new NoopCmd.Response(simpleRequest.getCommand(), "failed");
            return gson.toJson(response);
        }

    }
}
