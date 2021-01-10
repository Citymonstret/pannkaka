package org.incendo.pannkaka.description;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * Simple (static) {@link Description}
 */
public final class SimpleDescription implements Description {

    private final @NonNull String description;

    private SimpleDescription(final @NonNull String description) {
        this.description = description;
    }

    /**
     * Create a simple (static) description
     *
     * @param description Description string
     * @return Created description
     */
    public static @NonNull SimpleDescription of(final @NonNull String description) {
        return new SimpleDescription(description);
    }

    @Override
    public @NonNull String toString() {
        return this.description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimpleDescription that = (SimpleDescription) o;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

}
