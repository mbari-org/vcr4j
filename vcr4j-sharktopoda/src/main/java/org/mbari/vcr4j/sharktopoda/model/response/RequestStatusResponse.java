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

import org.mbari.vcr4j.sharktopoda.SharktopodaState;

import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T13:30:00
 */
public class RequestStatusResponse {

    private String response;
    private UUID uuid;
    private String status;

    public RequestStatusResponse(String response, UUID uuid, String status) {
        this.response = response;
        this.uuid = uuid;
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getStatus() {
        return status;
    }

    public SharktopodaState.State getState() {
        if (status.equalsIgnoreCase("playing")) return SharktopodaState.State.PLAYING;
        else if (status.equalsIgnoreCase("shuttling forward")) return SharktopodaState.State.SHUTTLE_FORWARD;
        else if (status.equalsIgnoreCase("shuttling reverse")) return SharktopodaState.State.SHUTTLE_REVERSE;
        else if (status.equalsIgnoreCase("paused")) return SharktopodaState.State.PAUSED;
        else return SharktopodaState.State.NOT_FOUND;
    }
}
