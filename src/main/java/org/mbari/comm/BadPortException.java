/*
 * @(#)BadPortException.java   2009.02.24 at 09:45:33 PST
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
 * The Monterey Bay Aquarium Research Institute (MBARI) provides this
 * documentation and code 'as is', with no warranty, express or
 * implied, of its quality or consistency. It is provided without support and
 * without obligation on the part of MBARI to assist in its use, correction,
 * modification, or enhancement. This information should not be published or
 * distributed to third parties without specific written permission from MBARI
 */
package org.mbari.comm;

/**
 * <p>Generic Exception thrown when there is a problem with communications
 * through a serial port</p>
 *
 *@author     <a href="http://www.mbari.org">MBARI</a>
 *@created    October 3, 2004
 *@version    $Id: BadPortException.java 3 2005-10-27 16:20:12Z hohonuuli $
 */
public class BadPortException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -7104019503101960740L;

    /**
     * Constructor for the BadPortException object
     */
    public BadPortException() {
        super();
    }

    /**
     * @param  s  Message to apply to this Exception
     */
    public BadPortException(String s) {
        super(s);
    }

    /**
     * Constructs ...
     *
     * @param s
     * @param t
     */
    public BadPortException(String s, Throwable t) {
        super(s, t);
    }
}
