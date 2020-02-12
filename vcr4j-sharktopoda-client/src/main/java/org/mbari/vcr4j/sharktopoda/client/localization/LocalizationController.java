package org.mbari.vcr4j.sharktopoda.client.localization;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.mbari.vcr4j.sharktopoda.client.IOBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2020-02-10T13:44:00
 */
public class LocalizationController extends IOBus {


    private final ObservableList<Localization> localizations =
            new SortedList<>(FXCollections.observableArrayList(),
                    Comparator.comparing(Localization::getElapsedTime));
    private final ObservableList<Localization> readonlyLocalizations =
            FXCollections.unmodifiableObservableList(localizations);
    private final Logger log = LoggerFactory.getLogger(getClass());


    public LocalizationController() {

        incoming.ofType(Localization.class)
                .subscribe(this::addOrReplaceLocalization,
                        e -> log.warn("An error occurred on the incoming localization bus", e));

        incoming.ofType(RemoveLocalizationMsg.class)
                .filter(msg -> msg.getLocalizationUuid() != null)
                .subscribe(msg -> removeLocalization(msg.getLocalizationUuid()),
                        e -> log.warn("An error occurred on the incoming RemoveLocalizationMsg bus", e));

    }

    /**
     * DO NOT CALL DIRECTLY. Updates the localizations collection when a new
     * localization is received on the incoming bus.
     * @param a
     */
    private void addOrReplaceLocalization(Localization a) {
        boolean exists = false;
        for (int i = 0; i< localizations.size(); i++) {
            Localization b = localizations.get(i);
            if (b.getLocalizationUuid().equals(a.getLocalizationUuid())) {
                localizations.set(i, a);
                exists = true;
                break;
            }
        }
        if (!exists) {
            localizations.add(a);
        }
    }


    public ObservableList<Localization> getLocalizations() {
        return readonlyLocalizations;
    }


    /**
     * Any new or modified localizations should be passed to this method. They
     * will be propagated as needed.
     * @param localization
     */
    public void publishLocalization(Localization localization) {
        Preconditions.require(localization.getLocalizationUuid() != null,
                "Localization requires a localizationUuid. Null was found.");
        Preconditions.require(localization.getConcept() != null,
                "A Localization requires a concept. Null was found");
        Preconditions.require(localization.getElapsedTime() != null,
                "A localization requires an elapsedtime. Null was found");
        Preconditions.require(localization.getX() != null,
                "A localization requires an x value. Null was found");
        Preconditions.require(localization.getX() > 0,
                "A localization can not have a negative x coordinate. " +
                localization.getX() + " + was found.");
        Preconditions.require(localization.getY() != null,
                "A localization requires an y value. Null was found");
        Preconditions.require(localization.getY() > 0,
                "A localization can not have a negative y coordinate. " +
                        localization.getX() + " + was found.");
        Preconditions.require(localization.getWidth() != null,
                "A localization requires a width value. Null was found");
        Preconditions.require(localization.getWidth() > 1,
                "A localization can not have a width less than 1 pixel." +
                        localization.getWidth() + " + was found.");
        Preconditions.require(localization.getHeight() != null,
                "A localization requires an height value. Null was found");
        Preconditions.require(localization.getHeight() > 0,
                "A localization can not have a height less than 1 pixel. " +
                        localization.getHeight() + " + was found.");

        // Publish to internal buss so it's drawn immediatly
        incoming.onNext(localization);

        // Publish to external bus to be published to remote app. After app does
        // something to it, it will be returned to the incoming bus where it
        // will update the existing one.
        outgoing.onNext(localization);
    }

    public void removeLocalization(UUID localizationUuid) {
        boolean exists = false;
        for (int i = 0; i< localizations.size(); i++) {
            Localization b = localizations.get(i);
            if (b.getLocalizationUuid().equals(localizationUuid)) {
                localizations.remove(i);
                exists = true;
                break;
            }
        }
        if (!exists) {
            log.debug("A localization with UUID of " + localizationUuid + " was not found. ");
        }
    }

    public void clearLocalizations() {
        localizations.clear();
    }


}
