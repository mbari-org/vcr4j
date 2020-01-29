package org.mbari.vcr4j.sharktopoda.client.udp;

import com.google.gson.Gson;
import io.reactivex.Observable;

import org.mbari.vcr4j.sharktopoda.client.model.GenericCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Brian Schlining
 * @since 2017-12-07T14:20:00
 */
class FramecaptureUdpIO {

    private volatile int remotePort;
    private final Observable<GenericCommand> commandObserver;
    private final CommandService commandService;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Class prefNodeKey = getClass();

    public FramecaptureUdpIO(CommandService commandService, Observable<GenericCommand> commandObserver) {
        this.commandObserver = commandObserver;
        this.commandService = commandService;
        this.commandObserver
                .filter(gc -> gc.getCommand().equals("connect"))
                .subscribe(this::doConnect);
        this.commandObserver
                .filter(gc -> gc.getCommand().equals("framecapture"))
                .subscribe(this::doFramecapture);
        Preferences prefs = Preferences.userNodeForPackage(prefNodeKey);
        remotePort = prefs.getInt("framecapturePort", 5000);
    }

    private void doConnect(GenericCommand cmd) {
        Integer port = cmd.getPort();
        if (port != null) {
            remotePort = port;
            Preferences prefs = Preferences.userNodeForPackage(prefNodeKey);
            prefs.putInt("framecapturePort", port);
            try {
                prefs.flush();
            } catch (BackingStoreException e) {
                log.warn("Failed to save framecapture port to preferences", e);
            }
        }
    }

    private void doFramecapture(GenericCommand cmd) {
        commandService.doFramecapture(cmd)
                .thenAccept(response -> {
                    try {
                        DatagramSocket socket = new DatagramSocket();
                        Gson gson = commandService.getGson();
                        byte[] data = gson.toJson(response).getBytes();
                        log.debug("Sending >>> " + new String(data));
                        DatagramPacket packet = new DatagramPacket(data,
                                data.length,
                                cmd.getPacketAddress(),
                                remotePort);
                        socket.send(packet);
                        socket.close();
                    }
                    catch (Exception e) {
                        log.warn("Failed to respond about framecapture", e);
                    }
                });
    }
}
