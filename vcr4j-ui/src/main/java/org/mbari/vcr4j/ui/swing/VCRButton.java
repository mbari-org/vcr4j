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



package org.mbari.vcr4j.ui.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import javafx.beans.property.ObjectProperty;
import org.mbari.swing.JFancyButton;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;

/**
 * <p>A generic VCRButton. Clicks on this will be sent to the IVCR object
 * associated with this. This is an abstract class refer to
 * <code>org.mbari.vcr4j.vcr.VCRStopButton</code> for an example implementation.
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
 * @author Brian Schlining
 */
public abstract class VCRButton extends JFancyButton {

    private volatile Disposable disposable;

    Icon offIcon;

    Icon onIcon;

    private final ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController;

    /** No argument constructor. Use setVcr() to associate this button with a IVCR */
    public VCRButton(ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController) {
        super();
        this.videoController = videoController;
        addActionListener(e ->  doAction());
        final Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.ORANGE,
            Color.GRAY), BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setBorder(border);

        videoController.addListener((obs, oldVal, newVal ) -> {
            if (oldVal != null && disposable != null) {
                disposable.dispose();
                disposable = null;
            }

            if (newVal != null) {
                newVal.getStateObservable().subscribe(newSubscriber());
            }
        });

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


    /** Action to take when this button is pressed */
    abstract void doAction();

    abstract void onNext(VideoState videoState);


    public ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoControllerProperty() {
        return videoController;
    }

    private Observer<VideoState> newSubscriber() {
        return new Observer<VideoState>() {
            @Override
            public void onComplete() {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onNext(VideoState videoState) {
                VCRButton.this.onNext(videoState);
            }

            @Override
            public void onSubscribe(Disposable disposable) {
                VCRButton.this.disposable = disposable;
            }
        };
    }

}
