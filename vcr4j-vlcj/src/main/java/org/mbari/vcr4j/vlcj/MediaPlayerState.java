package org.mbari.vcr4j.vlcj;

import org.mbari.vcr4j.VideoState;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * @author Brian Schlining
 * @since 2016-04-18T15:44:00
 */
public class MediaPlayerState implements VideoState {

    private final boolean connected;
    private final boolean fastForwarding;
    private final boolean playing;
    private final boolean reverseDirection;
    private final boolean rewinding;
    private final boolean shuttling;
    private final boolean stopped;

    public  MediaPlayerState(MediaPlayer mediaPlayer) {
        connected = mediaPlayer.isMediaParsed();
        float rate = mediaPlayer.getRate();
        fastForwarding = rate > 1;
        reverseDirection = rate < 0;
        rewinding = rate < 1;

        float shuttleRate = Math.abs(rate);
        shuttling = shuttleRate > 0 && shuttleRate < 1;

        playing = mediaPlayer.isPlaying();
        stopped = !playing;
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
