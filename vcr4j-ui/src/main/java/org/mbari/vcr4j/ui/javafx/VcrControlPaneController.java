package org.mbari.vcr4j.ui.javafx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.GlyphsFactory;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * @author Brian Schlining
 * @since 2018-03-21T16:23:00
 */
public class VcrControlPaneController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane root;

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
        String size = "35px";
        GlyphsFactory gf = MaterialIconFactory.get();
        final Text shuttleFwdIcon = gf.createIcon(MaterialIcon.ARROW_FORWARD, size);
        shuttleFwdButton.setGraphic(shuttleFwdIcon);

        final Text shuttleReverseIcon = gf.createIcon(MaterialIcon.ARROW_BACK, size);
        shuttleReverseButton.setGraphic(shuttleReverseIcon);

        final Text playIcon = gf.createIcon(MaterialIcon.PLAY_ARROW, size);
        playButton.setGraphic(playIcon);

        final Text gotoIcon = gf.createIcon(MaterialIcon.SEARCH, size);
        gotoButton.setGraphic(gotoIcon);

        final Text fwdIcon = gf.createIcon(MaterialIcon.FAST_FORWARD, size);
        fastforwardButton.setGraphic(fwdIcon);

        final Text rewindIcon = gf.createIcon(MaterialIcon.FAST_REWIND, size);
        rewindButton.setGraphic(rewindIcon);

        final Text stopIcon = gf.createIcon(MaterialIcon.STOP, size);
        stopButton.setGraphic(stopIcon);

        final Text ejectIcon = gf.createIcon(MaterialIcon.EJECT, size);
        ejectButton.setGraphic(ejectIcon);

    }

    public AnchorPane getRoot() {
        return root;
    }

    public static VcrControlPaneController newInstance() {
        FXMLLoader loader = new FXMLLoader(VcrControlPaneController.class
                .getResource("/fxml/VcrControlPane.fxml"));

        try {
            loader.load();
            return loader.getController();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load VcrControlPane from FXML", e);
        }
    }
}

