/*
 * @(#)IVCRTimecode.java   2009.02.24 at 09:44:52 PST
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



package org.mbari.vcr;

import org.mbari.movie.Timecode;
import org.mbari.util.IObservable;

/**
 * @author  brian
 */
public interface IVCRTimecode extends IObservable {

    /**
     * @return The frame number of the current timecode
     */
    int getFrame();

    /**
     * @return The hour value of the current timecode
     */
    int getHour();

    /**
     * @return The minute value of the current timecode
     */
    int getMinute();

    /**
     * @return The second value of the current timecode
     */
    int getSecond();

    /**
     * @return  the timecode as provided by the VCR. The format of each byte is the  upper 4-bits represent the 10's value while the lower 4-bits represent the  1's value. The units of each byte are as follows: <br> byte[0] = frame [4-bit decimal][4-bit ones]<br>  byte[1] = seconds<br> byte[2] = minutes<br> byte[3] = hours<br>
     * @uml.property  name="timecode"
     * @uml.associationEnd
     */

    //byte[] getTimecode();

    Timecode getTimecode();
}
