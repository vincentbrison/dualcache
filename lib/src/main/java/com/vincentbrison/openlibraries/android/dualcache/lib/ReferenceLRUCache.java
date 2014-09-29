package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.support.v4.util.LruCache;

/**
 * This is the LRU cache used for the RAM layer when configured to used references.
 * @param <T> is the class of object stored in the cache.
 */
public class ReferenceLRUCache<T> extends LruCache<String, T> {

    private SizeOf<T> mHandlerSizeOf;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     *
     * @param handler is the interface used to compute the size of each object stored in the ram cache layer.
     */
    public ReferenceLRUCache(int maxSize, SizeOf<T> handler) {
        super(maxSize);
        mHandlerSizeOf = handler;
    }

    @Override
    protected int sizeOf(String key, T value) {
        return mHandlerSizeOf.sizeOf(value);
    }
}
