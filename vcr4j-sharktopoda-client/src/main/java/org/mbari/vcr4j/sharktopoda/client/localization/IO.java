package org.mbari.vcr4j.sharktopoda.client.localization;

/**
 * @author Brian Schlining
 * @since 2020-02-11T17:00:00
 */
public class IO {

    private final int incomingPort;
    private final int outgoingPort;
    private LocalizationController controller = new LocalizationController();

    public IO(int incomingPort, int outgoingPort) {
        this.incomingPort = incomingPort;
        this.outgoingPort = outgoingPort;
        controller.getOutgoing()
                .ofType(Localization.class)
                .subscribe(this::send);
    }

    public int getIncomingPort() {
        return incomingPort;
    }

    public int getOutgoingPort() {
        return outgoingPort;
    }

    public LocalizationController getController() {
        return controller;
    }

    private void send(Localization localization) {

    }




}
