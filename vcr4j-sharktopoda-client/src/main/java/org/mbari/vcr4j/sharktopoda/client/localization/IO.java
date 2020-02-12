package org.mbari.vcr4j.sharktopoda.client.localization;

/**
 * @author Brian Schlining
 * @since 2020-02-11T17:00:00
 */
public class IO {

    private final int port;
    private LocalizationController controller = new LocalizationController();

    public IO(int port) {
        this.port = port;
        controller.getOutgoing()
                .ofType(Localization.class)
                .subscribe(this::send);
        listen();
    }

    public int getPort() {
        return port;
    }

    public LocalizationController getController() {
        return controller;
    }

    private void send(Localization localization) {

    }

    private void listen() {

    }


}
