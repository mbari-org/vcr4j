/*
 * @(#)VCRFastForwardButton.java   2009.02.24 at 09:44:54 PST
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

import org.mbari.vcr4j.IVCRState;

/**
 * <p>>Fast forward button used for the VCR UI</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 140 $
 */
public class VCRFastForwardButton extends VCRButton {

    /**
     *
     */
    private static final long serialVersionUID = -5276187037830872237L;

    /** Constructor */
    public VCRFastForwardButton() {
        super();
        setOnIcon("/images/vcr/ffwd_r.png");
        setOffIcon("/images/vcr/ffwd.png");
        setToolTipText("Fast Forward");
    }

    /**
     * This class should be registered to a VCRState object as an observer.
     * @param observed A VCRState Object
     * @param stateChange A message about the change of state.
     */
    public void update(Object observed, Object stateChange) {
        IVCRState s = (IVCRState) observed;

        if (s.isFastForwarding()) {
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

            if (s != null) {
                vcr.fastForward();
            }
        }
    }
}
