/*
 * @(#)VCRPanel.java   2009.02.24 at 09:44:53 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr4j.ui.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This panel provides VCR controls for the annotation GUI. To use this
 * panel as a standalone control run org.mbari.vims.annotation.bui.vcr.VCRFrame
 * </p>
 *
 * @author : $Author: hohonuuli $
 * @version : $Id: VCRPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 *
 */
public class VCRPanel extends JPanel {

    /**
     *
     */
    private static final Logger log = LoggerFactory.getLogger(VCRPanel.class);

    private VCRShuttleSpeedPanel sliderPanel;

    private JTextField timeCodeField;

    private JPanel timeCodePanel;    // Panel to hold the time code

    private ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController = new SimpleObjectProperty<>();

    private VCRButtonPanel vcrButtonPanel;    // VCR Buttons

    // Construct the panel

    /**
     * Constructs ...
     *
     */
    public VCRPanel() {
        try {
            initialize();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void disconnect() {
        Optional.ofNullable(videoController.get())
                .ifPresent(vc -> vc.getVideoIO().close());
        getTimeCodeField().setText("NO VIDEO");
    }

    /**
     *
     */
    public void finalize() {
        try {
            super.finalize();
            Optional.ofNullable(videoControllerProperty().get())
                    .ifPresent(vc -> vc.getVideoIO().close());
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     *     @return  the sliderPanel
     */
    public VCRShuttleSpeedPanel getSliderPanel() {
        if (sliderPanel == null) {
            sliderPanel = new VCRShuttleSpeedPanel();
        }

        return sliderPanel;
    }

    /**
     *     @return  the timeCodeField
     */
    JTextField getTimeCodeField() {
        if (timeCodeField == null) {
            timeCodeField = new TimeCodeField(videoController);
        }

        return timeCodeField;
    }


    JPanel getTimeCodePanel() {
        if (timeCodePanel == null) {
            timeCodePanel = new JPanel();
            timeCodePanel.setMinimumSize(new Dimension(180, 100));
            timeCodePanel.setPreferredSize(new Dimension(180, 100));
            timeCodePanel.add(getTimeCodeField());
            timeCodePanel.add(getSliderPanel());
        }

        return timeCodePanel;
    }

    /**
     *     Access the current timecode from the VCR
     *
     *     @return A Timecode object. To get a string formatted in HH:MM:SS:FF
     *             use toString(). The parts of the timecode can be accessed
     *             individually using getHour(), getMInute(), getSecond(), and
     *             getFrame(). Null is returned if no Timecode object is returned
     */
    public VideoIndex getVideoIndex() {
        VideoIndex videoIndex = null;

        if (videoController.get() != null) {
            CompletableFuture<VideoIndex> future = new CompletableFuture<>();
            videoController.get().getIndexObservable()
                    .take(1)
                    .forEach(future::complete);
            try {
                videoIndex = future.get(2, TimeUnit.SECONDS);
            }
            catch (Exception e) {

            }
        }

        return videoIndex;
    }



    VCRButtonPanel getVcrButtonPanel() {
        if (vcrButtonPanel == null) {
            vcrButtonPanel = new VCRButtonPanel(videoController);

            /*
             * Don't forget to associate the speed slider with the button panel.
             * Otherwise the button panels shuttle funcions don't work.
             */
            vcrButtonPanel.setSlider(getSliderPanel().getSlider());
        }

        return vcrButtonPanel;
    }


    private void initialize() throws Exception {
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);

        layout.setHgap(10);
        setLayout(layout);
        add(getTimeCodePanel());
        add(getVcrButtonPanel());

        videoController.addListener((obs, oldVal, newVal) -> {
            getTimeCodeField().setText("NO VIDEO");
            newVal.requestStatus();
        });

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
}
