/*
 * @(#)VCRApp.java   2011.09.15 at 04:20:32 PDT
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



package org.mbari.vcr4j.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * <p>Test program used for developing the VCR controls. This is a stand alone
 * application. To use run <i>java org.mbari.vcr4j.ui.VCRConsole</i> at the
 * command line.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class VCRApp {

    private static final Logger log = LoggerFactory.getLogger(VCRApp.class);
    private JFrame frame;

    /** Construct the application */
    public VCRApp() {
        initialize();
    }

    /**
         * @return  the frame
         */
    protected JFrame getFrame() {
        if (frame == null) {
            frame = new VCRConsoleFrame();
        }

        return frame;
    }

    private void initialize() {
        JFrame f = getFrame();

        f.pack();

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = f.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        f.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        f.setVisible(true);
    }

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            log.info("Unable to set look and feel", e);
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new VCRApp();
            }
        });
    }
}
