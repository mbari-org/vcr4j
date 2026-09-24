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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.mbari.vcr4j.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SelectionController {
    private final LocalizationController controller;

    private final ObservableList<Localization> selectedLocalizations =
            FXCollections.observableArrayList();

    public SelectionController(LocalizationController controller) {
        this.controller = controller;
        controller.getIncoming()
                .ofType(Message.class)
                .filter(msg -> Message.ACTION_SELECT.equalsIgnoreCase(msg.getAction()))
                .subscribe(msg -> select(msg.getLocalizations(), false));

        controller.getIncoming()
                .ofType(Message.class)
                .filter(msg -> Message.ACTION_DESELECT.equalsIgnoreCase(msg.getAction()))
                .subscribe(msg -> deselect(msg.getLocalizations(), false));

    //    controller.getLocalizations()
    //            .addListener((ListChangeListener<Localization>) c -> {
    //                while (c.next()) {
    //                    if (c.wasRemoved()) {
    //                        List<Localization> removed = (List<Localization>) c.getRemoved();
    //                        deselect(removed, true);
    //                    }
    //                }
    //            });

    }

    public void select(Collection<Localization> localizations, boolean sendNotify) {
       Collection<Localization> intersection =
               CollectionUtils.intersection(localizations, controller.getLocalizations());

       selectedLocalizations.clear();
       selectedLocalizations.addAll(intersection);

       if (sendNotify) {
           controller.getOutgoing()
                   .onNext(new Message(Message.ACTION_SELECT, new ArrayList<>(intersection)));
       }

    }

    public void deselect(Collection<Localization> localizations, boolean sendNotify) {
        Collection<Localization> intersection =
                CollectionUtils.intersection(localizations, controller.getLocalizations());

        selectedLocalizations.removeAll(intersection);
        if (sendNotify && !intersection.isEmpty()) {
            controller.getOutgoing()
                    .onNext(new Message(Message.ACTION_DESELECT, new ArrayList<>(intersection)));
        }

    }

    public void clearSelections() {
        select(Collections.emptyList(), true);
    }

    public LocalizationController getController() {
        return controller;
    }

    public ObservableList<Localization> getSelectedLocalizations() {
        return selectedLocalizations;
    }


}
