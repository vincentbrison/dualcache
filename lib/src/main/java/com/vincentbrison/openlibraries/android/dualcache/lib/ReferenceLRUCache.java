package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.support.v4.util.LruCache;

/**
 * Created by Brize on 10/09/2014.
 */
public class ReferenceLRUCache<T> extends LruCache<String, T> {

    private SizeOf<T> mHandlerSizeOf;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
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
