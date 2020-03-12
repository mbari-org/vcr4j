/**
 * @author Brian Schlining
 * @since 2020-03-12T11:40:00
 */
module vcr4j.vlc.http {
    requires com.google.gson;
    requires io.reactivex.rxjava2;
    requires vcr4j.core;
    requires org.slf4j;
    requires okhttp3;
    exports org.mbari.vcr4j.vlc.http;
    exports org.mbari.vcr4j.vlc.http.commands;
    exports org.mbari.vcr4j.vlc.http.model;
}