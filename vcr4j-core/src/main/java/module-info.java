/**
 * @author Brian Schlining
 * @since 2020-03-12T11:05:00
 */
module vcr4j.core {
    exports org.mbari.vcr4j;
    exports org.mbari.vcr4j.commands;
    exports org.mbari.vcr4j.decorators;
    exports org.mbari.vcr4j.time;
    exports org.mbari.vcr4j.util;
    requires io.reactivex.rxjava3;
    requires org.reactivestreams;
    requires org.slf4j;
}