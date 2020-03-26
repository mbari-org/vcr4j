/**
 * @author Brian Schlining
 * @since 2020-03-12T13:03:00
 */
open module vcr4j.sharktopoda.client {
    requires io.reactivex.rxjava2;
    requires com.google.gson;
    requires org.slf4j;
    requires jeromq;
    requires javafx.base;
    requires vcr4j.core;
    requires java.prefs;
    exports org.mbari.vcr4j.sharktopoda.client;
    exports org.mbari.vcr4j.sharktopoda.client.localization;
    exports org.mbari.vcr4j.sharktopoda.client.udp;

    requires junit;
    requires vcr4j.sharktopoda;
}