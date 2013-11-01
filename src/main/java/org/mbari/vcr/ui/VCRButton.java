/*
 * @(#)VCRButton.java   2009.02.24 at 09:44:54 PST
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

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;

import org.mbari.swing.JFancyButton;
import org.mbari.util.IObserver;
import org.mbari.vcr.IVCR;

/**
 * <p>A generic VCRButton. Clicks on this will be sent to the IVCR object
 * associated with this. This is an abstract class refer to
 * <code>org.mbari.vcr.VCRStopButton</code> for an example implementation.
 * There are two ways to create a VCRButton:</p>
 *
 * <pre>
 * // Method 1
 * IVCR vcr = new IVCR(someCommPort); //either VCRProxy or SonyVCRProxy
 * VCRButton b = new VCRButton(vcr);
 *
 * //Method 2
 * IVCR vcr = new IVCR(someCommPort); //either VCRProxy or SonyVCRProxy
 * VCRButton b = new VCRButton();
 * b.setVcr(vcr);
 * </pre>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 332 $
 */
public abstract class VCRButton extends JFancyButton implements IObserver {


    Icon offIcon;

    Icon onIcon;

    IVCR vcr;

    /** No argument constructor. Use setVcr() to associate this button with a IVCR */
    public VCRButton() {
        super();
        addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                vcrAction();
            }
        });
        final Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.ORANGE,
            Color.GRAY), BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setBorder(border);
    }

    /**
     * Consturctor used to associate this button with an IVCR. The vcrAction
     * implemented by subclasses will send commands to this VCR
     * @param newVcr
     */
    public VCRButton(IVCR newVcr) {
        this();    // Call default constructor
        setVcr(newVcr);
    }

    /**
         * @return  The VCR object associated with this button
         * @uml.property  name="vcr"
         */
    public IVCR getVcr() {
        return vcr;
    }

    /**
     * Sets the icon to be used when a particular state of the VCR is not occuring
     * @param relativePath Path relative to this class of the image file.
     */
    public void setOffIcon(String relativePath) {
        offIcon = new ImageIcon(getClass().getResource(relativePath));
        setIcon(offIcon);
    }

    /**
     * Sets the icon to be used when a particular state fo the VCR occurs.
     * @param relativePath Path relative to this class of the image file.
     */
    public void setOnIcon(String relativePath) {
        onIcon = new ImageIcon(getClass().getResource(relativePath));
        setPressedIcon(onIcon);
    }

    /**
         * Sets the IVCR object to register to and pass commands to.
         * @param newVcr  The VCR to associate this control to.
         * @uml.property  name="vcr"
         */
    public void setVcr(IVCR newVcr) {

        // Unsubscribe from the VCR
        if (vcr != null) {
            vcr.getVcrState().removeObserver(this);
        }

        // Only register if the VCR is not null
        if (newVcr != null) {
            vcr = newVcr;    // use the newVCR
            vcr.getVcrState().addObserver(this);
        }
    }

    /**
     * This class should be registered to a VCRState object as an observer.
     * @param observed A VCRState Object
     * @param stateChange A message about the change of state.
     */
    public abstract void update(Object observed, Object stateChange);

    /** Action to take when this button is pressed */
    abstract void vcrAction();
}
