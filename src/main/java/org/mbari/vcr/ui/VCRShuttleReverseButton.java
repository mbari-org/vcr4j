/*
 * @(#)VCRShuttleReverseButton.java   2009.02.24 at 09:44:53 PST
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



package org.mbari.vcr.ui;

import org.mbari.vcr.IVCRState;

/**
 * <p>Button for shuttling forward on the VCR</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: VCRShuttleReverseButton.java 124 2006-01-06 21:05:43Z hohonuuli $
 */
public class VCRShuttleReverseButton extends VCRShuttleButton {

    /**
     *
     */
    private static final long serialVersionUID = -246674259952516414L;

    /** Constructor */
    public VCRShuttleReverseButton() {
        super();
        setOnIcon("/images/vcr/shuttleback_r.png");
        setOffIcon("/images/vcr/shuttleback.png");
        setToolTipText("Shuttle reverse");
    }

    /**
     * This class should be registered to a VCRState object as an observer.
     * @param observed A VCRState Object
     * @param stateChange A message about the change of state.
     */
    public void update(Object observed, Object stateChange) {
        IVCRState s = (IVCRState) observed;

        if (s.isShuttling() & s.isReverseDirection()) {
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
    void vcrAction() {
        if (vcr != null) {
            IVCRState s = vcr.getVcrState();
            int speed = 100;

            if (s != null) {
                speed = slider.getValue();
            }

            vcr.shuttleReverse(speed);
        }
    }
}
