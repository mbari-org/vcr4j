package org.mbari.vcr4j.remote.control.commands.localization;

import org.mbari.vcr4j.remote.control.commands.RRequest;
import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.UUID;

/**
 * Tell the remote end to remove all localizations
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class ClearLocalizationsCmd extends LocalizationsCmd<ClearLocalizationsCmd.Request, ClearLocalizationsCmd.Response> {

    public static final String COMMAND = "clear localizations";

    public ClearLocalizationsCmd(Request value) {
        super(value);
    }

    public static class Request extends RRequest {
        public Request(UUID uuid) {
            super(COMMAND, uuid);
        }
    }

    public static class Response extends RResponse {
        public Response(String status) {
            super(COMMAND, status);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }
}
