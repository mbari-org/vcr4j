/**
 * @author Brian Schlining
 * @since 2020-03-12T11:19:00
 */
module vcr4j.purejavacomm {
    requires vcr4j.rs422;
    requires vcr4j.core;
    requires org.slf4j;
    requires purejavacomm;
    requires io.reactivex.rxjava3;
    exports org.mbari.vcr4j.purejavacomm;
}