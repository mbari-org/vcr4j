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
    }
}
