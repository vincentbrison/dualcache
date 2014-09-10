package com.vincentbrison.openlibraries.android.dualcache.lib;

/**
 *
 */
public class DualCacheBuilder<T> {

    private DualCache<T> mDualCache;

    public DualCacheBuilder(String id, int appVersion, Class<T> clazz) {
        mDualCache = new DualCache(id, appVersion, clazz);
    }

    public DualCacheBuilder<T> useJsonInRam(int maxRamSize) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_JSON);
        mDualCache.mRamCacheLru = new StringLRUCache(maxRamSize);
        return this;
    }

    public DualCacheBuilder<T> useReferenceInRam(int maxRamSize, SizeOf<T> handlerSizeOf) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_REFERENCE);
        mDualCache.mRamCacheLru = new ReferenceLRUCache(maxRamSize, handlerSizeOf);
        return this;
    }

    public DualCache build() {
        return mDualCache;
    }
}
