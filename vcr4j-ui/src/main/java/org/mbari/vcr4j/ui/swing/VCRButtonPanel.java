/*
 * @(#)VCRButtonPanel.java   2009.02.24 at 09:44:54 PST
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
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;

import javafx.beans.property.ObjectProperty;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Container for VCRButtons. This panel is mostly self contained but it
 * should be used as follows:</p>
 *
 * <pre>
 * // VCRAdapter is just a place holder for a real VCRProxy
 * // Alternatively this is the same as new VCRButtonPanel(new VCRProxy("COM1"))
 * VCRButtonPanel bp = new VCRButtonPanel(new VCRAdaper());
 * bp.setVcr(new VCRProxy());
 *
 * // In addition to a VCR a JSLider is used for setting the shuttle speeds.
 * bp.setSlider(new JSlider());
 * </pre>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class VCRButtonPanel extends JPanel {


    private static final Logger log = LoggerFactory.getLogger(VCRButtonPanel.class);

    final VCRButton btnPlay;


    final VCRButton btnStop;


    final VCRShuttleButton btnShuttleReverse ;


    final VCRShuttleButton btnShuttleForward;


    final VCRButton btnRewind ;


    final VCRButton btnGoto;


    final VCRButton btnFastForward ;


    final VCRButton btnEject;


    final GridLayout layout = new GridLayout(2, 4, 1, 1);


    private JSlider slider;


    private final ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController;


    public VCRButtonPanel(ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController) {
        super();
        this.videoController = videoController;
        btnEject = new VCREjectButton(videoController);
        btnFastForward = new VCRFastForwardButton(videoController);
        btnGoto = new VCRGotoButton(videoController);
        btnPlay = new VCRPlayButton(videoController);
        btnRewind = new VCRRewindButton(videoController);
        btnShuttleForward = new VCRShuttleForwardButton(videoController);
        btnShuttleReverse = new VCRShuttleReverseButton(videoController);
        btnStop = new VCRStopButton(videoController);

        videoController.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                newVal.requestStatus();
            }
        });

        try {
            initialize();
        }
        catch (Exception ex) {
            log.error("Failed to initialize", ex);
        }
    }


    public JSlider getSlider() {
        return slider;
    }


    void initialize() throws Exception {
        this.setLayout(layout);
        layout.setHgap(0);

        // Seek Button
        btnGoto.setMinimumSize(new Dimension(35, 35));

        // Fast forward button
        btnFastForward.setMinimumSize(new Dimension(35, 35));

        // Rewind button
        btnRewind.setMinimumSize(new Dimension(35, 35));

        // Eject button
        btnEject.setMinimumSize(new Dimension(35, 35));

        // Stop button
        btnStop.setMinimumSize(new Dimension(35, 35));

        // Play button
        btnPlay.setMinimumSize(new Dimension(35, 35));

        // Play button
        btnShuttleForward.setMinimumSize(new Dimension(35, 35));

        // Play button
        btnShuttleReverse.setMinimumSize(new Dimension(35, 35));


        add(btnGoto);
        add(btnShuttleReverse);
        add(btnPlay);
        add(btnShuttleForward);
        add(btnEject);
        add(btnRewind);
        add(btnStop);
        add(btnFastForward);
    }

    /**
     * The shuttle forward and revers butons depend on getting info from the slider. Use this method to set the
     * slider to use for controlling speed.
     * 
     * @param slider The slider we're using for control
     */
    public void setSlider(final JSlider slider) {
        this.slider = slider;
        btnShuttleForward.setSlider(slider);
        btnShuttleReverse.setSlider(slider);
    }

}
