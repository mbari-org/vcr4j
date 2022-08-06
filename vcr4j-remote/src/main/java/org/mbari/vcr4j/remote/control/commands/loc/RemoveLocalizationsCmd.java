package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RRequest;
import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.List;
import java.util.UUID;

public class RemoveLocalizationsCmd extends LocalizationsPayloadCmd<UUID, RemoveLocalizationsCmd.Request, RemoveLocalizationsCmd.Response> {
    public static final String Command = "remove localizations";

    public RemoveLocalizationsCmd(Request value) {
        super(value);
    }

    public RemoveLocalizationsCmd(UUID uuid, List<UUID> localizations) {
        this(new Request(uuid, localizations));
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends LocalizationRequest<UUID> {

        public Request(UUID uuid, List<UUID> localizations) {
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
