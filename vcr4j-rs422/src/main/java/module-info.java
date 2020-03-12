/**
 * @author Brian Schlining
 * @since 2020-03-12T11:13:00
 */
module vcr4j.rs422 {
    requires mbarix4j;
    requires vcr4j.core;
    requires org.slf4j;
    requires io.reactivex.rxjava2;
    exports org.mbari.vcr4j.rs422;
    exports org.mbari.vcr4j.rs422.commands;
    exports org.mbari.vcr4j.rs422.decorators;
}