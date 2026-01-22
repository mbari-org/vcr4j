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
import java.util.List;
import java.util.UUID;

/**
 * Request information about all open videos
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class RequestAllVideoInfosCmd
        extends RCommand<RequestAllVideoInfosCmd.Request, RequestAllVideoInfosCmd.Response> {

    public static final String COMMAND = "request all information";


    public RequestAllVideoInfosCmd() {
        super(new Request());
    }

    @Override
    public Class<Response> responseType() {
        return Response.class;
    }

    public static class Request extends RRequest {

        public Request() {
            super(COMMAND, null);
        }

        public String getCommand() {
            return command;
        }
    }


    public static class Response extends RResponse {
        private List<VideoInfoBean> videos;

        public Response(List<VideoInfo> videos) {
            super(COMMAND, null);
            this.videos = videos.stream()
                    .map(VideoInfoBean::from)
                    .toList();

        }

        public List<VideoInfoBean> getVideos() {
            return videos;
        }

        @Override
        public boolean success() {
            return true;
        }
    }
}
