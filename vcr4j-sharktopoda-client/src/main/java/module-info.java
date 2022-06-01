/**
 * @author Brian Schlining
 * @since 2020-03-12T11:24:00
 */
module vcr4j.sharktopoda.client {
    requires io.reactivex.rxjava3;
    requires com.google.gson;
    requires org.slf4j;
    requires jeromq;
    requires javafx.base;
    requires vcr4j.core;
    requires java.prefs;

    opens org.mbari.vcr4j.sharktopoda.client.localization to com.google.gson;
    opens org.mbari.vcr4j.sharktopoda.client.model to com.google.gson;

    exports org.mbari.vcr4j.sharktopoda.client;
    exports org.mbari.vcr4j.sharktopoda.client.localization;
    exports org.mbari.vcr4j.sharktopoda.client.decorators;
    exports org.mbari.vcr4j.sharktopoda.client.gson;
    exports org.mbari.vcr4j.sharktopoda.client.model;
    exports org.mbari.vcr4j.sharktopoda.client.udp;

}