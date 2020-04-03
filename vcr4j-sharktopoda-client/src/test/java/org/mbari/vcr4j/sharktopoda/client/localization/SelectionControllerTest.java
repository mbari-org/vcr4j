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