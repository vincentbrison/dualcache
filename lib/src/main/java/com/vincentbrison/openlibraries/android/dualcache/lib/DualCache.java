/*
 * Copyright 2014 Vincent Brison.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.content.Context;
import android.support.v4.util.LruCache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by Vincent Brison.
 * This class intent to provide a very easy to use, reliable, highly configurable, performance driven
 * cache library for Android.
 */
public class DualCache<T> {

    /**
     * Define the behaviour of the RAM layer.
     */
    public enum DualCacheRAMMode {

        /**
         * Means that object will be converted in JSON and stored as is in RAM. Object are duplicated
         * using json serializer.
         */
        ENABLE_WITH_JSON,

        /**
         *
         */
        ENABLE_WITH_REFERENCE,

        /**
         * The RAM layer is not used.
         */
        DISABLE
    }

    /**
     * Define the behaviour of the disk layer.
     */
    public enum DualCacheDiskMode {

        /**
         * Object are stored on disk using json serialization.
         */
        ENABLE_WITH_JSON,

        /**
         * The disk layer is not used.
         */
        DISABLE
    }

    /**
     * Is true if you want use {@link android.content.Context#MODE_PRIVATE} as policy for the disk files used by this cache. Otherwise {@link android.content.Context#getCacheDir()} are used.
     */
    private boolean mUsePrivateFiles = false;

    /**
     * Defined the sub folder from {@link android.content.Context#getCacheDir()} used to store all
     * the data generated from the use of this class.
     */
    protected static String CACHE_FILE_PREFIX = "dualcache";

    /**
     * Unique ID which define a cache.
     */
    protected String mId;

    /**
     * RAM cache.
     */
    protected LruCache mRamCacheLru;

    /**
     * Disk cache.
     */
    protected DiskLruCache mDiskLruCache;

    /**
     * Define the class store in this cache.
     */
    private Class<T> mClazz;

    /**
     * Hold the max size in bytes of the disk cache.
     */
    private int mDiskCacheSizeInBytes;

    /**
     * Define the app version of the application (allow you to automatically invalidate data from different app version on disk).
     */
    protected int mAppVersion;

    /**
     * By default the RAM layer use JSON serialization to store cached object.
     */
    private DualCacheRAMMode mRAMMode = DualCacheRAMMode.ENABLE_WITH_JSON;

    /**
     * By default the disk layer use JSON serialization to store cached object.
     */
    private DualCacheDiskMode mDiskMode = DualCacheDiskMode.ENABLE_WITH_JSON;

    /**
     * Gson serializer used to save data and load data. Can be used by multiple threads.
     */
    private static ObjectMapper sMapper;

    private SizeOf<T> handlerSizeOf;

