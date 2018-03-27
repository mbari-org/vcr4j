package org.mbari.vcr4j.ui.javafx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.GlyphsFactory;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
import io.reactivex.disposables.Disposable;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;

/**
 * @author Brian Schlining
 * @since 2018-03-21T16:23:00
 */
public class VcrControlPaneController {

    private ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController =
            new SimpleObjectProperty<>();
    private Disposable indexDisposable;

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

        videoController.addListener((obs, oldv, newv) -> {
            if (indexDisposable != null) {
                indexDisposable.dispose();
                indexDisposable = null;
            }

            boolean disable = true;
            if (videoController != null) {
                indexDisposable = newv.getIndexObservable()
                        .subscribe(vi ->  vi.getTimecode()
                                .ifPresent(tc -> Platform.runLater(() ->
                                    timecodeLabel.setText(tc.toString()))));
                disable = false;
            }
            shuttleReverseButton.setDisable(disable);
            shuttleFwdButton.setDisable(disable);
            playButton.setDisable(disable);
            rewindButton.setDisable(disable);
            fastforwardButton.setDisable(disable);
            stopButton.setDisable(disable);
            //gotoButton.setDisable(disable); // TODO uncomment when implemented
        });

        String size = "35px";
        GlyphsFactory gf = MaterialIconFactory.get();
        final Text shuttleFwdIcon = gf.createIcon(MaterialIcon.ARROW_FORWARD, size);
        shuttleFwdButton.setGraphic(shuttleFwdIcon);
        shuttleFwdButton.setOnAction(e -> doShuttleForward());
        shuttleFwdButton.setDisable(true);

        final Text shuttleReverseIcon = gf.createIcon(MaterialIcon.ARROW_BACK, size);
        shuttleReverseButton.setGraphic(shuttleReverseIcon);
        shuttleReverseButton.setOnAction(e -> doShuttleReverse());
        shuttleReverseButton.setDisable(true);

        final Text playIcon = gf.createIcon(MaterialIcon.PLAY_ARROW, size);
        playButton.setGraphic(playIcon);
        playButton.setOnAction(e -> doPlay());
        playButton.setDisable(true);

        final Text gotoIcon = gf.createIcon(MaterialIcon.SEARCH, size);
        gotoButton.setGraphic(gotoIcon);
        gotoButton.setOnAction(e -> doSeek());
        gotoButton.setDisable(true); // TODO Disable until we implement this

        final Text fwdIcon = gf.createIcon(MaterialIcon.FAST_FORWARD, size);
        fastforwardButton.setGraphic(fwdIcon);
        fastforwardButton.setOnAction(e -> doFastForward());
        fastforwardButton.setDisable(true);

        final Text rewindIcon = gf.createIcon(MaterialIcon.FAST_REWIND, size);
        rewindButton.setGraphic(rewindIcon);
        rewindButton.setOnAction(e -> doRewind());
        rewindButton.setDisable(true);

        final Text stopIcon = gf.createIcon(MaterialIcon.STOP, size);
        stopButton.setGraphic(stopIcon);
        stopButton.setOnAction(e -> doStop());
        stopButton.setDisable(true);

        final Text ejectIcon = gf.createIcon(MaterialIcon.EJECT, size);
        ejectButton.setGraphic(ejectIcon);
        ejectButton.setOnAction(e -> doEject());
        ejectButton.setDisable(true);
        setVideoController(null);

    }

    private void doShuttleForward() {
        Optional.ofNullable(videoController.get())
                .ifPresent(vc -> {
                    double value = speedSlider.getValue();
                    vc.shuttle(value / 255D);
                });
    }

    private void doShuttleReverse() {
        Optional.ofNullable(videoController.get())
                .ifPresent(vc -> {
                    double value = -1 * speedSlider.getValue();
                    vc.shuttle(value / 255D);
                });
    }

    private void doPlay() {
        Optional.ofNullable(videoController.get())
                .ifPresent(VideoController::play);
    }

    private void doSeek() {
        // TODO show timecode selection dialog
    }

    private void doFastForward() {
        Optional.ofNullable(videoController.get())
                .ifPresent(VideoController::fastForward);
    }

    private void doRewind() {
        Optional.ofNullable(videoController.get())
                .ifPresent(VideoController::rewind);
    }

    private void doStop() {
        Optional.ofNullable(videoController.get())
                .ifPresent(VideoController::stop);
    }

    private void doEject() {
        Optional.ofNullable(videoController.get())
                .ifPresent(vc -> vc.send(RS422VideoCommands.EJECT));
    }

    public VideoController<? extends VideoState, ? extends VideoError> getVideoController() {
        return videoController.get();
    }

    public ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoControllerProperty() {
        return videoController;
    }

    public void setVideoController(VideoController<? extends VideoState, ? extends VideoError> videoController) {
        this.videoController.set(videoController);
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

