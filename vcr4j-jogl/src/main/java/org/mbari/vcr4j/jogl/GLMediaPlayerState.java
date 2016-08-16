package org.mbari.vcr4j.jogl;

import com.jogamp.opengl.util.av.GLMediaPlayer;
import org.mbari.vcr4j.VideoState;

/**
 * @author Brian Schlining
 * @since 2016-08-15T16:35:00
 */
public class GLMediaPlayerState implements VideoState {

    private final boolean connected;
    private final boolean fastForwarding;
    private final boolean playing;
    private final boolean reverseDirection;
    private final boolean rewinding;
    private final boolean shuttling;
    private final boolean stopped;

    public GLMediaPlayerState(GLMediaPlayer mediaPlayer) {
        connected = mediaPlayer.isGLOriented();
        float rate = mediaPlayer.getPlaySpeed();
        fastForwarding = rate > 1;
        reverseDirection = rate < 0;
        rewinding = rate < -1;

        float shuttleRate = Math.abs(rate);
        shuttling = shuttleRate > 0 && shuttleRate < 1;

        playing = mediaPlayer.getState().equals(GLMediaPlayer.State.Playing);

        stopped = mediaPlayer.getState().equals(GLMediaPlayer.State.Paused);

    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isCueingUp() {
        return false;
    }

    @Override
    public boolean isFastForwarding() {
        return fastForwarding;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public boolean isReverseDirection() {
        return reverseDirection;
    }

    @Override
    public boolean isRewinding() {
        return rewinding;
    }

    @Override
    public boolean isShuttling() {
        return shuttling;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }
}
