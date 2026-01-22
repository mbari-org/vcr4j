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
/**
 * @author Brian Schlining
 * @since 2020-03-12T11:05:00
 */
module vcr4j.core {
    exports org.mbari.vcr4j;
    exports org.mbari.vcr4j.commands;
    exports org.mbari.vcr4j.decorators;
    exports org.mbari.vcr4j.time;
    exports org.mbari.vcr4j.util;
    requires io.reactivex.rxjava3;
    requires org.reactivestreams;
}
