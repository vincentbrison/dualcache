package com.vincentbrison.openlibraries.android.dualcache;

/**
 * Define the behaviour of the RAM layer.
 */
public enum DualCacheRamMode {
    /**
     * Means that object will be serialized with a specific serializer in RAM.
     */
    ENABLE_WITH_SPECIFIC_SERIALIZER,

    /**
     * Means that only references to objects will be stored in the RAM layer.
     */
    ENABLE_WITH_REFERENCE,

    /**
     * The RAM layer is not used.
     */
    DISABLE
}
