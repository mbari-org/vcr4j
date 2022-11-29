/*
 * @(#)VCRGotoButton.java   2009.02.24 at 09:44:53 PST
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



/* Generated by Together */
package org.mbari.vcr4j.ui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javafx.beans.property.ObjectProperty;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;

import javax.swing.*;

/**
 * <p>Goto button used for the VCR UI</p>
 *
 * @author Brian Schlining
 */
public class VCRGotoButton extends VCRButton {


    final VCRGotoFrame f = new VCRGotoFrame();

    private boolean firstShowing = true;



    public VCRGotoButton(ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController) {
        super(videoController);
        setOnIcon("/images/vcr/goto_r.png");
        setOffIcon("/images/vcr/goto.png");
        setToolTipText("Go to a timecode");
        f.setResizable(false);
        f.pack();
    }

    @Override
    void onNext(VideoState videoState) {
        Icon icon = videoState.isCueingUp() ? onIcon : offIcon;
        setIcon(icon);
    }


    /**
     * <p><!-- Method description --></p>
     *
     */
    void doAction() {
        if (videoControllerProperty().get() != null) {

            // Set the frame on screen if it hasn't been shown before.
            // Subsequent showings should be where ever the user placed it.
            if (firstShowing) {

                // Make sure the goto panel is on screen
                int thisY = getY();                 // This buttons y height
                Dimension fD = f.getSize();         // The VCRGotoFrames size
                int fY = fD.height;                 // The VCRGotoFrames height
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int sY = screenSize.height;         // The height of the screen
                Point p = getLocationOnScreen();    // The location of the Top-left corner fo the bottum

                if (thisY + fY + p.getY() > sY) {
                    int newY = ((int) p.getY()) - fY;
                    int newX = (int) p.getX();

                    f.setLocation(newX, newY);
                }
                else {
                    f.setLocation(getLocationOnScreen());
                }

                firstShowing = false;
            }

            f.setVisible(true);
        }
    }
}