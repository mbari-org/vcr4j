/*
 * @(#)VCRConsoleFrame_AboutBox.java   2009.02.24 at 09:44:54 PST
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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>Component used for testing VCR controls. Called by VCRConsole.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class VCRConsoleFrame_AboutBox extends JDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = -2463699692322534927L;

    /**
         */
    String comments = "Brian Schlining - MBARI";

    /**
         */
    String copyright = "Copyright (c) 2004";

    /**
         */
    JPanel panel1 = new JPanel();

    /**

         */
    JPanel panel2 = new JPanel();

    /**
         */
    JLabel label4 = new JLabel();

    /**
         */
    JLabel label3 = new JLabel();

    /**
         */
    JLabel label2 = new JLabel();

    /**
         */
    JLabel label1 = new JLabel();

    /**
         */
    JPanel insetsPanel3 = new JPanel();

    /**
         */
    JPanel insetsPanel2 = new JPanel();

    /**
         */
    JPanel insetsPanel1 = new JPanel();

    /**
         */
    JLabel imageLabel = new JLabel();

    /**
         */
    GridLayout gridLayout1 = new GridLayout();

    /**
         */
    FlowLayout flowLayout1 = new FlowLayout();

    /**
         */
    JButton button1 = new JButton();

    /**
         */
    BorderLayout borderLayout2 = new BorderLayout();

    /**
         */
    BorderLayout borderLayout1 = new BorderLayout();

    /**
         */
    String product = "VARS Project (VCR subproject). VCR component testing console.";

    /**
         */
    String version = "1.1";

    /**
     * Constructs ...
     *
     *
     * @param parent The frame that owns this dialog
     */
    public VCRConsoleFrame_AboutBox(Frame parent) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        pack();
    }

    /**
     * Close the dialog on a button event
     *
     * @param e the ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            cancel();
        }
    }

    /** Close the dialog */
    void cancel() {
        dispose();
    }

    /**
     * Component initialization
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        // imageLabel.setIcon(new ImageIcon(VcrConsoleFrame_AboutBox.class.getResource("[Your Image]")));
        this.setTitle("About");
        setResizable(false);
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        insetsPanel1.setLayout(flowLayout1);
        insetsPanel2.setLayout(flowLayout1);
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridLayout1.setRows(4);
        gridLayout1.setColumns(1);
        label1.setText(product);
        label2.setText(version);
        label3.setText(copyright);
        label4.setText(comments);
        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
        button1.setText("Ok");
        button1.addActionListener(this);
        insetsPanel2.add(imageLabel, null);
        panel2.add(insetsPanel2, BorderLayout.WEST);
        this.getContentPane().add(panel1, null);
        insetsPanel3.add(label1, null);
        insetsPanel3.add(label2, null);
        insetsPanel3.add(label3, null);
        insetsPanel3.add(label4, null);
        panel2.add(insetsPanel3, BorderLayout.CENTER);
        insetsPanel1.add(button1, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        panel1.add(panel2, BorderLayout.NORTH);
    }

    /**
     * Overridden so we can exit when window is closed
     *
     * @param e The WindowEvent
     */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }

        super.processWindowEvent(e);
    }
}
