package org.mbari.vcr4j.kipro;

import org.mbari.vcr4j.VideoState;

/**
 * @author Brian Schlining
 * @since 2016-02-04T11:17:00
 */
public class QuadState implements VideoState {

    private final int connectionID;

    public static final QuadState NOT_CONNECTED = new QuadState(0);

    public QuadState(int connectionID) {
        this.connectionID = connectionID;
    }

    public QuadState() {
        connectionID = 0;
    }

    @Override
    public boolean isConnected() {
        return connectionID != 0;
    }

    @Override
    public boolean isCueingUp() {
        return false;
    }

    @Override
    public boolean isFastForwarding() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isReverseDirection() {
        return false;
    }

    @Override
    public boolean isRewinding() {
        return false;
    }

    @Override
    public boolean isShuttling() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    public int getConnectionID() {
        return connectionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuadState quadState = (QuadState) o;

        return connectionID == quadState.connectionID;

    }

    @Override
    public int hashCode() {
        return connectionID;
    }
}
