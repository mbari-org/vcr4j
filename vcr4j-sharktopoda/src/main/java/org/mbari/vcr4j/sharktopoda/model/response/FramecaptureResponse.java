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

import java.io.File;
import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:33:00
 */
public class FramecaptureResponse {
    private String response = "framecapture";
    private long elapsedTimeMillis;
    private UUID imageReferenceUuid;
    private URL imageLocation;
    private String status;

    public FramecaptureResponse(long elapsedTimeMillis, UUID imageReferenceUuid, URL imageLocation, String status) {
        this.elapsedTimeMillis = elapsedTimeMillis;
        this.imageReferenceUuid = imageReferenceUuid;
        this.imageLocation = imageLocation;
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public UUID getImageReferenceUuid() {
        return imageReferenceUuid;
    }

    public URL getImageLocation() {
        return imageLocation;
    }

    public String getStatus() {
        return status;
    }
}
