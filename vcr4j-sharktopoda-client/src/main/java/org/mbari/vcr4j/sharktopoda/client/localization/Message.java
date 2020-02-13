package org.mbari.vcr4j.sharktopoda.client.localization;

/**
 * @author Brian Schlining
 * @since 2020-02-12T10:58:00
 */
public class Message {

    public static String ACTION_ADD = "add";
    public static String ACTION_DELETE = "remove";

    /**
     * add, delete
     */
    String action;
    Localization localization;

    public Message() {
    }

    public Message(String action, Localization localization) {
        this.action = action;
        this.localization = localization;
    }

    public String getAction() {
        return action;
    }

    public Localization getLocalization() {
        return localization;
    }
}
