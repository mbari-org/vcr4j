package org.mbari.vcr4j.kipro;

/**
 * @author Brian Schlining
 * @since 2016-02-04T16:54:00
 */
public class QuadConfigEvent {

    private int intValue;
    private int lastConfigUpdate;
    private String paramId;
    private int paramType;
    private String strValue;

    public QuadConfigEvent(int intValue, int lastConfigUpdate, String paramId, int paramType, String strValue) {
        this.intValue = intValue;
        this.lastConfigUpdate = lastConfigUpdate;
        this.paramId = paramId;
        this.paramType = paramType;
        this.strValue = strValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getLastConfigUpdate() {
        return lastConfigUpdate;
    }

    public String getParamId() {
        return paramId;
    }

    public int getParamType() {
        return paramType;
    }

    public String getStrValue() {
        return strValue;
    }
}
