/*
 * @(#)VCRShuttleSpeedPanel.java   2009.02.24 at 09:44:53 PST
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
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mbari.swing.JFancyButton;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRState;

/**
 * <p>
 * UI widget consisteing of a slider and a plus and minus button for adjusting
 * the VCRs shuttling speed.
 * </p>
 *
 * @author : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class VCRShuttleSpeedPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -6893667319542142785L;

    /**
         * @uml.property  name="sliderLabels"
         */
    private Hashtable sliderLabels = new Hashtable();

    /**
         * @uml.property  name="slider"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    JSlider slider = new JSlider();

    /**
         * @uml.property  name="label"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    JLabel label = new JLabel();

    /**
         * @uml.property  name="currentLabelKey"
         * @uml.associationEnd  qualifier="this:org.mbari.vcr4j.ui.VCRShuttleSpeedPanel javax.swing.JLabel"
         */
    private Integer currentLabelKey = Integer.valueOf(100);

    /**
         * @uml.property  name="currentLabel"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    private JLabel currentLabel = new JLabel();

    /**
         * @uml.property  name="buttonSize"
         */
    private Dimension buttonSize = new Dimension(20, 20);

    /**
         * @uml.property  name="btnIncrease"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    JButton btnIncrease = new JFancyButton();

    /**
         * @uml.property  name="btnDecrease"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    JButton btnDecrease = new JFancyButton();

    /**
         * @uml.property  name="vcr"
         * @uml.associationEnd
         */
    private IVCR vcr;

    /**
     * Constructs ...
     *
     */
    public VCRShuttleSpeedPanel() {
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param e
     */
    void btnDecreaseActionPerformed(ActionEvent e) {
        int i = slider.getValue();

        slider.setValue(i - 1);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param e
     */
    void btnIncreaseActionPerformed(ActionEvent e) {
        int i = slider.getValue();

        slider.setValue(i + 1);
    }

    /**
         * @return  The JSlider used to set the shuttle speed. This may need to be  passed to other UI components such as <i>VCRShuttleButton</i>.
         * @uml.property  name="slider"
         */
    public JSlider getSlider() {
        return slider;
    }

    /**
         * @return  The IVCR object that this class is registered to
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

        // Add a label to the slider
        label.setToolTipText("");
        label.setText("Speed: 000");

        // create the decrease button
        btnDecrease.setPreferredSize(buttonSize);
        btnDecrease.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnDecreaseActionPerformed(e);
            }
        });
        btnDecrease.setIcon(new ImageIcon(getClass().getResource("/images/vcr/minus.png")));
        btnDecrease.setPressedIcon(new ImageIcon(getClass().getResource("/images/vcr/minus_r.png")));

        // Create an increase button
        btnIncrease.setPreferredSize(buttonSize);
        btnIncrease.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnIncreaseActionPerformed(e);
            }
        });
        btnIncrease.setIcon(new ImageIcon(getClass().getResource("/images/vcr/plus.png")));
        btnIncrease.setPressedIcon(new ImageIcon(getClass().getResource("/images/vcr/plus_r.png")));

        // Create a slider to adjust the shuttle speed
        slider.setMinorTickSpacing(5);
        slider.setMajorTickSpacing(25);
        slider.setValue(100);
        slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                sliderPropertyChange(e);
            }
        });
        slider.setPaintTicks(true);
        slider.setMaximum(255);
        slider.setDoubleBuffered(true);
        slider.setPreferredSize(new Dimension(140, 40));
        slider.setToolTipText("");
        slider.setLabelTable(sliderLabels);
        setSliderLabel(slider.getValue());
        slider.setPaintLabels(true);

        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);

        this.setLayout(layout);
        layout.setHgap(0);
        this.add(btnDecrease, null);
        this.add(slider, null);
        this.add(btnIncrease, null);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param value
     */
    private void setSliderLabel(final int value) {

        // Remove any existing labels
        if (sliderLabels.size() > 0) {
            sliderLabels.remove(currentLabelKey);
        }

        // Add the new label
        currentLabelKey = Integer.valueOf(value);
        currentLabel.setText(currentLabelKey + "");
        sliderLabels.put(currentLabelKey, currentLabel);
    }

    /**
         * Sets the IVCR object to register to and pass commands to.
         * @param vcr  The VCR boject to associate to.
         * @uml.property  name="vcr"
         */
    public void setVcr(final IVCR vcr) {
        this.vcr = vcr;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param e
     */
    void sliderPropertyChange(final ChangeEvent e) {
        label.setText(slider.getValue() + "");
        setSliderLabel(slider.getValue());
        slider.repaint();

        if (vcr != null && !slider.getValueIsAdjusting()) {
            final IVCRState m = vcr.getVcrState();

            if (m != null) {
                if (m.isShuttling()) {
                    if (m.isReverseDirection()) {
                        vcr.shuttleReverse(slider.getValue());
                    }
                    else {
                        vcr.shuttleForward(slider.getValue());
                    }
                }
            }
        }
    }
}
