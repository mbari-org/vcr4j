package org.mbari.vcr4j.sharktopoda.client.localization;

import java.util.Collections;
import java.util.List;

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
    public static String ACTION_DELETE = "remove";

    /**
     * Clear the localizations and set new ones.
     */
    public static String ACTION_SET = "set";

    /**
     * Clear all localizations from the controller
     */
    public static String ACTION_CLEAR_VIDEO = "clear";

    public static String ACTION_CLEAR_ALL = "clear all";

    /**
     * add, delete
     */
    String action;
    List<Localization> localizations;

    public Message() {
    }

    public Message(String action, Localization localization) {
        this(action, List.of(localization));
    }

    public Message(String action, List<Localization> localizations) {
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
