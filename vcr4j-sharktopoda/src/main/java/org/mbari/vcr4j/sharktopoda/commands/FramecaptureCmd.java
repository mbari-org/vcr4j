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
package org.mbari.vcr4j.sharktopoda.commands;

import org.mbari.vcr4j.commands.SimpleVideoCommand;

import java.io.File;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2016-08-26T14:55:00
 */
public class FramecaptureCmd extends SimpleVideoCommand<FramecaptureCmd.Params> {


    public FramecaptureCmd(Params value) {
        super("framecapture", value);
    }

    public FramecaptureCmd(UUID imageReferenceUuid, File imageLocation) {
        this(new FramecaptureCmd.Params(imageReferenceUuid, imageLocation.getAbsolutePath()));
    }

    public static class Params {
        private final String imageLocation;
        private final UUID imageReferenceUuid;

        public Params(UUID imageReferenceUuid, String imageLocation) {
            this.imageLocation = imageLocation;
            this.imageReferenceUuid = imageReferenceUuid;
        }

        public String getImageLocation() {
            return imageLocation;
        }

        public UUID getImageReferenceUuid() {
            return imageReferenceUuid;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "imageLocation='" + imageLocation + '\'' +
                    ", imageReferenceUuid=" + imageReferenceUuid +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FramecaptureCmd{" + getValue() + "}";
    }
}
