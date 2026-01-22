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

import java.util.UUID;

/**
 * This command informs the video player of the port that this RemoteControl is listening to. The
 * video player uses this port to send {{@link FrameCaptureDoneCmd}} and any
 * {{@link org.mbari.vcr4j.remote.control.commands.localization.LocalizationsCmd}} that were initiated
 * by the video player.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class ConnectCmd extends RCommand<ConnectCmd.Request, ConnectCmd.Response> {

    public static final String COMMAND = "connect";

    public ConnectCmd(int port) {
        this(port, "localhost");
    }

    public ConnectCmd(int port, String host) {
        this(port, host, null);
    }

    public ConnectCmd(int port, String host, UUID uuid) {
        this(new Request(port, host, uuid));
    }

    public ConnectCmd(Request request) {
        super(request);
    }
    

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }


    public static class Request extends RRequest {
        private final int port;
        private final String host;

        public Request(int port, String host) {
            this(port, host, null);
        }

        public Request(int port, String host, UUID uuid) {
            super(COMMAND, null); // we don't need the uuid in the connect command
            this.port = port;
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public String getHost() {
            return host;
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
