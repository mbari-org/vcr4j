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



package org.mbari.vcr4j.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @version    2009.02.24 at 09:44:54 PST
 * @author     Brian Schlining [brian@mbari.org]    
 */
public class VCRFrame extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(VCRFrame.class);

    private Action connectAction = null;

    private JMenuItem connectMenuItem = null;

    private Action disconnectAction = null;

    private JMenuItem disconnectMenuItem = null;

    private Action exitAction = null;

    private JMenuItem exitMenuItem = null;

    private JMenu fileMenu = null;

    private JPanel jContentPane = null;

    private JMenuBar myMenuBar = null;

    private JMenu vcrMenu = null;
    private VCRPanel vcrPanel = null;

    private JDialog connectDialog;

    /**
     * This is the default constructor
     */
    public VCRFrame() {
        super();
        connectDialog = new VCRSelectionDialog(this, getVcrPanel().videoControllerProperty());
        initialize();
    }

    private Action getConnectAction() {
        if (connectAction == null) {
            connectAction = new AbstractAction() {


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


    private JMenuItem getConnectMenuItem() {
        if (connectMenuItem == null) {
            connectMenuItem = new JMenuItem();
            connectMenuItem.setAction(getConnectAction());
        }

        return connectMenuItem;
    }


    private Action getDisconnectAction() {
        if (disconnectAction == null) {
            disconnectAction = new AbstractAction() {
                public void actionPerformed(ActionEvent arg0) {
                    getVcrPanel().disconnect();
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

    private JMenuItem getDisconnectMenuItem() {
        if (disconnectMenuItem == null) {
            disconnectMenuItem = new JMenuItem();
            disconnectMenuItem.setAction(getDisconnectAction());
        }

        return disconnectMenuItem;
    }

    private Action getExitAction() {
        if (exitAction == null) {
            exitAction = new AbstractAction() {


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

    private JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setAction(getExitAction());
        }

        return exitMenuItem;
    }

    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getExitMenuItem());
        }

        return fileMenu;
    }


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


    private JMenuBar getMyMenuBar() {
        if (myMenuBar == null) {
            myMenuBar = new JMenuBar();
            myMenuBar.add(getFileMenu());
            myMenuBar.add(getVcrMenu());
        }

        return myMenuBar;
    }


    private JMenu getVcrMenu() {
        if (vcrMenu == null) {
            vcrMenu = new JMenu();
            vcrMenu.setText("VCR");
            vcrMenu.add(getConnectMenuItem());
            vcrMenu.add(getDisconnectMenuItem());
        }

        return vcrMenu;
    }


    private VCRPanel getVcrPanel() {
        if (vcrPanel == null) {
            vcrPanel = new VCRPanel();

        }

        return vcrPanel;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

    public void setVideoController(VideoController<? extends VideoState, ? extends VideoError> videoController) {
        getVcrPanel().setVideoController(videoController);
    }


}

