/*
 * @(#)VCR.java   2009.02.24 at 09:44:55 PST
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



package org.mbari.vcr.udp01;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mbari.movie.Timecode;
import org.mbari.vcr.IVCRReply;
import org.mbari.vcr.VCRAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This VCR is used to talk to a computer acting as a proxy for the VCR. This
 * version is expressly designed to work with MBARI ships.
 * @author brian
 */
public class VCR extends VCRAdapter {

    public static final byte[] GET_TIMECODE = "ltc.".getBytes();
    private static final Pattern pattern = Pattern.compile("[0-2][0-9]:[0-5][0-9]:[0-5][0-9]:[0-2][0-9]");
    private static final Logger log = LoggerFactory.getLogger(VCR.class);

    /**
         * @uml.property  name="receiveMessage" multiplicity="(0 -1)" dimension="1"
         */
    private byte[] receiveMessage = new byte[1024];

    /**
         * @uml.property  name="vcrReply"
         * @uml.associationEnd  multiplicity="(1 1)"
         */
    private final VCRReply vcrReply = new VCRReply();

    /**
         * @uml.property  name="incomingPacket"
         */
    private final DatagramPacket incomingPacket;

    /**
         * @uml.property  name="inetAddress"
         */
    private final InetAddress inetAddress;

    /**
         * @uml.property  name="port"
         */
    private final int port;

    /**
         * @uml.property  name="requestTimecodePacket"
         */
    private final DatagramPacket requestTimecodePacket;

    /**
         * @uml.property  name="socket"
         */
    private DatagramSocket socket;

    /**
     * Creates a new instance of VCR
     *
     * @param host
     * @param port
     *
     * @throws SocketException
     * @throws UnknownHostException
     */
    public VCR(String host, int port) throws UnknownHostException, SocketException {
        this.port = port;
        inetAddress = InetAddress.getByName(host);
        incomingPacket = new DatagramPacket(receiveMessage, receiveMessage.length);
        requestTimecodePacket = new DatagramPacket(GET_TIMECODE, GET_TIMECODE.length, inetAddress, port);
        requestTimeCode();
    }

    @Override
    public void disconnect() {

        if (!socket.isClosed()) {
            socket.close();
        }

        getVcrTimecode().getTimecode().setTime(0, 0, 0, 0);
        ((VCRState) getVcrState()).setPlaying(false);
        ((VCRState) getVcrState()).setConnected(false);
        super.disconnect();
    }

    @Override
    public void kill() {
        if (!socket.isClosed()) {
            socket.close();
        }
        super.kill();
    }

    @Override
    protected void finalize() throws Throwable {
        if (!socket.isClosed()) {
            socket.close();
        }

        super.finalize();
    }

    /**
     * Returns a name for this connection
     */
    @Override
    public String getConnectionName() {
        DatagramSocket s = null;
        String connectionName = "Not connected";

        try {
            s = getSocket();
            connectionName = s.getInetAddress().getHostName() + ":" + s.getPort();
        }
        catch (SocketException ex) {
            log.error("Failed to open a DatagramSocket", ex);
        }

        return connectionName;
    }

    public InetAddress getInetAddress() {
        return requestTimecodePacket.getAddress();
    }

    public int getPort() {
        return requestTimecodePacket.getPort();
    }

    /**
         * @return  the socket
         * @uml.property  name="socket"
         */
    private DatagramSocket getSocket() throws SocketException {
        if ((socket == null) || socket.isClosed() || !socket.isConnected()) {
            socket = new DatagramSocket(0);
            socket.connect(inetAddress, port);
            socket.setSoTimeout(4000);
        }

        return socket;
    }

    @Override
    public IVCRReply getVcrReply() {
        return vcrReply;
    }

    @Override
    public void requestLTimeCode() {
        if (log.isDebugEnabled()) {
            log.debug("Sending VCR Command -> 'ltc.' to " + getConnectionName());
        }

        sendCommand(requestTimecodePacket);
    }

    @Override
    public void requestStatus() {
        ((VCRState) getVcrState()).notifyObservers();
    }

    @Override
    public void requestTimeCode() {
        if (log.isDebugEnabled()) {
            log.debug("Sending VCR Command -> 'ltc.' to " + getConnectionName());
        }

        sendCommand(requestTimecodePacket);
    }

    @Override
    public void requestVTimeCode() {
        if (log.isDebugEnabled()) {
            log.debug("Sending VCR Command -> 'ltc.' to " + getConnectionName());
        }

        sendCommand(requestTimecodePacket);
    }

    private synchronized void sendCommand(DatagramPacket packet) {
        byte[] response = null;

        try {
            DatagramSocket s = getSocket();

            s.send(packet);
            s.receive(incomingPacket);    // blocks until returned on timedout

            int numBytes = incomingPacket.getLength();

            response = new byte[numBytes];
            System.arraycopy(incomingPacket.getData(), 0, response, 0, numBytes);
        }
        catch (Exception e) {

            // response will be null
            if (log.isErrorEnabled()) {
                log.error("UDP connection failed.", e);
            }
        }

        /*
         * Parse the timecode response.
         */
        String result = null;

        try {
            result = new String(response, "ASCII");
        }
        catch (UnsupportedEncodingException e) {
            try {

                // Try a different encoding
                result = new String(response, "8859_1");
            }
            catch (UnsupportedEncodingException ex) {

                // result = null
                if (log.isErrorEnabled()) {
                    log.error("Timecode over UDP is using an unknown encoding");
                }
            }
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to get a valid response from " + getConnectionName());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Received Response: '" + result + "'");
        }

        /*
         * The format of the response is yes(00:00:00:00) or no(00:00:00:00). In
         * order to be a bit mroe generic, we use a regular expression to search
         * for the first occurence of a substring in a timecode format (i.e.
         * like '01:23:45:12')
         */
        Timecode timecode = getVcrTimecode().getTimecode();

        if (result != null) {
            try {
                Matcher matcher = pattern.matcher(result);

                if (matcher.find()) {
                    timecode.setTimecode(matcher.group(0));
                }
            }
            catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Problem with parsing timecode '" + result + "'", e);
                }
            }
        }
    }
}
