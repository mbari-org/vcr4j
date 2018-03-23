package org.mbari.vcr4j.ui.javafx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author Brian Schlining
 * @since 2018-03-21T16:23:00
 */
public class VcrControlPaneController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label timecodeLabel;

    @FXML
    private JFXButton shuttleFwdButton;

    @FXML
    private JFXButton playButton;

    @FXML
    private JFXButton shuttleReverseButton;

    @FXML
    private JFXButton gotoButton;

    @FXML
    private JFXButton fastforwardButton;

    @FXML
    private JFXButton stopButton;

    @FXML
    private JFXButton rewindButton;

    @FXML
    private JFXButton ejectButton;

    @FXML
    private JFXSlider speedSlider;

    @FXML
    void initialize() {

    }
}

