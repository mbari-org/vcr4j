/*-
 * #%L
 * vcr4j-remote
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
module vcr4j.remote {
  
  requires com.google.gson;
  requires io.reactivex.rxjava3;
  requires vcr4j.core;
  
  exports org.mbari.vcr4j.remote.control.commands.localization;
  exports org.mbari.vcr4j.remote.control.commands;
  exports org.mbari.vcr4j.remote.control;
  exports org.mbari.vcr4j.remote.player;

  opens org.mbari.vcr4j.remote.player to com.google.gson;
  opens org.mbari.vcr4j.remote.control.commands to com.google.gson;
  opens org.mbari.vcr4j.remote.control.commands.localization to com.google.gson;
}
