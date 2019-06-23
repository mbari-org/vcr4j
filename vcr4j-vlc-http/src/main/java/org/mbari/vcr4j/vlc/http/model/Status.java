package org.mbari.vcr4j.vlc.http.model;


public class Status {

    private Boolean fullscreen;
    private String aspectratio;
    private Integer apiversion;
    private Integer currentplid;
    private Integer time;
    private Integer length;
    private Double rate;
    private String state; //playing, paused
    private Double position;

    public Status() {
    }

    public Status(Boolean fullscreen, String aspectratio, Integer apiversion,
                  Integer currentplid, Integer time, Integer length,
                  Double rate, String state, Double position) {
        this.fullscreen = fullscreen;
        this.aspectratio = aspectratio;
        this.apiversion = apiversion;
        this.currentplid = currentplid;
        this.time = time;
        this.length = length;
        this.rate = rate;
        this.state = state;
        this.position = position;
    }

    public Boolean getFullscreen() {
        return fullscreen;
    }

    public String getAspectratio() {
        return aspectratio;
    }

    public Integer getApiversion() {
        return apiversion;
    }

    public Integer getCurrentplid() {
        return currentplid;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getLength() {
        return length;
    }

    public Double getRate() {
        return rate;
    }

    public String getState() {
        return state;
    }

    /*
     * @return between 0 (start) and 1 (end)
     */
    public Double getPosition() {
        return position;
    }
}
