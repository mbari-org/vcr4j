/*
 * @(#)VCRTimecodeAdapter.java   2009.02.24 at 09:44:50 PST
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



/*
VCRTimecodeAdapter.java
 *
Created on January 6, 2006, 9:28 AM
 *
To change this template, choose Tools | Template Manager
and open the template in the editor.
 */

package org.mbari.vcr;

import java.text.DecimalFormat;
import org.mbari.movie.Timecode;
import org.mbari.util.IObserver;
import org.mbari.util.ObservableSupport;

/**
 *
 * @author brian
 */
public class VCRTimecodeAdapter implements IVCRTimecode {

    private static final DecimalFormat f = new DecimalFormat();

    static {
        f.setMaximumFractionDigits(0);
        f.setMinimumIntegerDigits(2);
        f.setMaximumIntegerDigits(2);
    }

    /**
     *     @uml.property  name="os"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final ObservableSupport os = new ObservableSupport();

    /**
     *     @uml.property  name="timecode"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    protected final Timecode timecode = new Timecode();

    /** Creates a new instance of VCRTimecodeAdapter */
    public VCRTimecodeAdapter() {
        timecode.addObserver(new Observer());
    }

    /**
     * Method description
     *
     *
     * @param observer
     */
    public void addObserver(IObserver observer) {
        os.add(observer);
    }

    /** @return The frame number of the current timecode */
    public int getFrame() {
        return timecode.getFrame();
    }

    /** @return The hour value of the current timecode */
    public int getHour() {
        return timecode.getHour();
    }

    /** @return The minute value of the current timecode */
    public int getMinute() {
        return timecode.getMinute();
    }

    /** @return The second value of the current timecode */
    public int getSecond() {
        return timecode.getSecond();
    }

    /**
     *     @return  the timecode as provided by the VCR. The format of each byte is the  upper 4-bits represent the 10's value while the lower 4-bits represent the  1's value. The units of each byte are as follows: <br> byte[0] = frame [4-bit decimal][4-bit ones]<br>  byte[1] = seconds<br> byte[2] = minutes<br> byte[3] = hours<br>
     *     @uml.property  name="timecode"
     */
    public Timecode getTimecode() {
        return timecode;
    }

    /** Notifies registered observers when the this objects state has changed. */
    protected void notifyObservers() {
        os.notify(this, null);
    }

    /**
     * Method description
     *
     */
    public void removeAllObservers() {
        os.clear();
    }

    /**
     * Method description
     *
     *
     * @param observer
     */
    public void removeObserver(IObserver observer) {
        os.remove(observer);
    }

    /** @return Formatted timecode in HH:MM:SS:FF (i.e. hours:minutes:seconds:frame) */
    public String toString() {
        return timecode.toString();
    }

    /**
     * Class that relays notification to registered observers when the
     * Timecode object is updated.
     */
    private class Observer implements IObserver {

        /**
         * Method description
         *
         *
         * @param obj
         * @param changeCode
         */
        public void update(Object obj, Object changeCode) {
            notifyObservers();
        }
    }
}
