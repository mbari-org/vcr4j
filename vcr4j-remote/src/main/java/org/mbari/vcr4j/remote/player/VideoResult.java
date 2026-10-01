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

/**
 * The outcome of a {@link VideoController} operation. When an operation fails, the
 * cause is reported to the remote app in the response's <code>cause</code> field.
 * The standard causes defined by the UDP remote protocol are available as constants.
 *
 * @param ok true if the operation succeeded
 * @param cause Why the operation failed. Null if it succeeded.
 */
public record VideoResult(boolean ok, String cause) {

    public static final String NO_VIDEO_FOR_UUID = "No video for uuid";
    public static final String NO_OPEN_VIDEOS = "No open videos";
    public static final String MALFORMED_URL = "Malformed URL";
    public static final String SEEK_BEFORE_START = "elapsedTimeMillis before start";
    public static final String SEEK_PAST_END = "elapsedTimeMillis past end";
    public static final String CANNOT_ADVANCE = "Cannot advance video in that direction";
    public static final String IMAGE_EXISTS = "Image exists at location";
    public static final String IMAGE_NOT_WRITABLE = "Image location not writable";
    public static final String INVALID_MESSAGE = "Invalid message";

    private static final VideoResult OK = new VideoResult(true, null);

    public static VideoResult success() {
        return OK;
    }

    public static VideoResult failed(String cause) {
        return new VideoResult(false, cause);
    }

    /**
     * @param success The outcome of an operation that reports only success/failure.
     * @param cause The cause to report if success is false
     * @return ok, or failed with the cause
     */
    public static VideoResult of(boolean success, String cause) {
        return success ? OK : failed(cause);
    }
}
