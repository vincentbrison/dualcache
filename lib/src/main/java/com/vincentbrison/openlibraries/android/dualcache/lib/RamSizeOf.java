package com.vincentbrison.openlibraries.android.dualcache.lib;

/**
 * Interface used to describe how to compute the size of an object in RAM.
 * @param <T> is the class of object on which this computation is done.
 */
public interface RamSizeOf<T> {

    /**
     * Compute the amount of RAM used by this object.
     * @param object on which the computation has to be done.
     * @return the size in bytes of the object in RAM.
     */
    int sizeOf(T object);
}
