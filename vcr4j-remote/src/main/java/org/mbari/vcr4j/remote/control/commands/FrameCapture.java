package org.mbari.vcr4j.remote.control.commands;

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

import java.util.UUID;

/**
 * Defines teh parameters that should be returned when a framecapture has been completed.
 */
public interface FrameCapture {

    /**
     *
     * @return The uuid of the video
     */
    UUID getUuid();

    /**
     *
     * @return Where the image was saved.
     */
    String getImageLocation();

    /**
     *
     * @return The uuid of the image
     */
    UUID getImageReferenceUuid();

    /**
     *
     * @return The elapsed time (as millis) into the video when the frame capture was taken
     */
    Long getElapsedTimeMillis();

}
