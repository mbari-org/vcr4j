/**
 * @author Brian Schlining
 * @since 2020-03-12T11:16:00
 */
module vcr4j.kipro {
    requires vcr4j.core;
    requires unirest.java;
    requires io.reactivex.rxjava2;
    requires org.slf4j;
    requires com.google.gson;
    exports org.mbari.vcr4j.kipro;
    exports org.mbari.vcr4j.kipro.commands;
    exports org.mbari.vcr4j.kipro.decorators;
    exports org.mbari.vcr4j.kipro.examples;
    exports org.mbari.vcr4j.kipro.json;
}