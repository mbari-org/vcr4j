package org.mbari.vcr4j.ui.swing;

import javafx.beans.property.ObjectProperty;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * @author Brian Schlining
 * @since 2016-04-04T16:05:00
 */
public class UDPConnectionPanel extends VCRConnectionPanel {

    private JTextField hostTextField;
    private JLabel lblHost;
    private JLabel lblPort;
    private JTextField portTextField;

    public UDPConnectionPanel(ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController) {
        super(videoController);
        initialize();
    }

    /**
     * @return array o {host: String, port: Integer, framerate: Double}
     */
    public Object[] getConnectionParameters() {
        return new Object[] { getHostTextField().getText(), Integer.valueOf(getPortTextField().getText()),
                Double.valueOf(29.97) };
    }


    public JTextField getHostTextField() {
        if (hostTextField == null) {
            hostTextField = new JTextField();
            hostTextField.setColumns(10);
        }

        return hostTextField;
    }

    private JLabel getLblHost() {
        if (lblHost == null) {
            lblHost = new JLabel("Host: ");
        }

        return lblHost;
    }

    private JLabel getLblPort() {
        if (lblPort == null) {
            lblPort = new JLabel("Port: ");
        }

        return lblPort;
    }


    public JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.setColumns(10);
        }

        return portTextField;
    }

    private void initialize() {
        setBorder(new TitledBorder(null, "UDP", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(getLblHost())
                                        .addComponent(getLblPort()))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(getPortTextField(), GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                                        .addComponent(getHostTextField(), GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getLblHost())
                                        .addComponent(getHostTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getLblPort())
                                        .addComponent(getPortTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(248, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
    }

    /*
     * Sets the connection parameters in the text fields
     */

    /**
     *
     * @param hostname HostName
     * @param portnumber port to connect to host
     */
    public void setConnectionParameters(String hostname, String portnumber) {
        getHostTextField().setText(hostname);
        getPortTextField().setText(portnumber);
    }

    /**
     *
     * @param enabled true enables editable fields
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getHostTextField().setEnabled(enabled);
        getPortTextField().setEnabled(enabled);
    }

    @Override
    public void connect() {

    }

    @Override
    public String getVcrPanelName() {
        return null;
    }
}
