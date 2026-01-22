/*-
 * #%L
 * vcr4j-jssc
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
 * @since 2020-03-12T11:15:00
 */
module vcr4j.jssc {
    requires io.reactivex.rxjava3;
    requires jssc;
    requires vcr4j.core;
    requires vcr4j.rs422;
    exports org.mbari.vcr4j.jssc;

}
