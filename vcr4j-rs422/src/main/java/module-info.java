/**
 * @author Brian Schlining
 * @since 2020-03-12T11:13:00
 */
module vcr4j.rs422 {
    requires vcr4j.core;
    requires io.reactivex.rxjava3;
    exports org.mbari.vcr4j.rs422;
    exports org.mbari.vcr4j.rs422.commands;
    exports org.mbari.vcr4j.rs422.decorators;
    exports org.mbari.vcr4j.rs422.util;
}