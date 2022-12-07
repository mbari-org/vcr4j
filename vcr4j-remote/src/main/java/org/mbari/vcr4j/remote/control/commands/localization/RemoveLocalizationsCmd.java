package org.mbari.vcr4j.remote.control.commands.localization;

import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.List;
import java.util.UUID;

/**
 * Command the remote end to remove localizations
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RemoveLocalizationsCmd extends LocalizationsPayloadCmd<UUID, RemoveLocalizationsCmd.Request, RemoveLocalizationsCmd.Response> {
    public static final String COMMAND = "remove localizations";

    public RemoveLocalizationsCmd(Request value) {
        super(value);
    }

    public RemoveLocalizationsCmd(UUID uuid, List<UUID> localizations) {
        this(new Request(uuid, localizations));
    }

    public static RemoveLocalizationsCmd fromLocalizations(UUID uuid, List<Localization> localizations) {
        return new RemoveLocalizationsCmd(uuid, localizations.stream().map(Localization::getUuid).toList());
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends LocalizationRequest<UUID> {

        public Request(UUID uuid, List<UUID> localizations) {
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
