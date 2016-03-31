/*
 * @(#)VCRSelectionPanel.java   2009.02.24 at 09:44:53 PST
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

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.timer.MonitoringVCR;

/**
 * Class description
 *
 *
 * @version    $Id: $
 * @author     Brian Schlining
 */
public class VCRSelectionPanel extends JPanel {

    private VCRConnectionPanel currentConnectionPanel = null;
    private JRadioButton rs422RB = null;
    private ActionListener rs422RBListener = null;
    private JRadioButton udp01RB = null;
    private ActionListener udp01RBListener = null;
    private JRadioButton udp02RB = null;
    private ActionListener udp02RBListener = null;
    private VCRConnectionPanel vcrConnectionPanelRs422 = null;    //  @jve:decl-index=0:visual-constraint="520,305"
    private VCRConnectionPanel vcrConnectionPanelUDP01 = null;    //  @jve:decl-index=0:visual-constraint="405,351"
    private VCRConnectionPanel vcrConnectionPanelUDP02 = null;
    private JPanel vcrTypePanel = null;

    /**
     * This is the default constructor
     */
    public VCRSelectionPanel() {
        super();
        initialize();
    }

    private JRadioButton getRs422RB() {
        if (rs422RB == null) {
            rs422RB = new JRadioButton();
            rs422RB.setText(getVcrConnectionPanelRs422().getVcrPanelName());
            rs422RB.addActionListener(getRs422RBListener());
            rs422RB.setSelected(true);

        }

        return rs422RB;
    }


    private ActionListener getRs422RBListener() {
        if (rs422RBListener == null) {
            rs422RBListener = new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (rs422RB.isSelected()) {
                        setCurrentConnectionPanel(getVcrConnectionPanelRs422());
                    }
                }
            };
        }

        return rs422RBListener;
    }

    private ActionListener getUdp01Listener() {
        if (udp01RBListener == null) {
            udp01RBListener = new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (udp01RB.isSelected()) {
                        setCurrentConnectionPanel(getVcrConnectionPanelUDP01());
                    }
                }
            };
        }

        return udp01RBListener;
    }


    private JRadioButton getUdp01RB() {
        if (udp01RB == null) {
            udp01RB = new JRadioButton();
            udp01RB.setText(getVcrConnectionPanelUDP01().getVcrPanelName());
            udp01RB.addActionListener(getUdp01Listener());
        }

        return udp01RB;
    }

    private ActionListener getUdp02Listener() {
        if (udp02RBListener == null) {
            udp02RBListener = new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (udp02RB.isSelected()) {
                        setCurrentConnectionPanel(getVcrConnectionPanelUDP02());
                    }
                }
            };
        }

        return udp02RBListener;
    }


    private JRadioButton getUdp02RB() {
        if (udp02RB == null) {
            udp02RB = new JRadioButton();
            udp02RB.setText(getVcrConnectionPanelUDP02().getVcrPanelName());
            udp02RB.addActionListener(getUdp02Listener());
        }

        return udp02RB;
    }


    public IVCR getVcr() {
        return currentConnectionPanel.getVcr();
    }


    private VCRConnectionPanel getVcrConnectionPanelRs422() {
        if (vcrConnectionPanelRs422 == null) {
            vcrConnectionPanelRs422 = new org.mbari.vcr4j.rs422.VCRConnectionPanel();
        }

        return vcrConnectionPanelRs422;
    }


    private VCRConnectionPanel getVcrConnectionPanelUDP01() {
        if (vcrConnectionPanelUDP01 == null) {
            vcrConnectionPanelUDP01 = new org.mbari.vcr4j.udp01.VCRConnectionPanel();
        }

        return vcrConnectionPanelUDP01;
    }

    private VCRConnectionPanel getVcrConnectionPanelUDP02() {
        if (vcrConnectionPanelUDP02 == null) {
            vcrConnectionPanelUDP02 = new org.mbari.vcr4j.udp02.VCRConnectionPanel();
        }

        return vcrConnectionPanelUDP02;
    }

    private JPanel getVcrTypePanel() {
        if (vcrTypePanel == null) {
            vcrTypePanel = new JPanel();
            vcrTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Connection Type",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            vcrTypePanel.add(getRs422RB(), null);
            vcrTypePanel.add(getUdp01RB(), null);
            vcrTypePanel.add(getUdp02RB(), null);
        }

        return vcrTypePanel;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getVcrTypePanel(), BorderLayout.NORTH);
        this.add(getVcrConnectionPanelRs422(), BorderLayout.CENTER);
        setCurrentConnectionPanel(getVcrConnectionPanelRs422());

        ButtonGroup bg = new ButtonGroup();

        bg.add(getRs422RB());
        bg.add(getUdp01RB());
        bg.add(getUdp02RB());
    }

    private void setCurrentConnectionPanel(VCRConnectionPanel vcrConnectionPanel) {
        if (currentConnectionPanel != null) {
            remove(currentConnectionPanel);
        }

        currentConnectionPanel = vcrConnectionPanel;
        add(currentConnectionPanel, BorderLayout.CENTER);
        VCRSelectionPanel.this.revalidate();
        VCRSelectionPanel.this.repaint();
    }

    /**
     * Method description
     *
     *
     * @param vcr
     */
    public void setVcr(IVCR vcr) {
        if (vcr instanceof MonitoringVCR) {
            vcr = ((MonitoringVCR) vcr).getVcr();
        }

        if (vcr instanceof org.mbari.vcr4j.rs422.VCR) {
            getRs422RB().setSelected(true);
            getRs422RBListener().actionPerformed(null);
        }
        else if (vcr instanceof org.mbari.vcr4j.udp01.VCR) {
            org.mbari.vcr4j.udp01.VCR udpVcr = (org.mbari.vcr4j.udp01.VCR) vcr;

            getUdp01RB().setSelected(true);

            org.mbari.vcr4j.udp01.VCRConnectionPanel panel = (org.mbari.vcr4j.udp01
                .VCRConnectionPanel) getVcrConnectionPanelUDP01();

            panel.getPortField().setText(udpVcr.getPort() + "");
            panel.getHostField().setText(udpVcr.getInetAddress().getHostName());
            getUdp01Listener().actionPerformed(null);
        }
        else if (vcr instanceof org.mbari.vcr4j.udp02.VCR) {
            org.mbari.vcr4j.udp02.VCR udpVcr = (org.mbari.vcr4j.udp02.VCR) vcr;

            getUdp02RB().setSelected(true);

            org.mbari.vcr4j.udp02.VCRConnectionPanel panel = (org.mbari.vcr4j.udp02
                .VCRConnectionPanel) getVcrConnectionPanelUDP02();

            panel.getPortField().setText(udpVcr.getPort() + "");
            panel.getHostField().setText(udpVcr.getInetAddress().getHostName());
            getUdp02Listener().actionPerformed(null);
        }
    }
}
