package com.vincentbrison.openlibraries.android.dualcache.lib;

import com.vincentbrison.openlibraries.android.dualcache.lib.ramlrucache.ReferenceLRUCache;
import com.vincentbrison.openlibraries.android.dualcache.lib.ramlrucache.StringLRUCache;

/**
 * Class used to build a cache.
 * @param <T> is the class of object to store in cache.
 */
public class DualCacheBuilder<T> {

    private DualCache<T> mDualCache;

    /**
     * Start the building of the cache.
     * @param id is the id of the cache (should be unique).
     * @param appVersion is the app version of the app. If data are already stored in disk cache with previous app version, it will be invalidate.
     * @param clazz is the class of object to store in cache.
     */
    public DualCacheBuilder(String id, int appVersion, Class<T> clazz, boolean isLogEnable) {
        mDualCache = new DualCache<>(id, appVersion, clazz, new DualCacheLogger(isLogEnable));
    }

    /**
     * Use Json serialization/deserialization to store and retrieve object from ram cache.
     * @param maxRamSize is the max amount of ram which can be used by the ram cache.
     * @return the builder for the disk cache layer.
     */
    public DualCacheDiskBuilder<T> useDefaultSerializerInRam(int maxRamSize) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_DEFAULT_SERIALIZER);
        mDualCache.setRamCacheLru(new StringLRUCache(maxRamSize));
        return new DualCacheDiskBuilder<>(mDualCache);
    }

    /**
     * Use Json serialization/deserialization to store and retrieve object from ram cache.
     * @param maxRamSize is the max amount of ram which can be used by the ram cache.
     * @param serializer is the interface with provide serialization/deserialization methods for the ram cache layer.
     * @return the builder for the disk cache layer.
     */
    public DualCacheDiskBuilder<T> useCustomSerializerInRam(int maxRamSize, Serializer<T> serializer) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_CUSTOM_SERIALIZER);
        mDualCache.setRamCacheLru(new StringLRUCache(maxRamSize));
        mDualCache.setRAMSerializer(serializer);
        return new DualCacheDiskBuilder<>(mDualCache);
    }

    /**
     * Store directly object in ram. You have to provide a way to compute the size of an object in ram to be able to used the LRU capacity of the ram cache.
     * @param maxRamSize is the max amount of ram which can be used by the ram cache.
     * @param handlerRamSizeOf is the interface which let compute the size of object stored in ram.
     * @return the builder for the disk cache layer.
     */
    public DualCacheDiskBuilder<T> useReferenceInRam(int maxRamSize, RamSizeOf<T> handlerRamSizeOf) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_REFERENCE);
        mDualCache.setRamCacheLru(new ReferenceLRUCache<T>(maxRamSize, handlerRamSizeOf));
        return new DualCacheDiskBuilder<>(mDualCache);
    }

    /**
     * The ram cache will not be used, meaning that only the disk cache will be used.
     * @return the builder for the disk cache layer.
     */
    public DualCacheDiskBuilder<T> noRam() {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.DISABLE);
        return new DualCacheDiskBuilder<>(mDualCache);
    }

}
