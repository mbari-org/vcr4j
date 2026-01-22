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
 * Base class for all remote responses.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public abstract class RResponse {

    public static final String OK = "ok";
    public static final String FAILED = "failed";
    private final String response;
    private final String status;
    private final String cause; // Cause of failure. Optional

    public RResponse(String response, String status) {
        this(response, status, null);

    }

    public RResponse(String response, String status, String cause) {
        this.response = response;
        this.status = status;
        this.cause = cause;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public String getCause() {
        return cause;
    }

    public boolean isOk() {
        return status != null && status.equalsIgnoreCase(OK);
    }

    public abstract boolean success();
}
