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
package org.mbari.vcr4j.sharktopoda.client.localization;

import javafx.collections.ListChangeListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class SelectionControllerTest {

  private Logger log = LoggerFactory.getLogger(getClass());
  private LocalizationController localizationController = new LocalizationController();
  private SelectionController selectionController = new SelectionController(localizationController);

  @Test
  public void testSelection() {
    var lcl = new Localization("Goo", Duration.ofSeconds(2), UUID.randomUUID(),
            UUID.randomUUID(), 10, 10, 40, 40);
    localizationController.getOutgoing()
            .subscribe(msg -> log.info(msg.toString()));
    localizationController.addLocalization(lcl);
    selectionController.select(List.of(lcl), true);
  }
}