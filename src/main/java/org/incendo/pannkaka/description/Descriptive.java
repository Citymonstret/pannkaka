package org.incendo.pannkaka.description;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An object that can be described
 */
@FunctionalInterface
public interface Descriptive {

    /**
     * Get the description for this object
     *
     * @return Description that describes this object
     */
    @NonNull Description getDescription();

}
