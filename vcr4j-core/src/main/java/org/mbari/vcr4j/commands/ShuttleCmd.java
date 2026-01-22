package org.mbari.vcr4j.commands;

/*-
 * #%L
 * vcr4j-core
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

import org.mbari.vcr4j.util.Preconditions;

/**
 * A command to tell the video to shuttle forward (+ values) or backwards (- values). This class
 * excepts values between -1 and 1. ! being the maximum rate supported by the video. Your
 * VideoIO implementation should convert that to the appropriate values.
 */
public class ShuttleCmd extends SimpleVideoCommand<Double> {

    /**
     * @param rate -1 &lt;= rate &lt;= 1. 1 represents the maximum shuttle rate for the device. Your
     *             VideoIO implementation should convert this representation to the appropriate
     *             value.
     */
    public ShuttleCmd(double rate) {
        super("shuttle", rate);
        Preconditions.checkArgument(rate <= 1 && rate >= -1,
                "rate must be between -1 and 1 [you provided " + rate + "]");
    }
    
}
