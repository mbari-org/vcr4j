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
package org.mbari.vcr4j.sharktopoda.client.model;

import java.net.URL;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2017-12-05T13:18:00
 */
public class Video {
    private UUID uuid;
    private URL url;

    public Video(UUID uuid, URL url) {
        this.uuid = uuid;
        this.url = url;
    }

    public Video() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Video video = (Video) o;

        return uuid.equals(video.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}



