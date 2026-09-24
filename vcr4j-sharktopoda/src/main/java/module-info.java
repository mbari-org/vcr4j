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
/**
 * @author Brian Schlining
 * @since 2020-03-12T11:22:00
 */
module vcr4j.sharktopoda {

    requires com.google.gson;
    requires io.reactivex.rxjava3;
    requires vcr4j.core;
    
    opens org.mbari.vcr4j.sharktopoda.commands to com.google.gson;
    opens org.mbari.vcr4j.sharktopoda.model to com.google.gson;
    opens org.mbari.vcr4j.sharktopoda.model.request to com.google.gson;
    opens org.mbari.vcr4j.sharktopoda.model.response to com.google.gson;

    exports org.mbari.vcr4j.sharktopoda.commands;
    exports org.mbari.vcr4j.sharktopoda.decorators;
    exports org.mbari.vcr4j.sharktopoda.model.request;
    exports org.mbari.vcr4j.sharktopoda.model.response;
    exports org.mbari.vcr4j.sharktopoda.model;
    exports org.mbari.vcr4j.sharktopoda;
    
}