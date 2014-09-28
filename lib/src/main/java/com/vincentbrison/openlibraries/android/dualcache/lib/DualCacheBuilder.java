package com.vincentbrison.openlibraries.android.dualcache.lib;

public class DualCacheBuilder<T> {

    DualCache<T> mDualCache;

    public DualCacheBuilder(String id, int appVersion, Class<T> clazz) {
        mDualCache = new DualCache(id, appVersion, clazz);
    }

    public DualCacheDiskBuilder<T> useJsonInRam(int maxRamSize) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_DEFAULT_SERIALIZER);
        mDualCache.mRamCacheLru = new StringLRUCache(maxRamSize);
        return new DualCacheDiskBuilder<T>(mDualCache);
    }

    public DualCacheDiskBuilder<T> useReferenceInRam(int maxRamSize, SizeOf<T> handlerSizeOf) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_REFERENCE);
        mDualCache.mRamCacheLru = new ReferenceLRUCache(maxRamSize, handlerSizeOf);
        return new DualCacheDiskBuilder<T>(mDualCache);
    }

    public DualCacheDiskBuilder<T> noRam() {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.DISABLE);
        return new DualCacheDiskBuilder<T>(mDualCache);
    }

}
