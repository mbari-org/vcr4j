package org.mbari.vcr4j.sharktopoda.client.udp;

import org.mbari.vcr4j.sharktopoda.client.ClientController;

/**
 * Manages UDP communications so you don't have to. Usage:
 *
 * <pre>
 *  ClientController controller =  // .. implement this interface
 *  int port = 5005;   // Pick a UDP port for communications
 *  IO io = new IO(controller, port);
 *  // when done
 *  io.close()
 * </pre>
 *
 * @author Brian Schlining
 * @since 2019-12-05T14:18:00
 */
public class IO {

    private final UdpIO io;
    private final CommandService commandService;
    private final ClientController clientController;

    /**
     *
     * @param clientController
     * @param port
     */
    public IO(ClientController clientController, int port) {
        this.clientController = clientController;
        io = new UdpIO(port);
        commandService = new CommandService(clientController, io.getCommandSubject(), io.getResponseSubject());
    }

    public void close() {
        io.close();
    }

    public ClientController getClientController() {
        return clientController;
    }
}
