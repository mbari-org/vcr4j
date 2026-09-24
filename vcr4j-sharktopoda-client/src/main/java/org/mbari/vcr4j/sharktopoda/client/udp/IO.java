/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
