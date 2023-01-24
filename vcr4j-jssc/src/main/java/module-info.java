/**
 * @author Brian Schlining
 * @since 2020-03-12T11:15:00
 */
module vcr4j.jssc {
    requires io.reactivex.rxjava3;
    requires jssc;
    requires vcr4j.core;
    requires vcr4j.rs422;
    requires org.slf4j;
    exports org.mbari.vcr4j.jssc;

}