/*
 * Copyright © 2008 MBARI (brian@mbari.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.vcr4j.remote.player;

import org.mbari.vcr4j.remote.control.commands.FrameCapture;
import org.mbari.vcr4j.remote.control.commands.VideoInfo;

import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2022-08-08
 */
public interface VideoController {

    /**
     * Opens a video and focuses its window/stage. If the video with that UUID
     * already exists then just focus its window/stage.
     *
     * <p><b>This method must block until the video is actually open and ready to play</b>
     * (or has failed to open). Its return is what triggers the <code>open done</code> message to
     * the remote app, so returning early, e.g. while the media is still loading, would tell the
     * remote app that the video is ready when it is not. The remote app has already received
     * an <code>ok</code> response to its <code>open</code> request by the time this is called,
     * and this is always called off the UDP receive thread, so it is safe to block here.
     * Do not do the work on the UI thread and return before it finishes; wait for it.
     * (If you must load asynchronously, wait on the load, for example with a
     * {@link CompletableFuture#get(long, java.util.concurrent.TimeUnit)} with a timeout, before returning.)
     *
     * @param videoUuid Key to associate with video
     * @param url The URL (either http or file) of the video to be opened.
     * @return true if the video is open and ready to play, false if unable to open the video
     */
    boolean open(UUID videoUuid, URL url);

    /**
     * Closes a video window if it exists.
     * @param videoUuid
     * @return true if successful, false if it failed or the video does not exist
     */
    boolean close(UUID videoUuid);

    /**
     * Focuses an already open video/window and brings it to the foreground.
     * @param videoUuid
     * @return true if successful, false if it failed or the video does not exist.
     */
    boolean show(UUID videoUuid);

    /**
     * Used to decide whether localization commands for a video can be accepted.
     * Implementations should return true for any video that is open <i>or still
     * loading</i>, since localization commands for a loading video are expected to
     * be queued rather than rejected.
     * @param videoUuid The UUID of the video
     * @return true if the video is known to this player. The default is true, so
     *  existing implementations continue to accept all localization commands.
     */
    default boolean hasVideo(UUID videoUuid) {
        return true;
    }

    /**
     *
     * @return Returns a Video object representing the currently focused video/window.
     *  The optional is empty if no window is currently opened.
     *
     */
    Optional<VideoInfo> requestVideoInfo();

    /**
     *
     * @return A list of all currently open videos
     */
    List<VideoInfo> requestAllVideoInfos();

    /**
     * Sets the playback rate of the current window. 0 is stopped. 1 is normal
     * playback rate. Negative values are reverse shuttling. Refer to your media API to
     * see what the max and min allowed are. Note some codecs and APIs may not
     * support reverse playback.
     * @param videoUuid
     * @param rate
     * @return
     */
    boolean play(UUID videoUuid, double rate);

    /**
     * Stops playback but keeps the window open. Essentially the same as calling
     * `play(uuid, 0)`
     * @param videoUuid
     * @return
     */
    boolean pause(UUID videoUuid);

    /**
     *
     * @param videoUuid
     * @return The rate that the video is playing. This is used to infer status.
     *  0 is stopped. 1 is playing. Other +/- values indicate the shuttle rate
     */
    Optional<Double> requestRate(UUID videoUuid);

    /**
     *
     * @param videoUuid
     * @return The current elapsed time into the video
     */
    Optional<Duration> requestElapsedTime(UUID videoUuid);

    /**
     * Jumps to this point in the video
     * @param videoUuid
     * @param elapsedTime
     * @return
     */
    boolean seekElapsedTime(UUID videoUuid, Duration elapsedTime);

    /**
     * Advance the video a single frame (or some approximating of a very small
     * jump forward)
     * @param videoUuid
     * @return
     */
    boolean frameAdvance(UUID videoUuid);

    /**
     * Grab a frame from the current location of the specified video and write it
     * to disk to __saveLocation__.
     * Important: Be careful with threading when doing a framecapture. As much
     * as possible, IO should be done off of the UI thread.
     * @param videoUuid
     * @param imageReferenceUuid This is a key for a specific image. The value is essentially just
     *                           passed through and returned by the FrameCapture object.
     * @param saveLocation
     * @return A future that completes after the image has been written to disk.
     *  The future should be complete exceptionally if the image can't be captured
     *  or written to disk.
     */
    CompletableFuture<FrameCapture> framecapture(UUID videoUuid,
                                                 UUID imageReferenceUuid,
                                                 Path saveLocation);


    // ---- Result-returning variants -------------------------------------------------
    // The methods below report *why* an operation failed, so the cause can be passed on
    // to the remote app. The defaults delegate to the boolean methods above, so existing
    // implementations keep working. Override these to report precise causes (see the
    // constants in VideoResult).

    private VideoResult resultFor(UUID videoUuid, boolean ok, String genericCause) {
        return VideoResult.of(ok, hasVideo(videoUuid) ? genericCause : VideoResult.NO_VIDEO_FOR_UUID);
    }

    /**
     * Like {@link #open(UUID, URL)}, but reports the cause of a failure. As with
     * {@link #open(UUID, URL)}, <b>this must not return until the video is open and ready to play
     * (or has failed)</b>, because <code>open done</code> is sent to the remote app as soon as
     * it returns.
     */
    default VideoResult openVideo(UUID videoUuid, URL url) {
        return VideoResult.of(open(videoUuid, url), "Unable to open video");
    }

    /**
     * Like {@link #show(UUID)}, but reports the cause of a failure.
     */
    default VideoResult showVideo(UUID videoUuid) {
        return resultFor(videoUuid, show(videoUuid), "Unable to show video");
    }

    /**
     * Like {@link #play(UUID, double)}, but reports the cause of a failure.
     */
    default VideoResult playVideo(UUID videoUuid, double rate) {
        return resultFor(videoUuid, play(videoUuid, rate), "Unable to play video");
    }

    /**
     * Like {@link #pause(UUID)}, but reports the cause of a failure.
     */
    default VideoResult pauseVideo(UUID videoUuid) {
        return resultFor(videoUuid, pause(videoUuid), "Unable to pause video");
    }

    /**
     * Like {@link #seekElapsedTime(UUID, Duration)}, but reports the cause of a failure.
     * Implementations should use {@link VideoResult#SEEK_BEFORE_START} or
     * {@link VideoResult#SEEK_PAST_END} when the time is out of range.
     */
    default VideoResult seekVideo(UUID videoUuid, Duration elapsedTime) {
        return resultFor(videoUuid, seekElapsedTime(videoUuid, elapsedTime), "Unable to seek video");
    }

    /**
     * Advance or regress a single frame.
     * @param videoUuid The video
     * @param forward true to advance, false to go back one frame
     * @return The default implementation delegates to {@link #frameAdvance(UUID)} for
     *  forward and fails with {@link VideoResult#CANNOT_ADVANCE} for reverse. Override to
     *  support stepping backwards.
     */
    default VideoResult advanceFrame(UUID videoUuid, boolean forward) {
        if (!forward) {
            return VideoResult.failed(hasVideo(videoUuid) ? VideoResult.CANNOT_ADVANCE
                    : VideoResult.NO_VIDEO_FOR_UUID);
        }
        return resultFor(videoUuid, frameAdvance(videoUuid), VideoResult.CANNOT_ADVANCE);
    }

}
