/*
 * @(#)MessageLabel.java   2009.02.24 at 09:44:54 PST
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

import javax.swing.JLabel;
import org.mbari.util.IObserver;

/**
 * <p>This is a JLabel class that implements the IObserver interface. It updates the
 * label using the toString() method of the IObservable class that its registered to.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version : $Revision: 140 $
 */
public class MessageLabel extends JLabel implements IObserver {

    /**
     *
     */
    private static final long serialVersionUID = 3704113878198101835L;

    /**
     * This method is called when an object that this is registered to changes
     * its state. This update method sets the text based on the toString() method of the observed object.
     * @param observed The object observed
     * @param stateChange A change of state message from the observed object
     */
    public void update(Object observed, Object stateChange) {
        setText(observed.toString());
    }
}
