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



package org.mbari.vcr.rs422;

import gnu.io.CommPortIdentifier;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.mbari.comm.CommUtil;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.VCRAdapter;
import org.mbari.vcr.timer.AnnotationMonitoringVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class description
 *
 *
 * @version    $Id: $
 * @author     Brian Schlining
 */
public class VCRConnectionPanel extends org.mbari.vcr.ui.VCRConnectionPanel {

    /**
     * DOCUMENT ME!
     */
    public static final String VCR_PANEL_NAME = "Direct (RS422)";

    /**
     *
     */
    private static final long serialVersionUID = -5003433826793435901L;
    private static final Logger log = LoggerFactory.getLogger(VCRConnectionPanel.class);

    /**
     *     @uml.property  name="lbl1"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel lbl1 = null;

    /**
     *     @uml.property  name="vcrListCB"
     *     @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
     */
    private JComboBox vcrListCB = null;

    /**
     * This method initializes
     *
     */
    public VCRConnectionPanel() {
        super();
        initialize();
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
            final String port = (String) getVcrListCB().getSelectedItem();

            log.debug("Attempting to connect to " + port);
            vcr = new AnnotationMonitoringVCR(new VCR(port));

            // I think doing the following causes the timecode to lag....
            //            vcr = (IVCR) Worker.post(new Task(){
            //
            //                public Object run() {
            //                    IVCR vcr = null;
            //                    try {
            //                        vcr = new StateMonitoringVCR(new VCR(port));
            //                    } catch (Exception ex) {
            //                        throw new RuntimeException("Failed to open VCR connection", ex);
            //                    } 
            //                    return vcr;
            //                }
            //                
            //            });
        }
        catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to connect to VCR on port " + getVcrListCB().getSelectedItem(), e);
            }

            vcr = new VCRAdapter();
        }

        return vcr;
    }

    /**
     *     This method initializes vcrListCB
     *     @return  javax.swing.JComboBox
     */
    private JComboBox getVcrListCB() {
        if (vcrListCB == null) {
            vcrListCB = new JComboBox();

            Set ports = CommUtil.getAvailableSerialPorts();

            vcrListCB.setPreferredSize(new java.awt.Dimension(200, 25));

            for (Iterator i = ports.iterator(); i.hasNext(); ) {
                CommPortIdentifier cpi = (CommPortIdentifier) i.next();

                vcrListCB.addItem(cpi.getName());
            }
        }

        return vcrListCB;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVcrPanelName() {
        return VCR_PANEL_NAME;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new java.awt.Insets(0, 6, 0, 6);
        gridBagConstraints1.gridx = 1;

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new java.awt.Dimension(250, 60));
        lbl1 = new JLabel();
        lbl1.setText("Serial Port:");
        this.add(lbl1, gridBagConstraints);
        this.add(getVcrListCB(), gridBagConstraints1);
    }
}
