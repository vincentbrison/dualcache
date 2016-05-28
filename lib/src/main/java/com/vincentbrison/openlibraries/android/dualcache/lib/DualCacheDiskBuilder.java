package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * Class used to build a cache.
 * @param <T> is the class of the object stored in this cache.
 */
public class DualCacheDiskBuilder<T> {

    private DualCache<T> mDualCache;

    /**
     * Construct this builder.
     * @param dualCache is the dualcache to configure.
     */
    protected DualCacheDiskBuilder(DualCache<T> dualCache) {
        mDualCache = dualCache;
    }

    /**
     * Use custom serialization/deserialization to store and retrieve object from disk cache.
     * @param maxDiskSize is the max size of disk in bytes which an be used by the disk cache layer.
     * @param usePrivateFiles is true if you want to use {@link Context#MODE_PRIVATE} with the default disk cache folder.
     * @param serializer is the interface with provide serialization/deserialization methods for the disk cache layer.
     * @param context is used to access file system.
     * @return the "ready to use" dualcache.
     */
    public DualCache<T> useCustomSerializerInDisk(
        int maxDiskSize, boolean usePrivateFiles, Serializer<T> serializer, Context context
    ) {
        File folder = getDefaultDiskCacheFolder(usePrivateFiles, context);
        return useCustomSerializerInDiskIfProvided(maxDiskSize, folder, serializer, context);
    }

    /**
     * Use custom serialization/deserialization to store and retrieve object from disk cache.
     * @param maxDiskSize is the max size of disk in bytes which an be used by the disk cache layer.
     * @param diskCacheFolder is the folder where the disk cache will be stored.
     * @param serializer is the interface with provide serialization/deserialization methods for the disk cache layer.
     * @param context is used to access file system.
     * @return the "ready to use" dualcache.
     */
    public DualCache<T> useCustomSerializerInDisk(
        int maxDiskSize, File diskCacheFolder, Serializer<T> serializer, Context context
    ) {
        return useCustomSerializerInDiskIfProvided(maxDiskSize, diskCacheFolder, serializer, context);
    }

    /**
     * Use Json serializer/deserialiazer to store and retrieve object from the disk cache layer.
     * @param maxDiskSize is the max amount of disk in bytes which can be used by the disk cache layer.
     * @param usePrivateFiles is true if you want to use {@link Context#MODE_PRIVATE} with the default disk cache folder.
     * @param context is used to access file system.
     * @return the "ready to use" dualcache.
     */
    public DualCache<T> useDefaultSerializerInDisk(
        int maxDiskSize, boolean usePrivateFiles, Context context
    ) {
        File folder = getDefaultDiskCacheFolder(usePrivateFiles, context);
        return useCustomSerializerInDiskIfProvided(maxDiskSize, folder, null, context);
    }

    /**
     * Use Json serializer/deserialiazer to store and retrieve object from the disk cache layer.
     * @param maxDiskSize is the max amount of disk in bytes which can be used by the disk cache layer.
     * @param diskCacheFolder is the folder where the disk cache will be stored.
     * @param context is used to access file system.
     * @return the "ready to use" dualcache.
     */
    public DualCache<T> useDefaultSerializerInDisk(
        int maxDiskSize, File diskCacheFolder, Context context
    ) {
        return useCustomSerializerInDiskIfProvided(maxDiskSize, diskCacheFolder, null, context);
    }

    private File getDefaultDiskCacheFolder(boolean usePrivateFiles, Context context) {
        File folder = null;
        if (usePrivateFiles) {
            folder = context.getDir(
                DualCache.CACHE_FILE_PREFIX + mDualCache.getCacheId(),
                Context.MODE_PRIVATE
            );
        } else {
            folder = new File(context.getCacheDir().getPath()
                + "/" + DualCache.CACHE_FILE_PREFIX
                + "/" + mDualCache.getCacheId()
            );
        }
        return folder;
    }

    private DualCache<T> useCustomSerializerInDiskIfProvided(
        int maxDiskSize, File diskCacheFolder, Serializer<T> serializer, Context context
    ) {
        File crtDiskCacheFolder = diskCacheFolder;
        mDualCache.setDiskCacheSizeInBytes(maxDiskSize);

        if (serializer == null) {
            mDualCache.setDiskMode(DualCache.DualCacheDiskMode.ENABLE_WITH_DEFAULT_SERIALIZER);
        } else {
            mDualCache.setDiskMode(DualCache.DualCacheDiskMode.ENABLE_WITH_CUSTOM_SERIALIZER);
            mDualCache.setDiskSerializer(serializer);
        }

        if (crtDiskCacheFolder == null) {
            crtDiskCacheFolder = getDefaultDiskCacheFolder(false, context);
        }

        try {
            DiskLruCache diskLruCache =
                DiskLruCache.open(crtDiskCacheFolder, mDualCache.getAppVersion(), 1, maxDiskSize);
            mDualCache.setDiskLruCache(diskLruCache);
            mDualCache.setDiskCacheFolder(crtDiskCacheFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mDualCache;
    }

    /**
     * Use this if you do not want use the disk cache layer, meaning that only the ram cache layer will be used.
     * @return the "ready to use" dualcache.
     */
    public DualCache<T> noDisk() {
        if (mDualCache.getRAMMode().equals(DualCache.DualCacheRAMMode.DISABLE)) {
            throw new IllegalStateException("The ram cache layer and the disk cache layer are disable. You have to use at least one of those layers.");
        }

        mDualCache.setDiskMode(DualCache.DualCacheDiskMode.DISABLE);
        return mDualCache;
    }
}
