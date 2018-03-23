package org.mbari.vcr4j.ui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Brian Schlining
 * @since 2018-03-22T11:54:00
 */
public class VcrControlPaneControllerDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VcrControlPaneController controller = VcrControlPaneController.newInstance();
        Scene scene = new Scene(controller.getRoot());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
