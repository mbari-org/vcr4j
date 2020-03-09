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
    public static String ACTION_ADD = "add";

    /**
     * Removes localizations from the existing set. Remote apps
     * should remove them from persistent storage
     */
    public static String ACTION_REMOVE = "remove";

    public static String ACTION_CLEAR = "clear";

    public static String ACTION_SELECT = "select";

    public static String ACTION_DESELECT = "deselect";

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

}
