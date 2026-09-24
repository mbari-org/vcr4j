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

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-27T15:23:00
 */
public class PlayResponse {
    private String response;
    private String status;
    private UUID uuid;


    public PlayResponse(UUID uuid, String status) {
        this.response = "open";
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public UUID getUuid() {
        return uuid;
    }
}
