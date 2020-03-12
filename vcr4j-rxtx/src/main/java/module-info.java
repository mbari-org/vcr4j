/**
 * @author Brian Schlining
 * @since 2020-03-12T11:20:00
 */
module vcr4j.rxtx {
    exports org.mbari.vcr4j.rxtx;
    requires io.reactivex.rxjava2;
    requires mbarix4j;
    requires org.slf4j;
    requires rxtx.java;
    requires vcr4j.core;
    requires vcr4j.rs422;
}