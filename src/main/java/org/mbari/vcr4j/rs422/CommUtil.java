/*
 * @(#)CommUtil.java   2009.02.24 at 09:45:33 PST
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
package org.mbari.vcr4j.rs422;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import java.util.Enumeration;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Utilities for use with the javax.comm package.</p>
 *
 *@author     <a href="http://www.mbari.org">MBARI</a>
 *@created    October 3, 2004
 *@version    $Id: CommUtil.java 265 2006-06-20 05:30:09Z hohonuuli $
 */
public class CommUtil {

    private static final Logger log = LoggerFactory.getLogger(CommUtil.class);

    /**
     * No instantiation allowed
     */
    private CommUtil() {
        super();
    }

    /**
     * @return    A HashSet containing the CommPortIdentifier for all serial ports that are not currently being used.
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();

        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();

            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);

                    thePort.close();
                    log.debug("Serial Port, " + com.getName() + ", is available");
                    h.add(com);
                }
                catch (PortInUseException e) {
                    if (log.isInfoEnabled()) {
                        log.debug("Serial port, " + com.getName() + ", is in use.");
                    }
                }
                catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Failed to open serial port " + com.getName(), e);
                    }
                }
            }
        }

        return h;
    }

    /**
     * @return    A HashSet containing the CommPortIdentifier for all serial ports.
     */
    public static HashSet getParallelPorts() {
        HashSet h = new HashSet();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();

        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();

            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_PARALLEL:
                h.add(com);
            }
        }

        return h;
    }

    /**
     * @return    A HashSet containing the CommPortIdentifier for all serial ports.
     */
    public static HashSet<CommPortIdentifier> getSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();

        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();

            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                h.add(com);
            }
        }

        return h;
    }
}
