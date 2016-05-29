package com.vincentbrison.openlibraries.android.dualcache;

import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;
import com.vincentbrison.openlibraries.android.dualcache.ramlrucache.ReferenceLRUCache;
import com.vincentbrison.openlibraries.android.dualcache.ramlrucache.BytesLRUCache;

import java.io.File;
import java.io.IOException;

/**
 * Class used to build a cache.
 * @param <T> is the class of object to store in cache.
 */
public class Builder<T> {

    private String id;
    private int appVersion;
    private Class<T> clazz;

    private boolean logEnabled;

    private int maxRamSizeBytes;
    private DualCache.DualCacheRAMMode ramMode;
    private CacheSerializer<T> ramSerializer;
    private RamSizeOf<T> ramSizeOf;

    private int maxDiskSizeBytes;
    private DualCache.DualCacheDiskMode diskMode;
    private CacheSerializer<T> diskSerializer;
    private File diskFolder;

    /**
     * Start the building of the cache.
     * @param id is the id of the cache (should be unique).
     * @param appVersion is the app version of the app. If data are already stored in disk cache
     *                   with previous app version, it will be invalidate.
     * @param clazz is the class of object to store in cache.
     */
    public Builder(String id, int appVersion, Class<T> clazz) {
        this.id = id;
        this.appVersion = appVersion;
        this.clazz = clazz;
        this.ramMode = null;
        this.diskMode = null;
        this.logEnabled = false;
    }

    public Builder<T> logEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
        return this;
    }

    public DualCache<T> build() {
        if (ramMode == null) {
            throw new IllegalStateException("No ram mode set");
        }
        if (diskMode == null) {
            throw new IllegalStateException("No disk mode set");
        }

        DualCache<T> cache =
            new DualCache<>(id, appVersion, clazz, new Logger(logEnabled));

        cache.setRAMMode(ramMode);
        switch (ramMode) {
            case ENABLE_WITH_CUSTOM_SERIALIZER:
                cache.setRAMSerializer(ramSerializer);
                cache.setRamCacheLru(new BytesLRUCache(maxRamSizeBytes));
                break;
            case ENABLE_WITH_REFERENCE:
                cache.setRamCacheLru(new ReferenceLRUCache<>(maxRamSizeBytes, ramSizeOf));
                break;
        }

        cache.setDiskMode(diskMode);
        switch (diskMode) {
            case ENABLE_WITH_CUSTOM_SERIALIZER:
                cache.setDiskSerializer(this.diskSerializer);
                cache.setDiskCacheSizeInBytes(this.maxDiskSizeBytes);
                try {
                    DiskLruCache diskLruCache =
                        DiskLruCache.open(this.diskFolder, this.appVersion, 1, this.maxDiskSizeBytes);
                    cache.setDiskLruCache(diskLruCache);
                    cache.setDiskCacheFolder(this.diskFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }

        boolean isRamDisable = cache.getRAMMode().equals(DualCache.DualCacheRAMMode.DISABLE);
        boolean isDiskDisable = cache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE);

        if (isRamDisable && isDiskDisable) {
            throw new IllegalStateException("The ram cache layer and the disk cache layer are disable. You have to use at least one of those layers.");
        }

        return cache;
    }

    /**
     * Use Json serialization/deserialization to store and retrieve object from ram cache.
     * @param maxRamSizeBytes is the max amount of ram in bytes which can be used by the ram cache.
     * @param serializer is the cacheinterface with provide serialization/deserialization methods
     *                   for the ram cache layer.
     * @return the builder for the disk cache layer.
     */
    public Builder<T> useSerializerInRam(
        int maxRamSizeBytes, CacheSerializer<T> serializer
    ) {
        this.ramMode = DualCache.DualCacheRAMMode.ENABLE_WITH_CUSTOM_SERIALIZER;
        this.maxRamSizeBytes = maxRamSizeBytes;
        this.ramSerializer = serializer;
        return this;
    }

    /**
     * Store directly object in ram. You have to provide a way to compute the size of an object in
     * ram to be able to used the LRU capacity of the ram cache.
     * @param maxRamSizeBytes is the max amount of ram which can be used by the ram cache.
     * @param handlerRamSizeOf is the cacheinterface which let compute the size of object stored in
     *                         ram.
     * @return the builder for the disk cache layer.
     */
    public Builder<T> useReferenceInRam(
        int maxRamSizeBytes, RamSizeOf<T> handlerRamSizeOf
    ) {
        this.ramMode = DualCache.DualCacheRAMMode.ENABLE_WITH_REFERENCE;
        this.maxRamSizeBytes = maxRamSizeBytes;
        this.ramSizeOf = handlerRamSizeOf;
        return this;
    }

    /**
     * The ram cache will not be used, meaning that only the disk cache will be used.
     * @return the builder for the disk cache layer.
     */
    public Builder<T> noRam() {
        this.ramMode = DualCache.DualCacheRAMMode.DISABLE;
        return this;
    }

    /**
     * Use custom serialization/deserialization to store and retrieve object from disk cache.
     * @param maxDiskSizeBytes is the max size of disk in bytes which an be used by the disk cache layer.
     * @param usePrivateFiles is true if you want to use {@link Context#MODE_PRIVATE} with the default disk cache folder.
     * @param serializer is the cacheinterface with provide serialization/deserialization methods for the disk cache layer.
     * @param context is used to access file system.
     * @return the "ready to use" dualcache.
     */
    public Builder<T> useSerializerInDisk(
        int maxDiskSizeBytes, boolean usePrivateFiles, CacheSerializer<T> serializer, Context context
    ) {
        File folder = getDefaultDiskCacheFolder(usePrivateFiles, context);
        return useSerializerInDisk(maxDiskSizeBytes, folder, serializer);
    }

    /**
     * Use custom serialization/deserialization to store and retrieve object from disk cache.
     * @param maxDiskSizeBytes is the max size of disk in bytes which an be used by the disk cache layer.
     * @param diskCacheFolder is the folder where the disk cache will be stored.
     * @param serializer is the cacheinterface with provide serialization/deserialization methods for the disk cache layer.
     * @return the "ready to use" dualcache.
     */
    public Builder<T> useSerializerInDisk(
        int maxDiskSizeBytes, File diskCacheFolder, CacheSerializer<T> serializer
    ) {
        this.diskFolder = diskCacheFolder;
        this.diskMode = DualCache.DualCacheDiskMode.ENABLE_WITH_CUSTOM_SERIALIZER;
        this.maxDiskSizeBytes = maxDiskSizeBytes;
        this.diskSerializer = serializer;
        return this;
    }

    private File getDefaultDiskCacheFolder(boolean usePrivateFiles, Context context) {
        File folder = null;
        if (usePrivateFiles) {
            folder = context.getDir(
                DualCache.CACHE_FILE_PREFIX + this.id,
                Context.MODE_PRIVATE
            );
        } else {
            folder = new File(context.getCacheDir().getPath()
                + "/" + DualCache.CACHE_FILE_PREFIX
                + "/" + this.id
            );
        }
        return folder;
    }

    /**
     * Use this if you do not want use the disk cache layer, meaning that only the ram cache layer will be used.
     * @return the "ready to use" dualcache.
     */
    public Builder<T> noDisk() {
        this.diskMode = DualCache.DualCacheDiskMode.DISABLE;
        return this;
    }
}
