/**
 * This package provides communications to devices that support Sony's 9-Pin/RS422 protocol.
 *
 * Example Usage:
 * <pre>
 * // Configure rxtx native libraries
 * RXTX.setup();
 *
 * // Replace 'SerialPortName' with yours. e.g COM1 or /dev/ttys.myport
 * VideoIO<RS422State, RS422Error> io = new RXTXVideoIO("SerialPortName")
 * VideoController controller = new VideoController(io);
 *
 * // For UI apps you may want frequent status calls to the VCR
 * RS422StatusDecorator statusDeco = = new RS422StatusDecorator(io.getCommandSubject());
 *
 * controller.stop();
 * controller.play();
 * controller.requestIndex(); // for rs422 this is same as `controller.requestTimecode()`
 * </pre>
 * @author Brian Schlining
 * @since 2016-01-29T14:58:00
 */
package org.mbari.vcr4j.rxtx;