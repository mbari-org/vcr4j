/*
 * @(#)VCRSelectionDialog.java   2009.02.24 at 09:44:53 PST
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
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import javafx.beans.property.ObjectProperty;
import org.mbari.util.Dispatcher;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selects the connection for a VCR. When OK is clicked it puts the IVCR returned
 * in a Dispatcher. You can retrieve it as follows:
 *
 * <pre>
 * IVCR vcr = (IVCR) Dispatcher.getDispatcher(IVCR.class).getValueObject();
 * </pre>
 * @author brian
 *
 */
public class VCRSelectionDialog extends JDialog {

    private static final Logger log = LoggerFactory.getLogger(VCRSelectionDialog.class);
    private JPanel buttonPanel = null;
    private JButton cancelButton = null;
    private JPanel jContentPane = null;
    private Action okAction = null;
    private JButton okButton = null;
    private VCRSelectionPanel vcrSelectionPanel = null;
    private final ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController;



    public VCRSelectionDialog(Frame owner, ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController) throws HeadlessException {
        super(owner, true);
        this.videoController = videoController;
        initialize();
        this.setLocationRelativeTo(owner);
    }


    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getCancelButton(), null);
            buttonPanel.add(getOkButton(), null);
        }

        return buttonPanel;
    }


    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(e ->
                    VCRSelectionDialog.this.setVisible(false));
        }

        return cancelButton;
    }


    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getVcrSelectionPanel(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        }

        return jContentPane;
    }

    private Action getOkAction() {
        if (okAction == null) {
            okAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    log.debug("Opening a dialog to select the VCR");
                    VCRSelectionDialog.this.setVisible(false);
                    getVcrSelectionPanel().connect();
                    //Dispatcher.getDispatcher(IVCR.class).setValueObject(getVcrSelectionPanel().getVcr());
                    log.debug("Connection to VCR is now established");
                }
            };
            okAction.putValue(Action.NAME, "OK");
            okAction.putValue(Action.ACTION_COMMAND_KEY, "OK");
            okAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        }

        return okAction;
    }

    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setAction(getOkAction());

            /*
             * Pressing enter anywhere on the dialog is like pressing the OK
             * button.
             */
            Action a = getOkAction();

            okButton.getActionMap().put(a.getValue(Action.ACTION_COMMAND_KEY), a);
            okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY),
                                 a.getValue(Action.ACTION_COMMAND_KEY));
        }

        return okButton;
    }

    private VCRSelectionPanel getVcrSelectionPanel() {
        if (vcrSelectionPanel == null) {
            vcrSelectionPanel = new VCRSelectionPanel(videoController);
        }

        return vcrSelectionPanel;
    }


    private void initialize() {
        this.setSize(new java.awt.Dimension(301, 205));
        this.setTitle("VARS - Connect to VCR");
        this.setContentPane(getJContentPane());
        this.pack();

    }


    public void setVisible(boolean b) {
//        if (b) {
//            IVCR vcr = (IVCR) Dispatcher.getDispatcher(IVCR.class).getValueObject();
//
//            if (vcr == null) {
//                vcr = new VCRAdapter();
//            }
//
//            getVcrSelectionPanel().setVcr(vcr);
//        }

        super.setVisible(b);
    }

}

