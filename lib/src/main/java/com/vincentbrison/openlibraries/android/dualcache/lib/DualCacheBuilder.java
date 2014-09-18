package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class DualCacheBuilder<T> {

    private DualCache<T> mDualCache;

    public DualCacheBuilder(String id, int appVersion, Class<T> clazz) {
        mDualCache = new DualCache(id, appVersion, clazz);
    }

    public DualCacheBuilder<T> useJsonInRam(int maxRamSize) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_DEFAULT_SERIALIZER);
        mDualCache.mRamCacheLru = new StringLRUCache(maxRamSize);
        return this;
    }

    public DualCacheBuilder<T> useReferenceInRam(int maxRamSize, SizeOf<T> handlerSizeOf) {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.ENABLE_WITH_REFERENCE);
        mDualCache.mRamCacheLru = new ReferenceLRUCache(maxRamSize, handlerSizeOf);
        return this;
    }

    public DualCacheBuilder<T> useSerializerInDisk(int maxDiskSize, boolean usePrivateFiles, Serializer serializer) {
        mDualCache.setDiskMode(DualCache.DualCacheDiskMode.ENABLE_WITH_CUSTOM_SERIALIZER);
        mDualCache.mSerializer = serializer;
        File folder = null;
        if (usePrivateFiles) {
            folder = DualCacheContextUtils.getContext().getDir(DualCache.CACHE_FILE_PREFIX + mDualCache.mId, Context.MODE_PRIVATE);
        } else {
            folder = new File(DualCacheContextUtils.getContext().getCacheDir().getPath() + "/" + DualCache.CACHE_FILE_PREFIX + "/" + mDualCache.mId);
        }
        try {
            mDualCache.mDiskLruCache = DiskLruCache.open(folder, mDualCache.mAppVersion, 1, maxDiskSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public DualCacheBuilder<T> useJsonInDisk(int maxDiskSize, boolean usePrivateFiles) {
        mDualCache.setDiskMode(DualCache.DualCacheDiskMode.ENABLE_WITH_DEFAULT_SERIALIZER);
        File folder = null;
        if (usePrivateFiles) {
            folder = DualCacheContextUtils.getContext().getDir(DualCache.CACHE_FILE_PREFIX + mDualCache.mId, Context.MODE_PRIVATE);
        } else {
            folder = new File(DualCacheContextUtils.getContext().getCacheDir().getPath() + "/" + DualCache.CACHE_FILE_PREFIX + "/" + mDualCache.mId);
        }
        try {
            mDualCache.mDiskLruCache = DiskLruCache.open(folder, mDualCache.mAppVersion, 1, maxDiskSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public DualCacheBuilder<T> noDisk() {
        mDualCache.setDiskMode(DualCache.DualCacheDiskMode.DISABLE);
        return this;
    }

    public DualCacheBuilder<T> noRam() {
        mDualCache.setRAMMode(DualCache.DualCacheRAMMode.DISABLE);
        return this;
    }

    public DualCache<T> build() {
        DualCache dualCacheToBuild = mDualCache;
        mDualCache = null;
        return dualCacheToBuild;
    }
}
