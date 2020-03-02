package org.mbari.vcr4j.sharktopoda.client.localization;


import io.reactivex.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.mbari.vcr4j.sharktopoda.client.IOBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2020-02-10T13:44:00
 */
public class LocalizationController extends IOBus {

    private final ObservableList<Localization> localizations =
            FXCollections.observableArrayList();
    private final ObservableList<Localization> readonlyLocalizations =
            new SortedList<>(FXCollections.unmodifiableObservableList(localizations),
                    Comparator.comparing(Localization::getElapsedTime));
    private final Logger log = LoggerFactory.getLogger(getClass());


    public LocalizationController() {

        Observable<Message> msgObservable = incoming.ofType(Message.class);

        msgObservable
                .filter(msg -> Message.ACTION_ADD.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::addOrReplaceLocalizationsInternal,
                    e -> log.warn("An error occurred on the incoming localization bus", e));

        msgObservable
                .filter(msg -> Message.ACTION_REMOVE.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::deleteLocalizationsInternal,
                        e -> log.warn("An error occurred on the incoming localization bus", e));

        msgObservable
                .filter(msg -> Message.ACTION_SET.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::setLocalizations);

        msgObservable
                .filter(msg -> Message.ACTION_CLEAR_ALL.equalsIgnoreCase(msg.getAction()))
                .subscribe(msg -> clearAllLocalizations());

        msgObservable
                .filter(msg -> Message.ACTION_CLEAR_VIDEO.equalsIgnoreCase(msg.getAction()))
                .subscribe(msg -> {
                     msg.getLocalizations()
                          .stream()
                          .map(Localization::getVideoReferenceUuid)
                          .forEach(this::clearLocalizationsForVideo);
                });
    }

    public ObservableList<Localization> getLocalizations() {
        return readonlyLocalizations;
    }

    public void setLocalizations(Collection<Localization> xs) {
        localizations.clear();
        localizations.addAll(xs);
    }

    /**
     * Any new or modified localizations should be passed to this method. They
     * will be propagated as needed.
     * @param localization
     */
    public void addLocalization(Localization localization) {
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

        // Publish to internal buss so it's drawn immediately
        Message msg = new Message(Message.ACTION_ADD, localization);
        incoming.onNext(msg);

        // Publish to external bus to be published to remote app. After app does
        // something to it, it will be returned to the incoming bus where it
        // will update the existing one.
        outgoing.onNext(msg);
    }

    /**
     * DO NOT CALL DIRECTLY. Updates the localizations collection when a new
     * localization is received on the incoming bus.
     * @param a
     */
    private void addOrReplaceLocalizationInternal(Localization a) {

        boolean exists = false;
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

    private void addOrReplaceLocalizationsInternal(Collection<Localization> xs) {
        for (var x : xs) {
            try {
                addOrReplaceLocalizationInternal(x);
            } catch (IllegalArgumentException e) {
                log.warn("Failed to add a localization that was missing required values.", e);
            }
        }
    }

    public void removeLocalization(Localization localization) {
        Preconditions.require(localization.getLocalizationUuid() != null,
                "Can not remove a localization without a localizationUuid");
        removeLocalization(localization.getLocalizationUuid());
    }

    public void removeLocalization(UUID localizationUuid) {
        Preconditions.require(localizationUuid != null,
                "removeLocalization(null) is not allowed");
        Localization a = new Localization();
        a.setLocalizationUuid(localizationUuid);
        Message msg = new Message(Message.ACTION_REMOVE, a);
        incoming.onNext(msg);
    }

    /**
     * DO NOT CALL DIRECTLY.
     * @param localizationUuid
     */
    private void removeLocalizationInternal(UUID localizationUuid) {
        boolean exists = false;
        Message msg = null;
        for (int i = 0; i< localizations.size(); i++) {
            Localization b = localizations.get(i);
            if (b.getLocalizationUuid().equals(localizationUuid)) {
                localizations.remove(i);
                msg = new Message(Message.ACTION_REMOVE, b);
                exists = true;
                break;
            }
        }
        if (!exists) {
            log.debug("A localization with UUID of " + localizationUuid + " was not found. Unable to remove.");
        }
        if (msg != null) {
            log.debug("Removing localization (uuid = " + localizationUuid + ")");
            outgoing.onNext(msg);
        }
    }

    private void deleteLocalizationsInternal(Collection<Localization> localizations) {
        localizations.forEach(a -> removeLocalizationInternal(a.getLocalizationUuid()));
    }

    /**
     * Clears the internal cache of localizations. Does not delete them from remote
     * apps. Use this when switching media.
     */
    public void clearAllLocalizations() {
        localizations.clear();
    }

    public void clearLocalizationsForVideo(UUID videoReferenceUuid) {
        List<Localization> toBeCleared = localizations.stream()
                .filter(m -> videoReferenceUuid.equals(m.getVideoReferenceUuid()))
                .collect(Collectors.toList());
        localizations.removeAll(toBeCleared);
    }

}
