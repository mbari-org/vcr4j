package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RCommand;
import org.mbari.vcr4j.remote.control.commands.RRequest;
import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.List;
import java.util.UUID;

public class AddLocalizationsCmd extends LocalizationsPayloadCmd<Localization, AddLocalizationsCmd.Request, AddLocalizationsCmd.Response> {

    public static final String Command = "add localizations";

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
            super(Command, uuid, localizations);
        }

    }

    public static class Response extends RResponse {
        public Response(String status) {
            super(Command, status);
        }

        @Override
        public boolean success() {
            return isOk();
        }
    }
}
