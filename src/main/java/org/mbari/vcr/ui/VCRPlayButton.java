/*
 * @(#)VCRPlayButton.java   2009.02.24 at 09:44:53 PST
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
 * <p>The play button for use with the VCR UI.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 135 $
 */
public class VCRPlayButton extends VCRButton {

    /**
     *
     */
    private static final long serialVersionUID = -3099656705069988837L;

    /** Constructor */
    public VCRPlayButton() {
        super();
        setOnIcon("/images/vcr/play_r.png");
        setOffIcon("/images/vcr/play.png");
        setToolTipText("Play");
    }

    /**
     * This class should be registered to a VCRState object as an observer.
     * @param observed A VCRState Object
     * @param stateChange A message about the change of state.
     */
    public void update(Object observed, Object stateChange) {
        IVCRState s = (IVCRState) observed;

        if (s.isPlaying()) {
            setIcon(onIcon);
        }
        else {
            setIcon(offIcon);
        }

        repaint();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    void vcrAction() {
        if (vcr != null) {
            vcr.play();
        }
    }
}
