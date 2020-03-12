/**
 * @author Brian Schlining
 * @since 2020-03-12T11:11:00
 */
module vcr4j.jogl {
    exports org.mbari.vcr4j.jogl;
    requires vcr4j.core;
    requires jogl.all;
    requires io.reactivex.rxjava2;
}