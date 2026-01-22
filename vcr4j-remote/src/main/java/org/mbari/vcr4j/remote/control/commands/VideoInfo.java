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

import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2022-08-08
 */
public interface VideoInfo {

    // {"durationMillis":899431,"frameRate":29.970027923583984,"isKey":false,"url":"http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T210458.233Z_t4s4_1280_tc03560915_h264.mp4","uuid":"4b78e58a-4c14-4d9b-bdd4-38e287651682"}
    UUID getUuid();
    URL getUrl();
    Long getDurationMillis();
    Double getFrameRate();
    Boolean isKey();

}
