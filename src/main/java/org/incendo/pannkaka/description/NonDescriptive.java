package org.incendo.pannkaka.description;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * Wrapper for non-descriptive objects
 */
public final class NonDescriptive implements Descriptive {

    private final @NonNull Object object;

    private NonDescriptive(final @NonNull Object object) {
        this.object = object;
    }

    /**
     * Create a new wrapper
     *
     * @param object Object to wrap
     * @return Wrapped object
     */
    public static @NonNull NonDescriptive of(final @NonNull Object object) {
        return new NonDescriptive(object);
    }

    @Override
    public @NonNull Description getDescription() {
        return SimpleDescription.of(String.format("[%s] %s", object.getClass().getSimpleName(), object.toString()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NonDescriptive that = (NonDescriptive) o;
        return object.equals(that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }

}
