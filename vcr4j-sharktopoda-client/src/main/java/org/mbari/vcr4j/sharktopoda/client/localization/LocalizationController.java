package org.mbari.vcr4j.sharktopoda.client.localization;


import io.reactivex.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.mbari.vcr4j.sharktopoda.client.IOBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2020-02-10T13:44:00
 */
public class LocalizationController extends IOBus {

    private final Comparator<Localization> uuidComparator = Comparator.comparing(Localization::getLocalizationUuid);

    private final ObservableList<Localization> localizations = FXCollections.observableArrayList();

    private final ObservableList<Localization> uuidLocalizations =
            new SortedList<>(localizations, uuidComparator);

    private final Comparator<Localization> elapsedTimeComparator = Comparator.comparing(Localization::getElapsedTime);

    private final ObservableList<Localization> elapsedTimeLocalizations =
            new SortedList<>(localizations, elapsedTimeComparator);

    private final Logger log = LoggerFactory.getLogger(getClass());


    public LocalizationController() {

        Observable<Message> msgObservable = incoming.ofType(Message.class);

        msgObservable
                .filter(msg -> Message.ACTION_ADD.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::addLocalizations,
                    e -> log.warn("An error occurred on the incoming localization bus", e));

        msgObservable
                .filter(msg -> Message.ACTION_REMOVE.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::removeLocalizationsInternal,
                        e -> log.warn("An error occurred on the incoming localization bus", e));

        msgObservable
                .filter(msg -> Message.ACTION_SET.equalsIgnoreCase(msg.getAction()))
                .map(Message::getLocalizations)
                .subscribe(this::setLocalizations);

        msgObservable
                .filter(msg -> Message.ACTION_CLEAR_ALL.equalsIgnoreCase(msg.getAction()))
                .subscribe(msg -> clearAllLocalLocalizations());

        msgObservable
                .filter(msg -> Message.ACTION_CLEAR_VIDEO.equalsIgnoreCase(msg.getAction()))
                .subscribe(msg ->
                     msg.getLocalizations()
                          .stream()
                          .map(Localization::getVideoReferenceUuid)
                          .forEach(this::clearLocalLocalizationsForVideo));
    }

    /**
     * This is the internal cache of localizations. You can observe this list for changes, both
     * remote and locally triggered.
     *
     * @return A read-only list of localizations
     */
    public ObservableList<Localization> getLocalizations() {
        return elapsedTimeLocalizations;
    }

    /**
     * Set the internal cache of localizations. The collection you provide is copied into the internal
     * cache
     * @param xs
     */
    public synchronized void setLocalizations(Collection<Localization> xs) {
        localizations.clear();
        localizations.addAll(xs);
    }

    /**
     * Sets the local and remote cache of localizations. Very useful when you want to sync the
     * localizations in both clients
     * @param xs
     */
    public void setAllLocalizations(Collection<Localization> xs) {
        setLocalizations(xs);
        setRemoteLocalizations(xs);
    }

    /**
     * Sets the cache of localizations used by any remote clients listening to this controller.
     * @param xs
     */
    public void setRemoteLocalizations(Collection<Localization> xs) {
        outgoing.onNext(new Message(Message.ACTION_SET, new ArrayList<>(xs)));
    }

    /**
     * Adds localizations to both local and remote caches.
     * @param xs
     */
    public synchronized void addLocalizations(Iterable<Localization> xs) {
        var toBeAdded = new ArrayList<Localization>();
        var toBeRemoved = new ArrayList<Localization>();
        for (var x : xs) {
            var i = Collections.binarySearch(uuidLocalizations, x, uuidComparator);
            if (i >= 0) {
                log.debug("Replacing localization (uuid = " + x.getLocalizationUuid() +
                        ", elapsedTime = " + x.getElapsedTime().toMillis() + "ms)");
                toBeRemoved.add(uuidLocalizations.get(i));
            }
            else {
                log.debug("Adding localization (uuid = " + x.getLocalizationUuid() +
                        ", elapsedTime = " + x.getElapsedTime().toMillis() + "ms)");
            }
            toBeAdded.add(x);
        }
        localizations.removeAll(toBeRemoved);
        localizations.addAll(toBeAdded);
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
     * Removes a localization from the internal cache as well as from any remote subscribers
     * @param localization THe only field read is the localizationUuid
     */
    public void removeLocalization(Localization localization) {
        Preconditions.require(localization.getLocalizationUuid() != null,
                "Can not remove a localization without a localizationUuid");
        removeLocalization(localization.getLocalizationUuid());
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
        Message msg = new Message(Message.ACTION_REMOVE, a);
        incoming.onNext(msg);
    }

    /**
     * Clears all localizations from both the internal local cache and all remote subscribers.
     */
    public void clearAllLocalizations() {
        clearAllLocalLocalizations();
        clearAllRemoteLocalizations();
    }

    /**
     * Clears the internal cache. Does not effect remote subscribers
     */
    public void clearAllLocalLocalizations() {
        localizations.clear();
    }

    /**
     * Clears remote subscribers. Does not effect the internal cache of `this`
     */
    public void clearAllRemoteLocalizations() {
        outgoing.onNext(new Message(Message.ACTION_CLEAR_ALL));
    }

    /**
     * Clears out localizations for a given video, based on its UUID. This applys to both the local
     * and remote caches.
     * @param videoReferenceUuid
     */
    public void clearLocalizationsForVideo(UUID videoReferenceUuid) {
        clearLocalLocalizationsForVideo(videoReferenceUuid);
        clearRemoteLocalizationsForVideo(videoReferenceUuid);
    }

    /**
     * Clears the localizations for a given video from remote subscribers. The local cache is not
     * affected.
     * @param videoReferenceUuid
     */
    public void clearRemoteLocalizationsForVideo(UUID videoReferenceUuid) {
        Localization x = new Localization();
        x.setVideoReferenceUuid(videoReferenceUuid);
        outgoing.onNext(new Message(Message.ACTION_CLEAR_VIDEO, x));
    }

    /**
     * Clears the localization for a given video from the local cache. Remote subscribers are not
     * affected.
     * @param videoReferenceUuid
     */
    public void clearLocalLocalizationsForVideo(UUID videoReferenceUuid) {
        List<Localization> toBeCleared = localizations.stream()
                .filter(m -> videoReferenceUuid.equals(m.getVideoReferenceUuid()))
                .collect(Collectors.toList());
        localizations.removeAll(toBeCleared);
    }




    /**
     * DO NOT CALL DIRECTLY
     * @param xs
     */
    private void removeLocalizationsInternal(Collection<Localization> xs) {

        var deletedLocalizations = new ArrayList<Localization>();
        for (var x : xs) {
            var i = Collections.binarySearch(localizations, x, uuidComparator);
            if (i >= 0) {
                log.debug("Removing localization (uuid = " + x.getLocalizationUuid() + ")");
                deletedLocalizations.add(x);
            }
            else {
                log.debug("A localization with UUID of " + x.getLocalizationUuid() +
                        " was not found. Unable to remove.");
            }
        }

        if (!deletedLocalizations.isEmpty()) {
            localizations.removeAll(deletedLocalizations);
            var msg = new Message(Message.ACTION_REMOVE, deletedLocalizations);
            outgoing.onNext(msg);
        }

    }



}
