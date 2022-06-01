package org.mbari.vcr4j.sharktopoda.client.localization;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.mbari.vcr4j.sharktopoda.client.IOBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Brian Schlining
 * @since 2020-02-10T13:44:00
 */
public class LocalizationController extends IOBus {

    /**
     * This is the mutable internal collection of localizations
     */
    private final ObservableList<Localization> localizations = FXCollections.observableArrayList();

    private final ObservableList<Localization> readOnlyLocalizations =
            new SortedList<>(FXCollections.unmodifiableObservableList(localizations),
                    Comparator.comparing(Localization::getElapsedTime));


    private final Logger log = LoggerFactory.getLogger(getClass());

    public LocalizationController() {

        var msgObservable = incoming.ofType(Message.class);

        msgObservable
                .filter(msg -> Message.ACTION_ADD.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::addOrReplaceLocalizationsInternal,
                    e -> log.warn("An error occurred on the incoming localization bus", e));

        msgObservable
                .filter(msg -> Message.ACTION_REMOVE.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::removeLocalizationsInternal,
                        e -> log.warn("An error occurred on the incoming localization bus", e));

        msgObservable
                .filter(msg -> Message.ACTION_CLEAR.equalsIgnoreCase(msg.getAction()))
                .forEach(msg -> localizations.clear());

    }

    public void clear() {
        var msg = new Message(Message.ACTION_CLEAR);
        incoming.onNext(msg);
        outgoing.onNext(msg);
    }


    /**
     * This is the internal cache of localizations. You can observe this list for changes, both
     * remote and locally triggered.
     *
     * @return A read-only list of localizations
     */
    public ObservableList<Localization> getLocalizations() {
        return readOnlyLocalizations;
    }


    /**
     * DO NOT CALL DIRECTLY. Updates the localizations collection when a new
     * localization is received on the incoming bus.
     * @param xs
     */
    private void addOrReplaceLocalizationsInternal(Collection<Localization> xs) {
        for (var x : xs) {
            try {
                addOrReplaceLocalizationInternal(x);
            } catch (IllegalArgumentException e) {
                log.warn("Failed to add a localization that was missing required values.", e);
            }
        }
    }

    /**
     * DO NOT CALL DIRECTLY. Updates the localizations collection when a new
     * localization is received on the incoming bus.
     * @param a
     */
    private void addOrReplaceLocalizationInternal(Localization a) {

        boolean exists = false;
        // Slow linear scan :-(
        for (int i = 0; i< localizations.size(); i++) {
            Localization b = localizations.get(i);
            if (b.getLocalizationUuid().equals(a.getLocalizationUuid())) {
                log.debug("Replacing localization (uuid = " + a.getLocalizationUuid() + ")");
                localizations.set(i, a);
                exists = true;
                break;
            }
        }
        if (!exists) {
            log.debug("Adding localization (uuid = " + a.getLocalizationUuid() + ")");
            localizations.add(a);
        }
    }



    /**
     * Any new or modified localizations should be passed to this method. They
     * will be propagated as needed.
     * @param localization
     */
    public void addLocalization(Localization localization) {
        addLocalizations(List.of(localization));
    }

    public void addLocalizations(Collection<Localization> localizations) {
        localizations.forEach(this::validateLocalizationForAdd);
        if (!localizations.isEmpty()) {
            Message msg = new Message(Message.ACTION_ADD, new ArrayList<>(localizations));
            incoming.onNext(msg);
            outgoing.onNext(msg);
        }
    }

    private void validateLocalizationForAdd(Localization localization) {
        Preconditions.require(localization.getLocalizationUuid() != null,
                "Localization requires a localizationUuid. Null was found.");
        Preconditions.require(localization.getConcept() != null,
                "A Localization requires a concept. Null was found");
        Preconditions.require(localization.getElapsedTime() != null,
                "A localization requires an elapsedtime. Null was found");
        Preconditions.require(localization.getX() != null,
                "A localization requires an x value. Null was found");
        Preconditions.require(localization.getX() >= 0,
                "A localization can not have a negative x coordinate. " +
                        localization.getX() + " + was found.");
        Preconditions.require(localization.getY() != null,
                "A localization requires an y value. Null was found");
        Preconditions.require(localization.getY() >= 0,
                "A localization can not have a negative y coordinate. " +
                        localization.getX() + " + was found.");
        Preconditions.require(localization.getWidth() != null,
                "A localization requires a width value. Null was found");
        Preconditions.require(localization.getWidth() > 0,
                "A localization can not have a width less than 1 pixel." +
                        localization.getWidth() + " + was found.");
        Preconditions.require(localization.getHeight() != null,
                "A localization requires an height value. Null was found");
        Preconditions.require(localization.getHeight() > 0,
                "A localization can not have a height less than 1 pixel. " +
                        localization.getHeight() + " + was found.");
    }

    /**
     * Removes a localization from the internal cache as well as from any remote subscribers
     * @param localization THe only field read is the localizationUuid
     */
    public void removeLocalization(Localization localization) {
        removeLocalizations(List.of(localization));
    }

    /**
     * Removes a localization from the internal cache as well as from remote subscribers
     * @param localizationUuid The UUID of the localization to be removed.
     */
    public void removeLocalization(UUID localizationUuid) {
        Preconditions.require(localizationUuid != null,
                "removeLocalization(null) is not allowed");
        Localization a = new Localization();
        a.setLocalizationUuid(localizationUuid);
        removeLocalization(a);
    }

    public void removeLocalizations(Collection<Localization> localizations) {
        localizations.forEach(this::validateLocalizationForRemove);
        Message msg = new Message(Message.ACTION_REMOVE, new ArrayList<>(localizations));
        incoming.onNext(msg);
        outgoing.onNext(msg);
    }

    private void validateLocalizationForRemove(Localization localization) {
        Preconditions.require(localization.getLocalizationUuid() != null,
                "Can not remove a localization without a localizationUuid");
    }

    private void removeLocalizationsInternal(Collection<Localization> xs) {
        for (var x: xs) {
            try {
                removeLocalizationInternal(x);
            }
            catch (IllegalArgumentException e) {
                log.warn("Failed to remove a localization that was missing required values.", e);
            }
        }
    }

    private void removeLocalizationInternal(Localization localization) {
        boolean exists = false;
        Message msg = null;
        for (int i = 0; i< localizations.size(); i++) {
            Localization b = localizations.get(i);
            if (b.getLocalizationUuid().equals(localization.getLocalizationUuid())) {
                localizations.remove(i);
                exists = true;
                break;
            }
        }
        if (!exists) {
            log.debug("A localization with UUID of " + localization.getLocalizationUuid() +
                    " was not found. Unable to remove.");
        }
        if (msg != null) {
            log.debug("Removing localization (uuid = " + localization.getLocalizationUuid() +
                    ")");
        }
    }



}
