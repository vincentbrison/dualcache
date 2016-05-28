package com.vincentbrison.openlibraries.android.dualcache.ramlrucache;

import com.vincentbrison.openlibraries.android.dualcache.RamSizeOf;

/**
 * This is the LRU cache used for the RAM layer when configured to used references.
 * @param <T> is the class of object stored in the cache.
 */
public class ReferenceLRUCache<T> extends RamLruCache<String, T> {

    private RamSizeOf<T> mHandlerRamSizeOf;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     *
     * @param handler is the cacheinterface used to compute the size of each object stored in the ram cache layer.
     */
    public ReferenceLRUCache(int maxSize, RamSizeOf<T> handler) {
        super(maxSize);
        mHandlerRamSizeOf = handler;
    }

    @Override
    protected int sizeOf(String key, T value) {
        return mHandlerRamSizeOf.sizeOf(value);
    }
}
