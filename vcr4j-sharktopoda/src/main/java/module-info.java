/**
 * @author Brian Schlining
 * @since 2020-03-12T11:22:00
 */
module vcr4j.sharktopoda {
    exports org.mbari.vcr4j.sharktopoda.commands;
    exports org.mbari.vcr4j.sharktopoda.decorators;
    exports org.mbari.vcr4j.sharktopoda.model;
    exports org.mbari.vcr4j.sharktopoda;
    requires com.google.gson;
    requires io.reactivex.rxjava2;
    requires org.slf4j;
    requires vcr4j.core;
}