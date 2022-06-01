/**
 * @author Brian Schlining
 * @since 2020-03-12T11:08:00
 */
module vcr4j.javafx {
    exports org.mbari.vcr4j.javafx;
    exports org.mbari.vcr4j.javafx.decorators;
    requires io.reactivex.rxjava3;
    requires javafx.media;
    requires vcr4j.core;
}