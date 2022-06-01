/**
 * @author Brian Schlining
 * @since 2020-03-12T14:21:00
 */
open module vcr4j.udp {
    exports org.mbari.vcr4j.udp;
    requires io.reactivex.rxjava3;
    requires org.slf4j;
    requires vcr4j.core;
    requires junit;
}