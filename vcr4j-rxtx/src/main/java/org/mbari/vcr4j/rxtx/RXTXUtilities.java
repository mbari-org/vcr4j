package org.mbari.vcr4j.rxtx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import org.mbari.vcr4j.commands.VideoCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2016-01-29T10:22:00
 */
public class RXTXUtilities {

    private static final Logger log = LoggerFactory.getLogger(RXTXUtilities.class);

    /**
     * No instantiation allowed
     */
    private RXTXUtilities() {
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
                        CommPort thePort = com.open(RXTXUtilities.class.getSimpleName(), 50);

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
    public static HashSet<CommPortIdentifier> getParallelPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<>();
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

    /**
     *  Method used to iterate through the communication ports looking for the serial port with the VCR attached to it.
     * @return The port with the Sony VCR hooked up to it. If no VCR is found <b>null<b/> is returned
     */
    public static Set<CommPortIdentifier> getVcrPort() {

        // Get a set of ports that are not in use
        final HashSet serialPorts = getAvailableSerialPorts();
        final Iterator i = serialPorts.iterator();
        String portName = "";


        final Map<CommPortIdentifier, Boolean> vcrPorts = new HashMap<>();

        while (i.hasNext()) {
            try {
                CommPortIdentifier port = (CommPortIdentifier) i.next();
                portName = port.getName();

                final RXTXVideoIO io = new RXTXVideoIO(port.getName());
                io.send(VideoCommands.REQUEST_STATUS);
                io.getStateObservable()
                        .subscribe(state -> vcrPorts.put(port, state.isConnected()), e -> {}, io::close);


            }
            catch (NoSuchElementException e) {
                log.warn("No response was returned by the VCR");
            }
            catch (Exception e) {
                log.warn("Problem with accessing serial port: " + portName, e);
            }
        }

        return vcrPorts.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}

