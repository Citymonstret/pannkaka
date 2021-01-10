package org.incendo.pannkaka;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.pannkaka.description.Description;
import org.incendo.pannkaka.description.Descriptive;
import org.incendo.pannkaka.description.NonDescriptive;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CallStack implements Iterable<@NonNull Description> {

    private static final @NonNull ThreadLocal<CallStack> threadStack = new ThreadLocal<>();

    private final @NonNull AtomicBoolean writeLocked = new AtomicBoolean(false);
    private final @NonNull Stack<@NonNull Description> internalStack = new Stack<>();
    private @MonotonicNonNull Thread thread;

    private CallStack(final @NonNull Stack<@NonNull Description> stack) {
        this.internalStack.addAll(stack);
    }

    private CallStack() {
    }

    /**
     * Create a new empty call stack and install it to the calling thread
     *
     * @return Created call stack
     */
    public static @NonNull CallStack empty() {
        return new CallStack().install();
    }

    /**
     * Get the currently installed thread stack for the calling thread,
     * or create and install a new one
     *
     * @return The call stack associated with the calling thread
     */
    public static @NonNull CallStack get() {
        final CallStack stack = threadStack.get();
        if (stack == null) {
            return empty();
        }
        return stack;
    }

    /**
     * Install the {@link CallStack} in the executing {@link Thread}
     *
     * @return {@code this}
     */
    public synchronized @NonNull CallStack install() {
        if (this.thread != null) {
            throw new IllegalStateException("Cannot replace the owning thread");
        }
        threadStack.set(this);
        this.thread = Thread.currentThread();
        return this;
    }

    /**
     * Wrap a {@link ExecutorService}. The wrapped executor will receive
     * a copy of this call stack that will automatically be installed
     * whenever the executor runs a command.
     *
     * @param executor Executor to wrap
     * @return Wrapped executor
     */
    public synchronized @NonNull ExecutorService wrapExecutor(final @NonNull ExecutorService executor) {
        return new AbstractExecutorService() {
            @Override
            public void shutdown() {
                executor.shutdown();
            }

            @Override
            public @NonNull List<Runnable> shutdownNow() {
                return executor.shutdownNow();
            }

            @Override
            public boolean isShutdown() {
                return executor.isShutdown();
            }

            @Override
            public boolean isTerminated() {
                return executor.isTerminated();
            }

            @Override
            public boolean awaitTermination(final long timeout, final @NonNull TimeUnit unit) throws InterruptedException {
                return executor.awaitTermination(timeout, unit);
            }

            @Override
            public void execute(final @NonNull Runnable command) {
                final CallStack split = split();
                executor.execute(() -> {
                    split.install();
                    command.run();
                });
            }
        };
    }

    /**
     * Push an object to the stack
     *
     * @param description Object to push
     */
    public synchronized void push(final @NonNull Description description) {
        this.checkWrite();
        this.internalStack.add(description);
    }

    /**
     * Push an object to the stack
     *
     * @param descriptive Object to push
     */
    public synchronized void push(final @NonNull Descriptive descriptive) {
        this.push(descriptive.getDescription());
    }

    /**
     * Push a new object to the stack
     *
     * @param object Object to push
     */
    public synchronized void push(final @NonNull Object object) {
        this.push(NonDescriptive.of(object));
    }

    /**
     * Pop an object from the stack
     *
     * @return Popped object
     */
    public synchronized @NonNull Description pop() {
        this.checkWrite();
        return this.internalStack.pop();
    }

    /**
     * Create a new call stack based on the current callstack.
     * Modifying this or the created call stack will not modify
     * the other. They are completely disjoint after creation.
     * <p>
     * This must be installed ({@link #install()}) before it can be used.
     *
     * @return New call stack
     */
    public synchronized @NonNull CallStack split() {
        return new CallStack(this.internalStack);
    }

    /**
     * Create a new call stack based on this call stack, without
     * an owning thread. It must be installed ({@link #install()})
     * before it can be used. This will invalidate the current
     * call stack.
     *
     * @return New call stack
     */
    public synchronized @NonNull CallStack migrate() {
        this.writeLocked.set(true);
        return new CallStack(this.internalStack);
    }

    private void checkWrite() {
        if (writeLocked.get()) {
            throw new IllegalStateException("Cannot mutate a locked call stack");
        }
        if (this.thread == null) {
            throw new IllegalStateException("The call stack has not been associated with a thread");
        }
        if (!this.equals(threadStack.get())) {
            throw new IllegalStateException("The call stack has not been associated with the current thread");
        }
        if (!Thread.currentThread().equals(this.thread)) {
            throw new IllegalStateException("The calling thread does not own this call stack");
        }
    }

    @Override
    public synchronized @NonNull Iterator<@NonNull Description> iterator() {
        return Collections.unmodifiableCollection(this.internalStack).iterator();
    }

}
