package org.mbari.vcr4j.sharktopoda.client.localization;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Brian Schlining
 * @since 2020-02-12T13:52:00
 */
public class LocalizationControllerTest {


    LocalizationController controller = new LocalizationController();
    List<Message> in = new ArrayList<>();
    List<Message> out = new ArrayList<>();
    private boolean hasInitRun = false;


    public void reset() {
        if (!hasInitRun) {
            controller.getOutgoing()
                    .ofType(Message.class)
                    .subscribe(in::add);
            controller.getIncoming()
                    .ofType(Message.class)
                    .subscribe(out::add);
            hasInitRun = true;
        }
        controller.clearAllLocalizations();
        in.clear();
        out.clear();
    }


    @Test
    public void testAddSingle() {
        reset();
        controller.addLocalization(DataGenerator.newLocalization());
        assertEquals(controller.getLocalizations().size(),1);
        compare(1, in , out);
        controller.clearAllLocalizations();
    }

    @Test
    public void testAddMany() {
        reset();
        var xs = DataGenerator.newLocalizations(10);
        xs.forEach(controller::addLocalization);
        compare(10, in, out);
    }

    public void testUpdate() {

    }

    public void testRemove() {

    }

    private void compare(int expectedSize, List<Message> in, List<Message> out) {
        assertEquals(expectedSize, in.size());
        assertEquals(expectedSize, out.size());
        for (int i = 0; i < expectedSize; i++) {
            compare(in.get(i), out.get(i));
        }
    }

    private void compare(Message a, Message b) {
        assertEquals(a.getAction(), b.getAction());
        assertEquals(a.getLocalizations().size(), b.getLocalizations().size());
        for (int i = 0; i < a.getLocalizations().size(); i++) {
            compare(a.getLocalizations().get(i), b.getLocalizations().get(i));
        }
    }

    private void compare(Localization a, Localization b) {
        assertEquals(a.getLocalizationUuid(), b.getLocalizationUuid());
        assertEquals(a.getHeight(), b.getHeight());
        assertEquals(a.getWidth(), b.getWidth());
        assertEquals(a.getY(), b.getY());
        assertEquals(a.getX(), b.getX());
        assertEquals(a.getElapsedTime(), b.getElapsedTime());
        assertEquals(a.getConcept(), b.getConcept());
    }
}