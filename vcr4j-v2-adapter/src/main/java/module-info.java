/**
 * @author Brian Schlining
 * @since 2020-03-12T11:34:00
 */
module vcr4j.v2.adapter {
    exports org.mbari.vcr4j.adapter.noop;
    exports org.mbari.vcr4j.adapter;
    requires io.reactivex.rxjava2;
    requires javafx.base;
    requires mbarix4j;
    requires org.slf4j;
    requires vcr4j.core;
    requires vcr4j.rs422;
}