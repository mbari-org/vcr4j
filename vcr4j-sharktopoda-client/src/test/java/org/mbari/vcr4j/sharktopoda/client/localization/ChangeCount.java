package org.mbari.vcr4j.sharktopoda.client.localization;

import javafx.collections.ListChangeListener;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author Brian Schlining
 * @since 2020-03-06T11:32:00
 */
public class ChangeCount {
    final AtomicInteger permutationCount = new AtomicInteger();
    final AtomicInteger addedCount = new AtomicInteger();
    final AtomicInteger updatedCount = new AtomicInteger();
    final AtomicInteger replacedCount = new AtomicInteger();
    final AtomicInteger removedCount = new AtomicInteger();

    void assertCount(int p, int a, int u, int r, int rm) {
        assertEquals("Permutation count is wrong", p, permutationCount.get());
        assertEquals("Added count is wrong", a, addedCount.get());
        assertEquals("Updated count is wrong", u, updatedCount.get());
        assertEquals("Replaced count is wrong", r, replacedCount.get());
        assertEquals("Removed count is wrong", rm, removedCount.get());
    }

    void resetCount() {
        permutationCount.set(0);
        addedCount.set(0);
        updatedCount.set(0);
        replacedCount.set(0);
        removedCount.set(0);
    }

    ListChangeListener<? super Localization> newListener() {
        return (ListChangeListener<Localization>) change -> {
            while (change.next()) {
                if (change.wasAdded()) addedCount.incrementAndGet();
                if (change.wasPermutated()) permutationCount.incrementAndGet();
                if (change.wasUpdated()) updatedCount.incrementAndGet();
                if (change.wasReplaced()) replacedCount.incrementAndGet();
                if (change.wasRemoved()) removedCount.incrementAndGet();
            }
        };
    }
}