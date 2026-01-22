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

public class VideoInfoBean implements VideoInfo {

    private UUID uuid;
    private URL url;
    private Long durationMillis;
    private Double frameRate;
    private Boolean isKey;

    public VideoInfoBean(UUID uuid, URL url, Long durationMillis, Double frameRate, Boolean isKey) {
        this.uuid = uuid;
        this.url = url;
        this.durationMillis = durationMillis;
        this.frameRate = frameRate;
        this.isKey = isKey;
    }

    public VideoInfoBean() {
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Long getDurationMillis() {
        return durationMillis;
    }

    @Override
    public Double getFrameRate() {
        return frameRate;
    }

    @Override
    public Boolean isKey() {
        return isKey;
    }

    public static VideoInfoBean from(VideoInfo that) {
        if (that instanceof VideoInfoBean v) {
            return v;
        }
        else {
            return new VideoInfoBean(that.getUuid(), that.getUrl(), that.getDurationMillis(), that.getFrameRate(), that.isKey());
        }
    }
}
