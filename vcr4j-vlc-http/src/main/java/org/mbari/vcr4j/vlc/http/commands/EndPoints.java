package org.mbari.vcr4j.vlc.http.commands;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Brian Schlining
 * @since 2019-06-14T16:29:00
 */
public class EndPoints {
    final int port;

    public EndPoints(int port) {
        this.port = port;
    }

    // http://127.0.0.1:8080/requests/status.json?command=

    public URL openAndPlay(String mrl) {
        String url = null;
        try {
            url = URLEncoder.encode(mrl, "UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        String s = baseUrl() + "in_play&input=" + url;
        return toUrl(s);
    }

    public URL playById(String id) {
        String s = baseUrl() + "pl_play&id=" + id;
        return toUrl(s);
    }

    public URL togglePause(String id) {
        String s= baseUrl() + "pl_pause=" + id;
        return toUrl(s);
    }

    public URL resumeIfPaused() {
        String s= baseUrl() + "pl_forceresume";
        return toUrl(s);
    }

    public URL pauseIfPlaying() {
        String s= baseUrl() + "pl_forcepause";
        return toUrl(s);
    }

    public URL stop() {
        String s = baseUrl() + "pl_stop";
        return toUrl(s);
    }

    public URL emptyPlaylist() {
        String s= baseUrl() + "pl_empty";
        return toUrl(s);
    }

    /**
     * @param rate Must be > 0
     * @return
     */
    public URL playbackRate(int rate) {
        String s = baseUrl() + "rate&val=" + rate;
        return toUrl(s);
    }

    public URL seekTo(String val) {
        String s = baseUrl() + "seek&val=" + val;
        return toUrl(s);
    }

    public URL playlist() {
        String s =  "http://localhost:" + port + "/requests/playlist.json";
        return toUrl(s);
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/requests/status.json?command=";
    }

    private URL toUrl(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }
}