/*
 * @(#)TimeCodeField.java   2009.02.24 at 09:44:54 PST
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.mbari.util.IObserver;
import org.mbari.vcr.IVCRTimecode;

/**
 * <p>A JTextField object that is prettied up for use in a VCR GUI and can be
 * registered to a VCRTimecode object. Once this object is registered, by using
 * IVCR.getVCRManager().addTimecodeObserver(thisTimeCodeField), then it will
 * automatically update and dsiplay the timecode whenever a call to
 * IVCR.getTimecode() occurs.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public class TimeCodeField extends JTextField implements IObserver {

    /**
     *
     */
    private static final long serialVersionUID = -2796428940479908239L;

    /**
         * @uml.property  name="size"
         */
    private Dimension size = new Dimension(180, 40);

    /** No argument constructor */
    public TimeCodeField() {
        super();
        initComponent();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initComponent() {

        // time code display:
        setBackground(Color.black);
        setFont(new java.awt.Font("SansSerif", Font.BOLD, 26));
        setForeground(Color.red);
        setBorder(BorderFactory.createEtchedBorder());

        // setMaximumSize(new Dimension(150, 40));
        // setMinimumSize(new Dimension(150, 40));
        setPreferredSize(size);
        setRequestFocusEnabled(false);
        setCaretColor(Color.black);
        setDisabledTextColor(Color.black);
        setEditable(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setSelectedTextColor(Color.red);
        setSelectionColor(Color.black);
        setColumns(9);
        setAlignmentX(CENTER_ALIGNMENT);
    }

    /**
     * Implementation of IObserver. In this case the IObserver should be a VCRTimeCode object.
     * This object should be registered as VCRTimeCode.addObserver(this).
     * Usually done through the VCRManger.addTimeCodeObserver();
     *
     * @param observed
     * @param stateChange
     */
    public void update(Object observed, Object stateChange) {
        if (observed instanceof IVCRTimecode) {
            this.setText(observed.toString());
        }
    }
}