    static {
        sMapper = new ObjectMapper();
        sMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        sMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        sMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    protected DualCache(String id, int appVersion, Class<T> clazz) {
        // Set params
        mId = id;
        mAppVersion = appVersion;
        mClazz = clazz;

    }


    /**
     * Construct a DualCache object. The #DualCacheMode is set to BOTH_RAM_AND_DISK by default.
     *
     * @param id                   is the unique id of this cache.
     * @param appVersion           is the app version of the application.
     * @param ramCacheSizeInBytes  is the max size of the ram to use.
     * @param diskCacheSizeInBytes is the max size of disk to use, 0 for unlimited size.
     * @param usePrivateFiles      is true if you want use {@link android.content.Context#MODE_PRIVATE} as policy for the disk files used by this cache. Otherwise {@link android.content.Context#getCacheDir()} are used.
     * @param clazz                is the class of object to cache.
     */
    /*public DualCache(String id, int appVersion, int ramCacheSizeInBytes, int diskCacheSizeInBytes, boolean usePrivateFiles, Class<T> clazz) {

        // Set params
        mId = id;
        mAppVersion = appVersion;
        mClazz = clazz;
        mDiskCacheSizeInBytes = diskCacheSizeInBytes;
        mUsePrivateFiles = usePrivateFiles;

        // Build caches
        mRamCacheLru = new StringLRUCache(ramCacheSizeInBytes);

        File folder = null;
        if (mUsePrivateFiles) {
            folder = DualCacheContextUtils.getContext().getDir(CACHE_FILE_PREFIX + mId, Context.MODE_PRIVATE);
        } else {
            folder = new File(DualCacheContextUtils.getContext().getCacheDir().getPath() + "/" + CACHE_FILE_PREFIX + "/" + mId);
        }
        try {
            mDiskLruCache = DiskLruCache.open(folder, appVersion, 1, diskCacheSizeInBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    */

    public void setCustomSizeOfHandler(SizeOf handler) {

    }

    /**
     * Put an object in cache.
     *
     * @param key    is the key of the object.
     * @param object is the object to put in cache.
     */
    public void put(String key, T object) {
        DualCacheLogUtils.logInfo("Object " + key + " is saved in cache.");
        String stringObject = null;

        try {
            stringObject = sMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (mRAMMode.equals(DualCacheRAMMode.ENABLE_WITH_JSON)) {
            mRamCacheLru.put(key, stringObject);
        }

        if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_JSON)) {
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                editor.set(0, stringObject);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return the object of the corresponding key from the cache. In no object is available, return null.
     *
     * @param key is the key of the object.
     * @return the object of the corresponding key from the cache. In no object is available, return null.
     */
    public T get(String key) {

        String stringObject = null;
        DiskLruCache.Snapshot snapshotObject = null;

        // Try to get the object from RAM.
        if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_JSON)) {
            stringObject = (String) mRamCacheLru.get(key);
        }

        if (stringObject == null) {
            if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_JSON)) {
                // Try to get the cached object from disk.
                DualCacheLogUtils
                        .logInfo("Object " + key + " is not in the RAM. Try to get it from disk.");
                try {
                    snapshotObject = mDiskLruCache.get(key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (snapshotObject != null) {
                    DualCacheLogUtils.logInfo("Object " + key + " is on disk.");

                    // Refresh object in ram.
                    try {
                        String snapshotObjectAsString = snapshotObject.getString(0);
                        mRamCacheLru.put(key, snapshotObjectAsString);
                        return sMapper.readValue(snapshotObjectAsString, mClazz);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else
                    DualCacheLogUtils.logInfo("Object " + key + " is not on disk.");
            }
        } else {
            DualCacheLogUtils.logInfo("Object " + key + " is in the RAM.");
            try {
                return sMapper.readValue(stringObject, mClazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // No data are available.
        return null;
    }

    /**
     * Delete the corresponding object in cache.
     *
     * @param key is the key of the object.
     */
    public void delete(String key) {

        if (mRAMMode.equals(DualCacheRAMMode.ENABLE_WITH_JSON)) {
            mRamCacheLru.remove(key);
        }

        if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_JSON)) {
            try {
                mDiskLruCache.remove(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove all objects from cache (both RAM and disk).
     */
    public void invalidate() {
        if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_JSON)) {
            invalidateDisk();
        }
        invalidateRAM();
    }

    /**
     * Remove all objects from RAM.
     */
    public void invalidateRAM() {
        mRamCacheLru.evictAll();
    }

    /**
     * Remove all objects from Disk.
     */
    public void invalidateDisk() {
        try {
            mDiskLruCache.delete();
            File folder = new File(DualCacheContextUtils.getContext().getCacheDir().getPath() + "/" + CACHE_FILE_PREFIX + "/" + mId);
            mDiskLruCache = DiskLruCache.open(folder, mAppVersion, 1, mDiskCacheSizeInBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the mapper if you want to do more configuration on it.
     *
     * @return the mapper if you want to do more configuration on it.
     */
    public static ObjectMapper getMapper() {
        return sMapper;
    }

    /**
     * Return the size used in bytes of the RAM cache.
     *
     * @return the size used in bytes of the RAM cache.
     */
    public long getRamSize() {
        return mRamCacheLru.size();
    }

    /**
     * Return the size used in bytes of the disk cache.
     *
     * @return the size used in bytes of the disk cache.
     */
    public long getDiskSize() {
        return mDiskLruCache.size();
    }

    public DualCacheRAMMode getRAMMode() {
        return mRAMMode;
    }

    public void setRAMMode(DualCacheRAMMode RAMMode) {
        this.mRAMMode = RAMMode;
    }

    public DualCacheDiskMode getDiskMode() {
        return mDiskMode;
    }

    public void setDiskMode(DualCacheDiskMode diskMode) {
        this.mDiskMode = diskMode;
    }
}
