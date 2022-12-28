package org.mbari.vcr4j.decorators;

/**
 * For this project, Decorators are classes that manage a bundle of subscribers to
 * VideoIO objects. Generally, using their constructor wires them up. If you no longer
 * need them just call `unsubscribe()`.
 *
 * @author Brian Schlining
 * @since 2016-02-03T13:27:00
 */
public interface Decorator {

    /**
     * Tells the decorator to unsubscribe from all Observables that it is watching.
     */
    void unsubscribe();

}
