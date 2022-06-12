package org.mbari.vcr4j.remote.control.commands.loc;

import org.mbari.vcr4j.remote.control.commands.RRequest;
import org.mbari.vcr4j.remote.control.commands.RResponse;

import java.util.List;
import java.util.UUID;

public class UpdateLocalizationsCmd extends LocalizationsCmd<UpdateLocalizationsCmd.Request, UpdateLocalizationsCmd.Response> {

    public static final String Command = "update localizations";

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

    public static class Request extends RRequest {

        private List<Localization> localizations;

        public Request(UUID uuid, List<Localization> localizations) {
            super(Command, uuid);
            this.localizations = List.copyOf(localizations);
        }

        public List<Localization> getLocalizations() {
            return localizations;
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
