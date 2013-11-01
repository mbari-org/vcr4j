/*
 * @(#)IVCRUserbits.java   2009.02.24 at 09:44:52 PST
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

import org.mbari.util.IObservable;

/**
 *
 * @author brian
 */
public interface IVCRUserbits extends IObservable {

    /**
     *
     *
     * @return The user bits
     */
    byte[] getUserbits();

    void setUserbits(byte[] userbits);
}
