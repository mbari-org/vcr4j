package org.mbari.vcr4j.sharktopoda.client.model;

import com.google.gson.annotations.SerializedName;

import java.net.InetAddress;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2017-12-05T13:14:00
 */
public class GenericCommand {
    private String command;
    private Integer port;
    private String host;
    private URL url;
    private UUID uuid;
    private Double rate;
    @SerializedName("elapsed_time_millis") private Duration elapsedTime;
    private String imageLocation;
    private UUID imageReferenceUuid;
    private transient InetAddress packetAddress;
    private transient int packetPort;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Double getRate() {
        return rate;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public UUID getImageReferenceUuid() {
        return imageReferenceUuid;
    }

    public InetAddress getPacketAddress() {
        return packetAddress;
    }

    public void setPacketAddress(InetAddress packetAddress) {
        this.packetAddress = packetAddress;
    }

    public int getPacketPort() {
        return packetPort;
    }

    public void setPacketPort(int packetPort) {
        this.packetPort = packetPort;
    }
}
