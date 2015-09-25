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



package org.mbari.vcr4j.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.mbari.util.IObserver;
import org.mbari.vcr4j.IVCR;
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
 * </p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class VCRButtonPanel extends JPanel implements IObserver {

    /**
     *
     */
    private static final long serialVersionUID = 3053553700981767287L;
    private static final Logger log = LoggerFactory.getLogger(VCRButtonPanel.class);

    /**
         * @uml.property  name="btnPlay"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRButton btnPlay = new VCRPlayButton();

    /**
         * @uml.property  name="btnStop"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRButton btnStop = new VCRStopButton();

    /**
         * @uml.property  name="btnShuttleReverse"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRShuttleButton btnShuttleReverse = new VCRShuttleReverseButton();

    /**
         * @uml.property  name="btnShuttleForward"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRShuttleButton btnShuttleForward = new VCRShuttleForwardButton();

    /**
         * @uml.property  name="btnRewind"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRButton btnRewind = new VCRRewindButton();

    /**
         * @uml.property  name="btnGoto"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRButton btnGoto = new VCRGotoButton();

    /**
         * @uml.property  name="btnFastForward"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRButton btnFastForward = new VCRFastForwardButton();

    /**
         * @uml.property  name="btnEject"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    final VCRButton btnEject = new VCREjectButton();

    // final VCRButton btnGrabFrame = new FrameCaptureButton();

    /**
         * @uml.property  name="layout"
         */
    final GridLayout layout = new GridLayout(2, 4, 1, 1);

    /**
         * @uml.property  name="slider"
         * @uml.associationEnd
         */
    private JSlider slider;

    /**
         * @uml.property  name="vcr"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    private IVCR vcr;

    /**
     * Constructor
     * @param vcr The VCR object that the button panel will send commands to.
     */
    public VCRButtonPanel(final IVCR vcr) {
        super();

        try {
            jbInit();
            setVcr(vcr);
        }
        catch (Exception ex) {
            log.error("Failed to initialize", ex);
        }
    }

    /**
         * @return  The slider used fro setting the shuttle speed
         * @uml.property  name="slider"
         */
    public JSlider getSlider() {
        return slider;
    }

    /**
         * @return  The IVCR accosiated with this component.
         * @uml.property  name="vcr"
         */
    public IVCR getVcr() {
        return vcr;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @throws Exception
     */
    void jbInit() throws Exception {
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
         * The shuttle forward and revers butons depend on getting info from the slider. Use this method to set the slider to use for controlling speed.
         * @param slider  The JSlider controlling the shuttle speed
         * @uml.property  name="slider"
         */
    public void setSlider(final JSlider slider) {
        this.slider = slider;
        btnShuttleForward.setSlider(slider);
        btnShuttleReverse.setSlider(slider);
    }

    /**
         * Method used to switch to a different VCR
         * @param newVcr  The new vcr object to send commands to.
         * @uml.property  name="vcr"
         */
    public void setVcr(final IVCR newVcr) {
        if (newVcr != null) {
            vcr = newVcr;

            // Get the manager object
            try {

                // Inital setup. Send a status command to the VCR
                vcr.getVcrState().addObserver(this);
                vcr.requestStatus();

                // Add the vcr to the buttons so they can reflect the VCRs state.
                btnStop.setVcr(vcr);
                btnPlay.setVcr(vcr);
                btnEject.setVcr(vcr);
                btnFastForward.setVcr(vcr);
                btnRewind.setVcr(vcr);
                btnShuttleForward.setVcr(vcr);
                btnShuttleReverse.setVcr(vcr);
                btnGoto.setVcr(vcr);
            }
            catch (NullPointerException e) {

                // The VCR Manager is null
                log.error("Problem setting VCR", e);
            }
        }
    }

    /**
     * to be registered with to a VCRState object
     *
     * @param observed
     * @param stateChange
     */
    public void update(final Object observed, final Object stateChange) {

        // Do nothing by default
    }
}
