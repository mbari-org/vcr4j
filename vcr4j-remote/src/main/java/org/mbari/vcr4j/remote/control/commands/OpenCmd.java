package org.mbari.vcr4j.remote.control.commands;

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

import java.net.URL;
import java.util.UUID;

/**
 * Open a video.
 */
public class OpenCmd extends RCommand<OpenCmd.Request, OpenCmd.Response> {
    public static final String COMMAND = "open";

    public OpenCmd(UUID uuid, URL url) {
        this(new Request(uuid, url));
    }

    public OpenCmd(Request request) {
        super(request);
    }

    public static class Request extends RRequest {

        private final URL url;

        public Request(UUID uuid, URL url) {
            super(COMMAND, uuid);
            this.url = url;
        }

        public URL getUrl() {
            return url;
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
