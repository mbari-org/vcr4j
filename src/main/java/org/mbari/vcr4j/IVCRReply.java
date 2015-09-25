/*
 * @(#)IVCRReply.java   2009.02.24 at 09:44:52 PST
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

import org.mbari.util.IObservable;

/**
 * @author  brian
 */
public interface IVCRReply extends IObservable {

    /**
     * @return  The error object associated with this reply
     * @see  org.mbari.vcr4j.IVCRError
     */
    IVCRError getVcrError();

    /**
     * @return  The state object associated with this reply
     * @see  org.mbari.vcr4j.IVCRState
     */
    IVCRState getVcrState();

    /**
     * @return  The timecode object associated with this reply
     * @see  org.mbari.vcr4j.IVCRTimecode
     */
    IVCRTimecode getVcrTimecode();

    /**
     * @return
     */
    IVCRUserbits getVcrUserbits();

    boolean isAck();

    boolean isNack();

    /**
     * Checks to see if the reply is a reply to a Status sense (also called GET_STATUS
     *
     * @return True if he reply contains a status update
     */
    boolean isStatusReply();

    /**
     * Checks to see if he reply is a response to a timecode command
     *
     * @return True if the reply is for a timecode
     */
    boolean isTimecodeReply();

    /**
     * @return
     */
    boolean isUserbitsReply();
}
