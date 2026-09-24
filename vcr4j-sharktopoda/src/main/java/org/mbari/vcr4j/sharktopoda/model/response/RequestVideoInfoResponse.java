/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.sharktopoda.model.response;

import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:25:00
 */
public class RequestVideoInfoResponse implements IVideoInfo {

    private String response;
    private UUID uuid;
    private URL url;

    public RequestVideoInfoResponse(UUID uuid, URL url) {
        this.response = "request video information";
        this.uuid = uuid;
        this.url = url;
    }

    public String getResponse() {
        return response;
    }

    public UUID getUuid() {
        return uuid;
    }

    public URL getUrl() {
        return url;
    }
}
