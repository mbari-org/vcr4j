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



package org.mbari.vcr4j.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mbari.util.IObserver;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.VCRAdapter;
import org.mbari.vcr4j.time.Timecode;
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
    private static final long serialVersionUID = -3169420522894558598L;

    // listens to timecode updates


    private VCRShuttleSpeedPanel sliderPanel;

    // Contains shuttle speed slider


    private JTextField timeCodeField;

    private JPanel timeCodePanel;    // Panel to hold the time code

    private IVCR vcr;    // Place holder VCR

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
        vcr.disconnect();
        getTimeCodeField().setText("NO VCR");
    }

    /**
     *
     */
    public void finalize() {
        try {
            super.finalize();
            getVcr().disconnect();
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
            timeCodeField = new TimeCodeField();
        }

        return timeCodeField;
    }

    /**
     *     @return  the timeCodePanel
     *     @uml.property  name="timeCodePanel"
     */
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
    public Timecode getTimecode() {
        Timecode timecode = null;

        if (vcr != null) {
            timecode = vcr.getVcrTimecode().getTimecode();
        }

        return timecode;
    }

    /**
     *     Allow classes to programtically access the VCR
     *     @return
     *     @uml.property  name="vcr"
     */
    public IVCR getVcr() {
        return vcr;
    }

    /**
     *     @return  the vcrButtonPanel
     *     @uml.property  name="vcrButtonPanel"
     */
    VCRButtonPanel getVcrButtonPanel() {
        if (vcrButtonPanel == null) {
            vcrButtonPanel = new VCRButtonPanel(getVcr());

            /*
             * Don't forget to associate the speed slider with the button panel.
             * Otherwise the button panels shuttle funcions don't work.
             */
            vcrButtonPanel.setSlider(getSliderPanel().getSlider());
        }

        return vcrButtonPanel;
    }

    // Component initialization

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @throws Exception
     */
    private void initialize() throws Exception {
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);

        layout.setHgap(10);
        setLayout(layout);
        add(getTimeCodePanel());
        add(getVcrButtonPanel());
        setVcr(new VCRAdapter());
    }

    /**
     *     Change the VCR used by the control panel
     *     @param  newVcr
     *     @uml.property  name="vcr"
     */
    public void setVcr(IVCR newVcr) {
        getTimeCodeField().setText("NO VCR");

        if (vcr != null) {
            vcr.getVcrTimecode().removeObserver((IObserver) getTimeCodeField());
        }

        if (newVcr != null) {
            vcr = newVcr;
            vcr.getVcrTimecode().addObserver((IObserver) getTimeCodeField());

            try {
                getTimeCodeField().setText(vcr.getVcrTimecode().toString());
            }
            catch (Exception e) {
                getTimeCodeField().setText("NO VCR");

                if (log.isInfoEnabled()) {
                    log.info("Unable to set the timecode value in the display.", e);
                }
            }

            getVcrButtonPanel().setVcr(vcr);
            getSliderPanel().setVcr(vcr);
            vcr.requestStatus();
        }
        else {
            getTimeCodeField().setText("NO VCR");
        }
    }
}
