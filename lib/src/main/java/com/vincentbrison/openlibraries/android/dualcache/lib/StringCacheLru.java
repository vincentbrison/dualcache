package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.support.v4.util.LruCache;

/**
 * Created by A559998 on 11/07/2014.
 */
public class StringCacheLru extends LruCache<String, String> {
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public StringCacheLru(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, String value) {
        return value.getBytes().length;
    }
}
