package org.mbari.vcr4j.remote.player;

/*-
 * #%L
 * vcr4j-remote
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
     * @param videoUuid Key to associate with video
     * @param url The URL (either http or file) of the video to be opened.
     * @return true if successful, false if unable to open the video
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

}
