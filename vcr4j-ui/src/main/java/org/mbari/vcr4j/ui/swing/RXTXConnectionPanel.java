package org.mbari.vcr4j.ui.swing;

import gnu.io.CommPortIdentifier;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javafx.beans.property.ObjectProperty;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.rs422.decorators.RS422StatusDecorator;
import org.mbari.vcr4j.rxtx.RXTXUtilities;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Brian Schlining
 * @since 2016-04-04T10:10:00
 */
public class RXTXConnectionPanel extends VCRConnectionPanel {

    public static final String VCR_PANEL_NAME = "Direct (RS422)";

    private static final Logger log = LoggerFactory.getLogger(VCRConnectionPanel.class);


    private JLabel lbl1 = null;

    private JComboBox vcrListCB = null;


    public RXTXConnectionPanel(ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController) {
        super(videoController);
        initialize();
    }


    @Override
    public void connect() {

        try {
            final String port = (String) getVcrListCB().getSelectedItem();

            log.debug("Attempting to connect to " + port);
            RXTXVideoIO io = RXTXVideoIO.open(port);
            Decorator syncDecorator = new VCRSyncDecorator<>(io);
            Decorator statusDecorator = new RS422StatusDecorator(io);
            videoControllerProperty().set(new VideoController<>(io));

        }
        catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to connect to VCR on port " + getVcrListCB().getSelectedItem(), e);
            }

        }

    }


    private JComboBox getVcrListCB() {
        if (vcrListCB == null) {
            vcrListCB = new JComboBox();

            Set ports = RXTXUtilities.getAvailableSerialPorts();

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
