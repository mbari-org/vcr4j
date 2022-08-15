package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.List;
import java.util.UUID;

/**
 * Command the remote and to update the contained localizations. If the matching UUID is not
 * found in the remote end, those localizations should be ignored.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class UpdateLocalizationsCmd extends LocalizationsPayloadCmd<Localization, UpdateLocalizationsCmd.Request, UpdateLocalizationsCmd.Response> {

    public static final String COMMAND = "update localizations";

    public UpdateLocalizationsCmd(Request value) {
        super(value);
    }

    public UpdateLocalizationsCmd(UUID uuid, List<Localization> localizations) {
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
