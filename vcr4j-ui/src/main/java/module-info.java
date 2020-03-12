/**
 * @author Brian Schlining
 * @since 2020-03-12T11:30:00
 */
module vcr4j.ui {
    requires com.jfoenix;
    requires de.jensd.fx.fontawesomefx.commons;
    requires de.jensd.fx.fontawesomefx.materialicons;
    requires io.reactivex.rxjava2;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires vcr4j.core;
    requires vcr4j.rs422;
    requires vcr4j.rxtx;
    requires java.desktop;
    requires org.slf4j;
    requires mbarix4j;
    requires vcr4j.v2.adapter;
    exports org.mbari.vcr4j.ui.javafx;
    exports org.mbari.vcr4j.ui.swing;
    opens org.mbari.vcr4j.ui.javafx to javafx.fxml;
}