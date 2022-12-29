/**
 * @author Brian Schlining
 * @since 2020-03-12T11:22:00
 */
module vcr4j.sharktopoda {

    requires com.google.gson;
    requires io.reactivex.rxjava3;
    requires vcr4j.core;
    
    opens org.mbari.vcr4j.sharktopoda.commands to com.google.gson;
    opens org.mbari.vcr4j.sharktopoda.model to com.google.gson;
    opens org.mbari.vcr4j.sharktopoda.model.request to com.google.gson;
    opens org.mbari.vcr4j.sharktopoda.model.response to com.google.gson;

    exports org.mbari.vcr4j.sharktopoda.commands;
    exports org.mbari.vcr4j.sharktopoda.decorators;
    exports org.mbari.vcr4j.sharktopoda.model.request;
    exports org.mbari.vcr4j.sharktopoda.model.response;
    exports org.mbari.vcr4j.sharktopoda.model;
    exports org.mbari.vcr4j.sharktopoda;
    
}