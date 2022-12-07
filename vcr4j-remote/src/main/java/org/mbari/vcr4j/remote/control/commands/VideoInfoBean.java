package org.mbari.vcr4j.remote.control.commands;

import java.net.URL;
import java.util.UUID;

public class VideoInfoBean implements VideoInfo {

    private UUID uuid;
    private URL url;
    private Long durationMillis;
    private Double frameRate;
    private Boolean isKey;

    public VideoInfoBean(UUID uuid, URL url, Long durationMillis, Double frameRate, Boolean isKey) {
        this.uuid = uuid;
        this.url = url;
        this.durationMillis = durationMillis;
        this.frameRate = frameRate;
        this.isKey = isKey;
    }

    public VideoInfoBean() {
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Long getDurationMillis() {
        return durationMillis;
    }

    @Override
    public Double getFrameRate() {
        return frameRate;
    }

    @Override
    public Boolean isKey() {
        return isKey;
    }

    public static VideoInfoBean from(VideoInfo that) {
        if (that instanceof VideoInfoBean v) {
            return v;
        }
        else {
            return new VideoInfoBean(that.getUuid(), that.getUrl(), that.getDurationMillis(), that.getFrameRate(), that.isKey());
        }
    }
}
