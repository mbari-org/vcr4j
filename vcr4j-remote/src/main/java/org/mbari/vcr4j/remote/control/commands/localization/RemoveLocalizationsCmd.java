package org.mbari.vcr4j.remote.control.commands.localization;

/*-
 * #%L
 * vcr4j-remote
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
