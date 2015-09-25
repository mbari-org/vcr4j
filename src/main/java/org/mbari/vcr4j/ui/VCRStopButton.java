/*
 * @(#)VCRStopButton.java   2009.02.24 at 09:44:53 PST
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

/**
 * <p>The stop button for use with the VCR UI.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 3 $
 */
public class VCRStopButton extends VCRButton {

    /**
     *
     */
    private static final long serialVersionUID = 3667106735545355541L;

    /** Constructor */
    public VCRStopButton() {
        super();
        setOnIcon("/images/vcr/stop_r.png");
        setOffIcon("/images/vcr/stop.png");
        setToolTipText("Stop");
    }

    /**
     * This class should be registered to a VCRState object as an observer.
     * @param observed A VCRState Object
     * @param stateChange A message about the change of state.
     */
    public void update(Object observed, Object stateChange) {
        if (vcr.getVcrState().isStopped()) {
            setIcon(onIcon);
        }
        else {
            setIcon(offIcon);
        }
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    protected void vcrAction() {
        if (vcr != null) {
            vcr.stop();
        }
    }
}
