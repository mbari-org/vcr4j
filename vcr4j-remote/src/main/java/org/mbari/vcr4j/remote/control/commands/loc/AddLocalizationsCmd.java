package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.List;
import java.util.UUID;

/**
 * Tell the remote end to add the following localizations
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class AddLocalizationsCmd extends LocalizationsPayloadCmd<Localization, AddLocalizationsCmd.Request, AddLocalizationsCmd.Response> {

    public static final String COMMAND = "add localizations";

    public AddLocalizationsCmd(Request value) {
        super(value);
    }

    public AddLocalizationsCmd(UUID uuid, List<Localization> localizations) {
        this(new Request(uuid, localizations));
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends LocalizationRequest<Localization> {

        public Request(UUID uuid, List<Localization> localizations) {
            super(COMMAND, uuid, localizations);
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
}
