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



package org.mbari.vcr4j;

import javafx.beans.property.ObjectProperty;
import org.mbari.util.IObservable;
import org.mbari.vcr4j.time.Timecode;

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


    Timecode getTimecode();

    ObjectProperty<Timecode> timecodeProperty();
}
