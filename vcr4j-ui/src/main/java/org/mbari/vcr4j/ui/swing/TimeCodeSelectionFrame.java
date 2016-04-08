/*
 * @(#)TimeCodeSelectionFrame.java   2009.02.24 at 09:44:54 PST
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



/*
 * Created on Sep 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mbari.vcr4j.ui.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.Document;
import org.mbari.awt.layout.VerticalFlowLayout;

/**
 * @author brian
 */
public abstract class TimeCodeSelectionFrame extends JFrame {


    private VerticalFlowLayout layout = new VerticalFlowLayout();

    private JPanel btnPanel;

    private JButton cancelButton;

    protected ActionListener okActionListener;

    private JButton okButton;

    protected TimeSelectPanel timePanel;

    public TimeCodeSelectionFrame() {
        initialize();
    }


    public JPanel getBtnPanel() {
        if (btnPanel == null) {
            btnPanel = new JPanel();
            btnPanel.add(getOkButton(), null);
            btnPanel.add(getCancelButton(), null);
        }

        return btnPanel;
    }


    public JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
        }

        return cancelButton;
    }

    /**
     * @return  The ActionListener that is called when the OK button of this panel  is clicked.
     */
    public abstract ActionListener getOkActionListener();


    public JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setPreferredSize(new Dimension(73, 27));
            okButton.setText("OK");
            okButton.addActionListener(getOkActionListener());
            okButton.setRequestFocusEnabled(true);
            okButton.setFocusable(true);
        }

        return okButton;
    }

    public TimeSelectPanel getTimePanel() {
        if (timePanel == null) {
            timePanel = new TimeSelectPanel();

            /*
             * Transfer focus from the frame textfield to the OK button
             * when the frame has 2 characters typed into it.
             */
            final TimeSlider ts = timePanel.getFrameWidget();
            final Document d = ts.getTextField().getDocument();

            d.addDocumentListener(timePanel.makeDocumentListener(getOkButton()));
        }

        return timePanel;
    }

    private void initialize() {
        this.getContentPane().setLayout(layout);
        this.getContentPane().add(getTimePanel(), null);
        this.getContentPane().add(getBtnPanel(), null);
    }
}
