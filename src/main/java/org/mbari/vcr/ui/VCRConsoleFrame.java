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



package org.mbari.vcr.ui;

import gnu.io.CommPortIdentifier;
import org.mbari.comm.BadPortException;
import org.mbari.vcr.rs422.CommUtil;
import org.mbari.util.IObserver;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.purejavacomm.VCR;
import org.mbari.vcr.rs422.VCRUserbits;
import org.mbari.vcr.timer.DefaultMonitoringVCR;

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
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
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

    private static final long serialVersionUID = 7687759441685659577L;
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

    /** Construct the frame */
    public VCRConsoleFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        try {
            jbInit();
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
     * @throws Exception
     */
    private void jbInit() throws Exception {

        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(350, 165));
        this.setTitle("VCR Console");
        menuFile.setText("File");
        menuFileExit.setText("Exit");
        menuFileExit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                menuFileExitActionPerformed(e);
            }
        });
        menuHelp.setText("Help");
        menuHelpAbout.setText("About");
        menuHelpAbout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                menuHelpAboutActionPerformed(e);
            }
        });
        menuConnect.setText("Connect to VCR");
        menuConnect.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Set ports = CommUtil.getAvailableSerialPorts();
                String[] portNames = new String[ports.size()];
                int n = 0;

                for (Iterator i = ports.iterator(); i.hasNext(); ) {
                    CommPortIdentifier cpi = (CommPortIdentifier) i.next();

                    portNames[n] = cpi.getName();
                    n++;
                }

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

                        //IVCR vcr = new StateMonitoringVCR(s);
                        IVCR vcr = new VCR(s);

                        vcr = new DefaultMonitoringVCR(vcr);
                        vcrPanel.setVcr(vcr);
                        statusBar.setVcr(vcr);
                    }
                    catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (BadPortException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
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
     * @param e
     */
    public void menuFileExitActionPerformed(ActionEvent e) {
        vcrPanel.getVcr().disconnect();
        System.exit(0);
    }

    /**
     * Help | About action performed
     *
     * @param e
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
     * @param e
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
    class UserbitLabel extends JLabel implements IObserver {

        /**
         *
         */
        private static final long serialVersionUID = -4624074153523052449L;
        private IVCR vcr;

        /**
         *
         */
        public UserbitLabel() {

            // TODO Auto-generated constructor stub
        }

        /**
         *         <p><!-- Method description --></p>
         *         @param  newVcr
         *         @uml.property  name="vcr"
         */
        void setVcr(IVCR newVcr) {
            if (vcr != null) {
                vcr.getVcrUserbits().removeObserver(UserbitLabel.this);
                setText("");
            }

            if (newVcr != null) {
                newVcr.getVcrUserbits().addObserver(UserbitLabel.this);
                vcr = newVcr;
            }
            else {
                vcr = null;
            }
        }

        /**
         *
         *
         * @param observedObj
         * @param changeCode
         */
        public void update(Object observedObj, Object changeCode) {
            VCRUserbits bits = (VCRUserbits) observedObj;
            byte[] b = bits.getUserbits();
            BigInteger bi = new BigInteger(b);
            String bs = bi.toString(16);

            if (bs.length() % 2 != 0) {

                // Pad with a zero
                bs = "0" + bs;
            }

            setText(bs);
        }
    }


    /**
     *     @author  brian
     */
    class UserbitPanel extends JPanel {

        /**
         *
         */
        private static final long serialVersionUID = 1423259866877457922L;
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
                button.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        final IVCR vcr = vcrPanel.getVcr();

                        if (vcr != null) {
                            vcr.requestLUserbits();
                            vcr.requestVUserbits();
                        }
                    }

                });
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
                setButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        final IVCR vcr = vcrPanel.getVcr();

                        if (vcr != null) {
                            vcr.presetUserbits(new byte[] { 1, 2, 3, 4 });
                        }
                    }

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

        void setVcr(IVCR vcr) {
            getLabel().setVcr(vcr);
        }
    }
}
