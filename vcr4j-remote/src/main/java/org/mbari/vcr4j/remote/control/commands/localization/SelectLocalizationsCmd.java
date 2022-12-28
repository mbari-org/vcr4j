package org.mbari.vcr4j.remote.control.commands.localization;

import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.List;
import java.util.UUID;

public class SelectLocalizationsCmd extends LocalizationsPayloadCmd<UUID, SelectLocalizationsCmd.Request, SelectLocalizationsCmd.Response> {
    public static final String COMMAND = "select localizations";

    public SelectLocalizationsCmd(SelectLocalizationsCmd.Request value) {
        super(value);
    }

    public SelectLocalizationsCmd(UUID uuid, List<UUID> localizations) {
        this(new SelectLocalizationsCmd.Request(uuid, localizations));
    }

    public static SelectLocalizationsCmd fromLocalizations(UUID uuid, List<Localization> localizations) {
        return new SelectLocalizationsCmd(uuid, localizations.stream().map(Localization::getUuid).toList());
    }

    @Override
    public Class<SelectLocalizationsCmd.Response> responseType() {
        return SelectLocalizationsCmd.Response.class;
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