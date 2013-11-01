/*
 * @(#)VCRConnectionPanel.java   2009.02.24 at 09:44:55 PST
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



package org.mbari.vcr.udp02;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.mbari.swing.IntegerTextField;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.VCRAdapter;
import org.mbari.vcr.timer.AnnotationMonitoringVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class VCRConnectionPanel extends org.mbari.vcr.ui.VCRConnectionPanel {

    public static final String VCR_PANEL_NAME = "CSIRO Ship (UDP)";

    /**
     *
     */
    private static final long serialVersionUID = 1915642901854749321L;
    private static final Logger log = LoggerFactory.getLogger(VCRConnectionPanel.class);

    /**
     *     @uml.property  name="hostField"
     *     @uml.associationEnd
     */
    private JTextField hostField = null;

    /**
     *     @uml.property  name="hostLabel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel hostLabel = null;

    /**
     *     @uml.property  name="portField"
     *     @uml.associationEnd
     */
    private JTextField portField = null;

    /**
     *     @uml.property  name="portLabel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel portLabel = null;

    /**
     * This method initializes
     *
     */
    public VCRConnectionPanel() {
        super();
        initialize();
    }

    /**
     *     This method initializes hostField
     *     @return  javax.swing.JTextField
     *     @uml.property  name="hostField"
     */
    public JTextField getHostField() {
        if (hostField == null) {
            hostField = new JTextField();
            hostField.setPreferredSize(new java.awt.Dimension(200, 20));
            hostField.setText("255.255.255.255");
            hostField.setEnabled(false);
        }

        return hostField;
    }

    /**
     *     This method initializes portField
     *     @return  javax.swing.JTextField
     *     @uml.property  name="portField"
     */
    public JTextField getPortField() {
        if (portField == null) {
            portField = new IntegerTextField();
            portField.setText("22228");
        }

        return portField;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @Override
    public IVCR getVcr() {
        IVCR vcr = null;

        try {
            final IVCR vcrUdp = new VCR(Integer.parseInt(getPortField().getText()));

            vcr = new AnnotationMonitoringVCR(vcrUdp);
        }
        catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to connect to VCR on " + getHostField().getText(), e);
            }

            vcr = new VCRAdapter();
        }

        return vcr;
    }

    public String getVcrPanelName() {
        return VCR_PANEL_NAME;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();

        gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints21.gridy = 1;
        gridBagConstraints21.weightx = 1.0;
        gridBagConstraints21.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints21.gridx = 1;

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints1.gridy = 1;
        portLabel = new JLabel();
        portLabel.setText("Port:");

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();

        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints2.gridx = 1;

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.gridy = 0;
        hostLabel = new JLabel();
        hostLabel.setText("Host:");
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new java.awt.Dimension(250, 60));
        this.add(hostLabel, gridBagConstraints);
        this.add(getHostField(), gridBagConstraints2);
        this.add(portLabel, gridBagConstraints1);
        this.add(getPortField(), gridBagConstraints21);

    }
}
