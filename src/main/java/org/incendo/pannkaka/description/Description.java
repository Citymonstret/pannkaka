package org.incendo.pannkaka.description;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Description that can describe an object
 */
public interface Description {

    /**
     * Get a string based description
     *
     * @return String description
     */
    @NonNull String toString();

}
