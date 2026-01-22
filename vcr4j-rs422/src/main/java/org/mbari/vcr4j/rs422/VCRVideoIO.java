package org.mbari.vcr4j.rs422;

/*-
 * #%L
 * vcr4j-rs422
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

import io.reactivex.rxjava3.core.Observable;
import org.mbari.vcr4j.VideoIO;

/**
 *
 * @author Brian Schlining
 * @since 2016-02-03T16:24:00
 */
public interface VCRVideoIO extends VideoIO<RS422State, RS422Error> {

    Observable<RS422Timecode> getTimecodeObservable();

    Observable<RS422Userbits> getUserbitsObservable();
}
