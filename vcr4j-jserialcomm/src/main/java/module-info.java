/**
 * @author Brian Schlining
 * @since 2020-03-12T11:12:00
 */
module vcr4j.jserialcomm {
    requires com.fazecast.jSerialComm;
    requires vcr4j.rs422;
    requires vcr4j.core;
    requires org.slf4j;
    exports org.mbari.vcr4j.jserialcomm;
}