/*
 * @(#)IVCRError.java   2009.02.24 at 09:44:52 PST
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


import mbarix4j.util.IObservable;

/**
 *
 * @author brian
 */
public interface IVCRError extends IObservable {

    /** Everything is OK */
    int OK = 0x00;

    /**
     * Although this is a potentially useful method. Most programs will be getter
     * served by the various 'is' methods.
     *
     * @return the integer error code
     */
    int getError();

    boolean isChecksumError();

    boolean isFramingError();

    boolean isOK();

    boolean isOverrunError();

    boolean isParityError();

    boolean isTimeout();

    boolean isUndefinedCommand();
}
