/*
 * @(#)VCRConsoleFrame.java   2011.09.15 at 04:27:55 PDT
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr4j.ui.swing;


import gnu.io.CommPortIdentifier;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.decorators.Decorator;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.rs422.VCRVideoIO;
import org.mbari.vcr4j.rs422.commands.PresetUserbitsCmd;
import org.mbari.vcr4j.rs422.commands.RS422VideoCommands;
import org.mbari.vcr4j.rs422.decorators.RS422LoggingDecorator;
import org.mbari.vcr4j.rs422.decorators.RS422StatusDecorator;
import org.mbari.vcr4j.rxtx.RXTXUtilities;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * Frame component used for testing and developing of VCR control. Called by
 * VCRConsole
 * </p>
 *
 * @author : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class VCRConsoleFrame extends JFrame {

    VCRPanel vcrPanel = new VCRPanel();
    JMenuItem menuHelpAbout = new JMenuItem();
    JMenu menuHelp = new JMenu();
    JMenuItem menuFileExit = new JMenuItem();
    JMenu menuFile = new JMenu();
    JMenuItem menuConnect = new JMenuItem();
    JMenuBar menuBar = new JMenuBar();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel contentPane;
    UserbitPanel statusBar;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /** Construct the frame */
    public VCRConsoleFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        try {
            initialize();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected UserbitPanel getStatusBar() {
        if (statusBar == null) {
            statusBar = new UserbitPanel();
        }

        return statusBar;
    }

    /**
     * Component initialization
     *
     * @throws Exception when the bad gets real
     */
    private void initialize() throws Exception {

        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(350, 165));
        this.setTitle("VCR Console");
        menuFile.setText("File");
        menuFileExit.setText("Exit");
        menuFileExit.addActionListener(this::menuFileExitActionPerformed);
        menuHelp.setText("Help");
        menuHelpAbout.setText("About");
        menuHelpAbout.addActionListener(this::menuHelpAboutActionPerformed);
        menuConnect.setText("Connect to VCR");
        menuConnect.addActionListener(e -> {
            Set<CommPortIdentifier> ports = RXTXUtilities.getAvailableSerialPorts();
            String[] portNames = ports.stream()
                    .map(CommPortIdentifier::getName)
                    .sorted()
                    .toArray(String[]::new);



            if (portNames.length == 0) {
                JOptionPane.showMessageDialog(VCRConsoleFrame.this,
                        "No serial ports were found. Unable to connect to a VCR.", "Error",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            String s = (String) JOptionPane.showInputDialog(VCRConsoleFrame.this, "Select a serial port",
                "VCR Serial Port", JOptionPane.PLAIN_MESSAGE, null, portNames, portNames[0]);

            if ((s != null) && (s.length() > 0)) {
                try {
                    log.debug("Opening " + s);
                    RXTXVideoIO io = RXTXVideoIO.open(s);
                    Decorator syncDecorator = new VCRSyncDecorator<>(io);
                    Decorator statusDecorator = new RS422StatusDecorator(io);
                    Decorator loggingDecorator = new RS422LoggingDecorator(io);
                    vcrPanel.setVideoController(new VideoController<>(io));
                }
                catch (Exception e1) {

                    e1.printStackTrace();
                }
            }
        });
        menuFile.add(menuConnect);
        menuFile.add(menuFileExit);
        menuHelp.add(menuHelpAbout);
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        this.setJMenuBar(menuBar);
        contentPane.add(getStatusBar(), BorderLayout.SOUTH);
        contentPane.add(vcrPanel, BorderLayout.CENTER);
    }

    /**
     * File | Exit action performed
     *
     * @param e The ActionEvent
     */
    public void menuFileExitActionPerformed(ActionEvent e) {
        vcrPanel.disconnect();
        System.exit(0);
    }

    /**
     * Help | About action performed
     *
     * @param e The ActionEvent
     */
    public void menuHelpAboutActionPerformed(ActionEvent e) {
        VCRConsoleFrame_AboutBox dlg = new VCRConsoleFrame_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();

        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.setVisible(true);
    }

    /**
     * Overridden so we can exit when window is closed
     *
     * @param e The WindowEvent
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            menuFileExitActionPerformed(null);
        }
    }

    /**
     *     @author  brian
     */
    class UserbitLabel extends JLabel {


        public UserbitLabel() {
            vcrPanel.videoControllerProperty().addListener((obs, oldVal, newVal) -> {
                setText("");

                if (newVal != null && newVal instanceof VCRVideoIO) {
                    VCRVideoIO io = (VCRVideoIO) newVal;
                    io.getUserbitsObservable().subscribe(ub -> {
                        byte[] b = ub.getUserbits();
                        BigInteger bi = new BigInteger(b);
                        String bs = bi.toString(16);
                        if (bs.length() % 2 != 0) {
                            // Pad with a zero
                            bs = "0" + bs;
                        }

                        setText(bs);
                    });

                }
            });
        }

    }


    /**
     *     @author  brian
     */
    class UserbitPanel extends JPanel {

        private JButton button;
        private UserbitLabel label;
        private JButton setButton;

        /**
         * Constructs ...
         *
         */
        UserbitPanel() {
            initialize();
        }


        JButton getButton() {
            if (button == null) {
                button = new JButton();
                button.setText("Get User-bits");
                button.addActionListener(e ->
                        Optional.ofNullable(vcrPanel.videoControllerProperty().get())
                                .ifPresent(vc -> vc.send(RS422VideoCommands.REQUEST_USERBITS)));
            }

            return button;
        }


        UserbitLabel getLabel() {
            if (label == null) {
                label = new UserbitLabel();
                label.setText(" ");
            }

            return label;
        }


        JButton getSetButton() {
            if (setButton == null) {
                setButton = new JButton();
                setButton.setText("Set User-bits");
                setButton.addActionListener(e -> {
                    Optional.ofNullable(vcrPanel.videoControllerProperty().get())
                            .ifPresent(vc -> vc.send(new PresetUserbitsCmd(new byte[] { 1, 2, 3, 4 })));
                });
            }

            return setButton;
        }


        private void initialize() {
            setLayout(new BorderLayout());
            add(getButton(), BorderLayout.WEST);
            add(getLabel(), BorderLayout.CENTER);
            add(getSetButton(), BorderLayout.EAST);
        }

    }
}
