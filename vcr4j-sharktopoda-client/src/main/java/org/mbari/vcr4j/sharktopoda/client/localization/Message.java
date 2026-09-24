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
package org.mbari.vcr4j.sharktopoda.client.localization;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2020-02-12T10:58:00
 */
public class Message {

    /**
     * Add localizations to existing set. This will add/replace
     * localizations based on their localizationUuid. Remote apps
     * should add/update to persistent storage
     */
    public static final String ACTION_ADD = "add";

    /**
     * Removes localizations from the existing set. Remote apps
     * should remove them from persistent storage
     */
    public static final String ACTION_REMOVE = "remove";

    public static final String ACTION_CLEAR = "clear";

    public static final String ACTION_SELECT = "select";

    public static final String ACTION_DESELECT = "deselect";

    /**
     * add, delete
     */
    String action;
    List<Localization> localizations;

    public Message() {
    }

    public Message(String action) {
        this(action, Collections.emptyList());
    }

    public Message(String action, Localization localization) {
        this(action, List.of(localization));
    }

    public Message(String action,  List<Localization> localizations) {
        this.action = action;
        this.localizations = Collections.unmodifiableList(localizations);
    }

    public String getAction() {
        return action;
    }

    public List<Localization> getLocalizations() {
        return localizations;
    }

    @Override
    public String toString() {
        return "Message{" + "action=" + action + ", localizations=" + localizations + '}';
    }
    
    

}
