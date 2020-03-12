/*
 * @(#)VCRGotoFrame.java   2009.02.24 at 09:44:53 PST
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.mbari.vcr4j.adapter.IVCR;
import org.mbari.vcr4j.adapter.IVCRTimecode;

/**
 * <p>
 * A frame that allows one to specify a timecode. This frame is popped up by the
 * VCRGotoButton and is used to specify a timecode for the vcr to seek to.
 * </p>
 * @author Brian Schlining
 */
public class VCRGotoFrame extends TimeCodeSelectionFrame {


    private IVCR vcr;

    /**
     * Constructs ...
     *
     */
    public VCRGotoFrame() {
        super();
    }

    /**
     * @return The ActionListener that is called when the OK button of this panel
     *          is clicked.
     */
    public ActionListener getOkActionListener() {
        if (okActionListener == null) {
            okActionListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (vcr != null) {
                        setVisible(false);
                        vcr.seekTimecode(timePanel.getTimecode());
                    }
                }
            };
        }

        return okActionListener;
    }


    public IVCR getVcr() {
        return vcr;
    }


    public void setVcr(IVCR vcr) {
        this.vcr = vcr;
    }


    public void setVisible(boolean b) {
        if (b) {
            final IVCR v = getVcr();
            final TimeSelectPanel tp = getTimePanel();

            if (v != null) {
                final IVCRTimecode tc = v.getVcrTimecode();

                tp.getHourWidget().setTime(tc.getHour());
                tp.getMinuteWidget().setTime(tc.getMinute());
                tp.getSecondWidget().setTime(tc.getSecond());
                tp.getFrameWidget().setTime(tc.getFrame());
            }

            tp.getHourWidget().getTextField().requestFocus();
        }

        super.setVisible(b);
    }
}
