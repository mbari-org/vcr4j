/*
 * @(#)VCRConnectionPanel.java   2009.02.24 at 09:44:54 PST
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

import javax.swing.JPanel;

import javafx.beans.property.ObjectProperty;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.VCRAdapter;
import org.mbari.vcr4j.VideoController;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;

/**
 * 
 *
 * @version    2009.02.24 at 09:44:54 PST
 * @author     Brian Schlining [brian@mbari.org]    
 */
public abstract class VCRConnectionPanel extends JPanel implements IVCRConnectionPanel {

    private final ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController;

    /**
     * This is the default constructor
     */
    public VCRConnectionPanel(ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController) {
        this.videoController = videoController;
        initialize();
    }

    public VideoController<? extends VideoState, ? extends VideoError> getVideoController() {
        return videoController.get();
    }

    public ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoControllerProperty() {
        return videoController;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(300, 200);
    }
}
