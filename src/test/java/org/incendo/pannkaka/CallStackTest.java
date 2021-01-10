package org.incendo.pannkaka;

import org.incendo.pannkaka.description.SimpleDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;
import java.util.concurrent.Executors;

public class CallStackTest {

    @Test
    void testPopPush() {
        final CallStack callStack = CallStack.empty();
        callStack.push(SimpleDescription.of("First"));
        callStack.push(SimpleDescription.of("Second"));
        Assertions.assertEquals("Second", callStack.pop().toString());
        Assertions.assertEquals("First", callStack.pop().toString());
    }

    @Test
    void testThreadGuard() throws Exception {
        final CallStack callStack = CallStack.empty();
        final Thread thread = new Thread(() -> {
            try {
                callStack.push(SimpleDescription.of("First"));
            } catch (final Exception ignored) {
            }
        });
        thread.start();
        thread.join();
        Assertions.assertThrows(EmptyStackException.class, callStack::pop);
    }

    @Test
    void testTransfer() throws Exception {
        final CallStack callStack = CallStack.empty();
        callStack.push(SimpleDescription.of("First"));
        final CallStack migrated = callStack.migrate();
        Assertions.assertEquals("First", Executors.newSingleThreadExecutor().submit(() -> {
            migrated.install();
            return migrated.pop();
        }).get().toString());
    }

    @Test
    void testAutoTransfer() throws Exception {
        final CallStack callStack = CallStack.empty();
        callStack.push(SimpleDescription.of("First"));
        Assertions.assertEquals("First", callStack.wrapExecutor(Executors.newSingleThreadExecutor()).submit(() -> CallStack
                .get()
                .pop())
                .get()
                .toString());
    }

    @Test
    void testNonDescriptive() {
        final CallStack callStack = CallStack.empty();
        callStack.push(10);
        Assertions.assertEquals("[Integer] 10", callStack.pop().toString());
    }

}
