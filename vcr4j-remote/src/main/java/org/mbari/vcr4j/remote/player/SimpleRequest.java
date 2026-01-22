package org.mbari.vcr4j.remote.player;

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

/**
 *  This is used as a video control to do a first pass at deserializing an incoming command/request.
 *  It parses the command portion of JSON only. The raw field contains the entire JSON of the
 *  original request.
 * @author Brian Schlining
 * @since 2022-08-08
 */
public class SimpleRequest {
    private String command;

    private String raw;

    public SimpleRequest(String command, String raw) {
        this.command = command;
        this.raw = raw;
    }

    public SimpleRequest(String command) {
        this(command, null);
    }

    public String getCommand() {
        return command;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }


}
