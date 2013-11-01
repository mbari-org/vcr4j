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
package org.mbari.vcr.ui;

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

    /**
         * @uml.property  name="layout"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    private VerticalFlowLayout layout = new VerticalFlowLayout();

    /**
         * @uml.property  name="btnPanel"
         * @uml.associationEnd
         */
    private JPanel btnPanel;

    /**
         * @uml.property  name="cancelButton"
         * @uml.associationEnd
         */
    private JButton cancelButton;

    /**
         * @uml.property  name="okActionListener"
         */
    protected ActionListener okActionListener;

    /**
         * @uml.property  name="okButton"
         * @uml.associationEnd
         */
    private JButton okButton;

    /**
         * @uml.property  name="timePanel"
         * @uml.associationEnd
         */
    protected TimeSelectPanel timePanel;

    /**
     * Constructs ...
     *
     */
    public TimeCodeSelectionFrame() {
        initialize();
    }

    /**
         * @return  Returns the btnPanel.
         * @uml.property  name="btnPanel"
         */
    public JPanel getBtnPanel() {
        if (btnPanel == null) {
            btnPanel = new JPanel();
            btnPanel.add(getOkButton(), null);
            btnPanel.add(getCancelButton(), null);
        }

        return btnPanel;
    }

    /**
         * @return  Returns the cancelButton.
         * @uml.property  name="cancelButton"
         */
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
         * @uml.property  name="okActionListener"
         */
    public abstract ActionListener getOkActionListener();

    /**
         * @return  Returns the okButton.
         * @uml.property  name="okButton"
         */
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

    /**
         * @return  Returns the timePanel.
         * @uml.property  name="timePanel"
         */
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

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {
        this.getContentPane().setLayout(layout);
        this.getContentPane().add(getTimePanel(), null);
        this.getContentPane().add(getBtnPanel(), null);
    }
}
