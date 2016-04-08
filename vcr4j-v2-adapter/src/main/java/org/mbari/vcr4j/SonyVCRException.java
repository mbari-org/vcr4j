/*
 * @(#)SonyVCRException.java   2009.02.24 at 09:44:51 PST
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



package org.mbari.vcr4j;

/**
 * <p>Exception representing an error with VCR communications</p>
 *
 * @author  Brian Schlining
 */
public class SonyVCRException extends VCRException {

    /**
     *
     */
    private static final long serialVersionUID = -6690660249802162657L;

    /**
     * Constructs ...
     *
     */
    public SonyVCRException() {
        super();
    }

    /**
     * Constructs ...
     *
     *
     * @param s
     */
    public SonyVCRException(String s) {
        super(s);
    }
}
