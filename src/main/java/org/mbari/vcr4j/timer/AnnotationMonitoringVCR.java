/*
 * @(#)AnnotationMonitoringVCR.java   2009.02.24 at 09:44:55 PST
 *
 * Copyright 2007 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vcr4j.timer;

import org.mbari.vcr4j.IVCR;

/**
 * This class is specifically designed for the
 * <a href="http://vars.sourceforge.net">Video Annotation and Reference
 * System</a>. It periodically requests updates to status and timecode. When the
 * VCR is recording it also writes UTC time to the userbits on the tape.
 *
 * @author brian
 */
public class AnnotationMonitoringVCR extends MonitoringVCR {

    /**
     * Creates a new instance of StateMonitoringVCR
     */
    public AnnotationMonitoringVCR() {
        this(DUMMY_VCR);
    }

    /**
     * Constructs ...
     *
     * @param vcr
     */
    public AnnotationMonitoringVCR(IVCR vcr) {
        super();
        addMonitor(new StatusMonitor());
        addMonitor(new TimecodeMonitor());
        addMonitor(new UserbitsMonitor());
        //addMonitor(new WriteTimeMonitor());
        setVcr(vcr);
        requestStatus();
    }
}
