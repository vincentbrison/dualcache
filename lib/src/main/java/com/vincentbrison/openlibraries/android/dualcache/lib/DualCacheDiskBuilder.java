package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

public class DualCacheDiskBuilder<T> {

    DualCache<T> mDualCache;

    protected DualCacheDiskBuilder (DualCache<T> dualCache) {
        mDualCache = dualCache;
    }

    public DualCache<T> useSerializerInDisk(int maxDiskSize, boolean usePrivateFiles, Serializer serializer) {
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
        return mDualCache;
    }

    public DualCache<T> useJsonInDisk(int maxDiskSize, boolean usePrivateFiles) {
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
        return mDualCache;
    }

    public DualCache<T> noDisk() {
        mDualCache.setDiskMode(DualCache.DualCacheDiskMode.DISABLE);
        return mDualCache;
    }
}
