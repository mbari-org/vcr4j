/*
 * @(#)VCRFrame.java   2009.02.24 at 09:44:54 PST
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.mbari.util.Dispatcher;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.VCRAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @version    2009.02.24 at 09:44:54 PST
 * @author     Brian Schlining [brian@mbari.org]    
 */
public class VCRFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -5441952610427675789L;
    private static final Logger log = LoggerFactory.getLogger(VCRFrame.class);
    private static final Object DISPATCHER_KEY_VCR = IVCR.class;

    /**
         * @uml.property  name="connectAction"
         * @uml.associationEnd
         */
    private Action connectAction = null;

    /**
         * @uml.property  name="connectMenuItem"
         * @uml.associationEnd
         */
    private JMenuItem connectMenuItem = null;

    /**
         * @uml.property  name="disconnectAction"
         * @uml.associationEnd
         */
    private Action disconnectAction = null;

    /**
         * @uml.property  name="disconnectMenuItem"
         * @uml.associationEnd
         */
    private JMenuItem disconnectMenuItem = null;

    /**
         * @uml.property  name="exitAction"
         * @uml.associationEnd
         */
    private Action exitAction = null;

    /**
         * @uml.property  name="exitMenuItem"
         * @uml.associationEnd
         */
    private JMenuItem exitMenuItem = null;

    /**
         * @uml.property  name="fileMenu"
         * @uml.associationEnd
         */
    private JMenu fileMenu = null;

    /**
         * @uml.property  name="jContentPane"
         * @uml.associationEnd
         */
    private JPanel jContentPane = null;

    /**
         * @uml.property  name="myMenuBar"
         * @uml.associationEnd
         */
    private JMenuBar myMenuBar = null;

    /**
         * @uml.property  name="vcrMenu"
         * @uml.associationEnd
         */
    private JMenu vcrMenu = null;

    /**
         * @uml.property  name="vcrPanel"
         * @uml.associationEnd
         */
    private JPanel vcrPanel = null;

    /**
         * @uml.property  name="connectDialog"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    private JDialog connectDialog = new VCRSelectionDialog(this);

    /**
     * This is the default constructor
     */
    public VCRFrame() {
        super();
        initialize();
    }

    /**
         * @return  the connectAction
         * @uml.property  name="connectAction"
         */
    private Action getConnectAction() {
        if (connectAction == null) {
            connectAction = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = -330526733161975871L;

                public void actionPerformed(ActionEvent arg0) {
                    connectDialog.setVisible(true);
                }
            };
            connectAction.putValue(Action.NAME, "Connect");
            connectAction.putValue(Action.ACTION_COMMAND_KEY, "connect");
            connectAction.putValue(Action.ACCELERATOR_KEY,
                                   KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        }

        return connectAction;
    }

    /**
         * This method initializes connectMenuItem
         * @return  javax.swing.JMenuItem
         * @uml.property  name="connectMenuItem"
         */
    private JMenuItem getConnectMenuItem() {
        if (connectMenuItem == null) {
            connectMenuItem = new JMenuItem();
            connectMenuItem.setAction(getConnectAction());
        }

        return connectMenuItem;
    }

    /**
         * @return  the disconnectAction
         * @uml.property  name="disconnectAction"
         */
    private Action getDisconnectAction() {
        if (disconnectAction == null) {
            disconnectAction = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = -3610374466613863418L;

                public void actionPerformed(ActionEvent arg0) {
                    IVCR vcr = (IVCR) Dispatcher.getDispatcher(DISPATCHER_KEY_VCR).getValueObject();

                    if (vcr != null) {
                        vcr.disconnect();
                    }
                }
            };
            disconnectAction.putValue(Action.NAME, "Disconnect");
            disconnectAction.putValue(Action.ACTION_COMMAND_KEY, "disconnect");
            disconnectAction.putValue(Action.ACCELERATOR_KEY,
                                      KeyStroke.getKeyStroke('D',
                                          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        }

        return disconnectAction;
    }

    /**
         * This method initializes disconnectMenuItem
         * @return  javax.swing.JMenuItem
         * @uml.property  name="disconnectMenuItem"
         */
    private JMenuItem getDisconnectMenuItem() {
        if (disconnectMenuItem == null) {
            disconnectMenuItem = new JMenuItem();
            disconnectMenuItem.setAction(getDisconnectAction());
        }

        return disconnectMenuItem;
    }

    /**
         * @return  the exitAction
         * @uml.property  name="exitAction"
         */
    private Action getExitAction() {
        if (exitAction == null) {
            exitAction = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 3549081954777824320L;

                public void actionPerformed(ActionEvent arg0) {
                    System.exit(0);
                }
            };
            exitAction.putValue(Action.NAME, "Exit");
            exitAction.putValue(Action.ACTION_COMMAND_KEY, "exit");
            exitAction.putValue(Action.ACCELERATOR_KEY,
                                KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        }

        return exitAction;
    }

    /**
         * This method initializes exitMenuItem
         * @return  javax.swing.JMenuItem
         * @uml.property  name="exitMenuItem"
         */
    private JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setAction(getExitAction());
        }

        return exitMenuItem;
    }

    /**
         * This method initializes fileMenu
         * @return  javax.swing.JMenu
         * @uml.property  name="fileMenu"
         */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getExitMenuItem());
        }

        return fileMenu;
    }

    /**
         * This method initializes jContentPane
         * @return  javax.swing.JPanel
         * @uml.property  name="jContentPane"
         */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getVcrPanel(), java.awt.BorderLayout.CENTER);

            Action a = getExitAction();

            jContentPane.getActionMap().put(a.getValue(Action.ACTION_COMMAND_KEY), a);
            jContentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                (KeyStroke) a.getValue(Action.ACCELERATOR_KEY), a.getValue(Action.ACTION_COMMAND_KEY));

        }

        return jContentPane;
    }

    /**
         * This method initializes myMenuBar
         * @return  javax.swing.JMenuBar
         * @uml.property  name="myMenuBar"
         */
    private JMenuBar getMyMenuBar() {
        if (myMenuBar == null) {
            myMenuBar = new JMenuBar();
            myMenuBar.add(getFileMenu());
            myMenuBar.add(getVcrMenu());
        }

        return myMenuBar;
    }

    /**
         * This method initializes vcrMenu
         * @return  javax.swing.JMenu
         * @uml.property  name="vcrMenu"
         */
    private JMenu getVcrMenu() {
        if (vcrMenu == null) {
            vcrMenu = new JMenu();
            vcrMenu.setText("VCR");
            vcrMenu.add(getConnectMenuItem());
            vcrMenu.add(getDisconnectMenuItem());
        }

        return vcrMenu;
    }

    /**
         * This method initializes vcrPanel
         * @return  javax.swing.JPanel
         * @uml.property  name="vcrPanel"
         */
    private JPanel getVcrPanel() {
        if (vcrPanel == null) {
            vcrPanel = new VCRPanel();

            /*
             * Listen for when the VCR is changed. This is done through the VcrDispatcher
             */
            final VCRPanel p = (VCRPanel) vcrPanel;
            PropertyChangeListener listener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    p.setVcr((IVCR) evt.getNewValue());
                }
            };

            Dispatcher.getDispatcher(DISPATCHER_KEY_VCR).addPropertyChangeListener(listener);

        }

        return vcrPanel;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setJMenuBar(getMyMenuBar());
        this.setContentPane(getJContentPane());
        this.setTitle("VCR Console");
        this.pack();

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        this.setVisible(true);
        this.setResizable(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            log.info("Unable to set look and feel", e);
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new VCRFrame();
            }

        });
    }

    public void setVcr(IVCR vcr) {
        Dispatcher dispatcher = Dispatcher.getDispatcher(DISPATCHER_KEY_VCR);

        if (vcr == null) {
            vcr = new VCRAdapter();
        }

        dispatcher.setValueObject(vcr);
    }
}    //  @jve:decl-index=0:visual-constraint="10,24"

