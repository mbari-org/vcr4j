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
