package org.mbari.vcr4j;

public interface VideoState {
    boolean isConnected();

    boolean isCueingUp();

    boolean isFastForwarding();

    boolean isPlaying();

    boolean isReverseDirection();

    boolean isRewinding();

    boolean isShuttling();

    boolean isStopped();
}
