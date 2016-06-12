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

package com.vincentbrison.openlibraries.android.dualcache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class intent to provide a very easy to use, reliable, highly configurable caching library
 * for Android.
 *
 * @param <T> is the Class of object to cache.
 */
public class DualCache<T> {

    /**
     * Define the behaviour of the RAM layer.
     */
    public enum DualCacheRamMode {
        /**
         * Means that object will be serialized with a specific serializer in RAM.
         */
        ENABLE_WITH_SPECIFIC_SERIALIZER,

        /**
         * Means that only references to objects will be stored in the RAM layer.
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
         * Means that object will be serialized with a specific serializer in disk.
         */
        ENABLE_WITH_SPECIFIC_SERIALIZER,

        /**
         * The disk layer is not used.
         */
        DISABLE
    }

    /**
     * Defined the sub folder from {@link android.content.Context#getCacheDir()} used to store all
     * the data generated from the use of this library.
     */
    protected static final String CACHE_FILE_PREFIX = "dualcache";

    private static final String LOG_PREFIX = "Entry for ";

    /**
     * Unique ID which define a cache.
     */
    private String mId;

    /**
     * RAM cache.
     */
    private RamLruCache mRamCacheLru;

    /**
     * Disk cache.
     */
    private DiskLruCache mDiskLruCache;

    /**
     * Define the class store in this cache.
     */
    private Class<T> mClazz;

    /**
     * Hold the max size in bytes of the disk cache.
     */
    private int mDiskCacheSizeInBytes;

    /**
     * Define the folder in which the disk cache will save its files.
     */
    private File mDiskCacheFolder;

    /**
     * Define the app version of the application (allow you to automatically invalidate data
     * from different app version on disk).
     */
    private int mAppVersion;

    /**
     * By default the RAM layer use JSON serialization to store cached object.
     */
    private DualCacheRamMode mRamMode;

    /**
     * By default the disk layer use JSON serialization to store cached object.
     */
    private DualCacheDiskMode mDiskMode;

    /**
     * The handler used when the ram cache is enable with
     * {@link DualCache
     * .DualCacheRAMMode#ENABLE_WITH_REFERENCE}
     */
    private SizeOf<T> mHandlerSizeOf;

    private CacheSerializer<T> mDiskSerializer;

    private CacheSerializer<T> mRamSerializer;

    private final ConcurrentMap<String, Lock> mEditionLocks = new ConcurrentHashMap<>();
    private ReadWriteLock mInvalidationReadWriteLock = new ReentrantReadWriteLock();
    private final Logger logger;

    /**
     * Constructor which only set global parameter of the cache.
     *
     * @param id         is the id of the cache.
     * @param appVersion is the app version of the app. (Data in disk cache will be invalidate if
     *                   their app version is inferior than this app version.
     * @param clazz      is the Class of object to store in cache.
     * @param logger
     */
    DualCache(String id, int appVersion, Class clazz, Logger logger) {
        mId = id;
        mAppVersion = appVersion;
        mClazz = clazz;
        this.logger = logger;
    }

    protected int getAppVersion() {
        return mAppVersion;
    }

    protected String getCacheId() {
        return mId;
    }

    protected void setDiskLruCache(DiskLruCache diskLruCache) {
        mDiskLruCache = diskLruCache;
    }

    protected void setRAMSerializer(CacheSerializer<T> ramSerializer) {
        mRamSerializer = ramSerializer;
    }

    protected void setDiskSerializer(CacheSerializer<T> diskSerializer) {
        mDiskSerializer = diskSerializer;
    }

    protected void setRamCacheLru(RamLruCache ramLruCache) {
        mRamCacheLru = ramLruCache;
    }

    /**
     * Return the size used in bytes of the RAM cache.
     *
     * @return the size used in bytes of the RAM cache.
     */
    public long getRamSize() {
        if (mRamCacheLru == null) {
            return -1;
        } else {
            return mRamCacheLru.size();
        }
    }

    /**
     * Return the size used in bytes of the disk cache.
     *
     * @return the size used in bytes of the disk cache.
     */
    public long getDiskSize() {
        if (mDiskLruCache == null) {
            return -1;
        } else {
            return mDiskLruCache.size();
        }

    }

    /**
     * Return the way objects are cached in RAM layer.
     *
     * @return the way objects are cached in RAM layer.
     */
    public DualCacheRamMode getRAMMode() {
        return mRamMode;
    }

    /**
     * Set the way objects are stored in the RAM layer.
     *
     * @param ramMode is the value to set.
     */
    protected void setRamMode(DualCacheRamMode ramMode) {
        this.mRamMode = ramMode;
    }

    protected void setDiskCacheSizeInBytes(int diskCacheSizeInBytes) {
        mDiskCacheSizeInBytes = diskCacheSizeInBytes;
    }

    public File getDiskCacheFolder() {
        return mDiskCacheFolder;
    }

    public void setDiskCacheFolder(File folder) {
        mDiskCacheFolder = folder;
    }

    /**
     * Return the way objects are cached in disk layer.
     *
     * @return the way objects are cached in disk layer.
     */
    public DualCacheDiskMode getDiskMode() {
        return mDiskMode;
    }

    /**
     * Put an object in cache.
     *
     * @param key    is the key of the object.
     * @param object is the object to put in cache.
     */
    public void put(String key, T object) {
        // Synchronize put on each entry. Gives concurrent editions on different entries, and atomic
        // modification on the same entry.
        if (mRamMode.equals(DualCacheRamMode.ENABLE_WITH_REFERENCE)) {
            mRamCacheLru.put(key, object);
        }

        if (mRamMode.equals(DualCacheRamMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
            mRamCacheLru.put(key, mRamSerializer.toBytes(object));
        }

        if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
            try {
                mInvalidationReadWriteLock.readLock().lock();
                getLockForGivenEntry(key).lock();
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                editor.set(0, new String(mDiskSerializer.toBytes(object)));
                editor.commit();
            } catch (IOException e) {
                logger.logError(e);
            } finally {
                getLockForGivenEntry(key).unlock();
                mInvalidationReadWriteLock.readLock().unlock();
            }
        }
    }

    /**
     * Return the object of the corresponding key from the cache. In no object is available,
     * return null.
     *
     * @param key is the key of the object.
     * @return the object of the corresponding key from the cache. In no object is available,
     * return null.
     */
    public T get(String key) {

        Object ramResult = null;
        byte[] diskResult = null;
        DiskLruCache.Snapshot snapshotObject = null;

        // Try to get the object from RAM.
        boolean isRamSerialized = mRamMode.equals(DualCacheRamMode.ENABLE_WITH_SPECIFIC_SERIALIZER);
        boolean isRamReferenced = mRamMode.equals(DualCacheRamMode.ENABLE_WITH_REFERENCE);
        if (isRamSerialized || isRamReferenced) {
            ramResult = mRamCacheLru.get(key);
        }

        if (ramResult == null) {
            // Try to get the cached object from disk.
            logEntryForKeyIsNotInRam(key);
            if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
                try {
                    mInvalidationReadWriteLock.readLock().lock();
                    getLockForGivenEntry(key).lock();
                    snapshotObject = mDiskLruCache.get(key);
                } catch (IOException e) {
                    logger.logError(e);
                } finally {
                    getLockForGivenEntry(key).unlock();
                    mInvalidationReadWriteLock.readLock().unlock();
                }

                if (snapshotObject != null) {
                    logEntryForKeyIsOnDisk(key);
                    try {
                        diskResult = snapshotObject.getString(0).getBytes();
                    } catch (IOException e) {
                        logger.logError(e);
                    }
                } else {
                    logEntryForKeyIsNotOnDisk(key);
                }
            }

            T objectFromStringDisk = null;

            if (diskResult != null) {
                // Refresh object in ram.
                if (mRamMode.equals(DualCacheRamMode.ENABLE_WITH_REFERENCE)) {
                    if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
                        // Need to get an instance from string.
                        if (objectFromStringDisk == null) {
                            objectFromStringDisk = mDiskSerializer.fromBytes(diskResult);
                        }
                        mRamCacheLru.put(key, objectFromStringDisk);
                    }
                } else if (mRamMode.equals(DualCacheRamMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
                    if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
                        // Need to get an instance from string.
                        if (objectFromStringDisk == null) {
                            objectFromStringDisk = mDiskSerializer.fromBytes(diskResult);
                        }
                        mRamCacheLru.put(key, mRamSerializer.toBytes(objectFromStringDisk));
                    }
                }
                if (mDiskMode.equals(DualCacheDiskMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
                    if (objectFromStringDisk == null) {
                        objectFromStringDisk = mDiskSerializer.fromBytes(diskResult);
                    }
                    return objectFromStringDisk;
                }
            }
        } else {
            logEntryForKeyIsInRam(key);
            if (mRamMode.equals(DualCacheRamMode.ENABLE_WITH_REFERENCE)) {
                return (T) ramResult;
            } else if (mRamMode.equals(DualCacheRamMode.ENABLE_WITH_SPECIFIC_SERIALIZER)) {
                return mRamSerializer.fromBytes((byte[]) ramResult);
            }
        }

        // No data is available.
        return null;
    }

    // Let concurrent modification on different keys.
    private Lock getLockForGivenEntry(String key) {
        if (!mEditionLocks.containsKey(key)) {
            mEditionLocks.putIfAbsent(key, new ReentrantLock());
        }
        return mEditionLocks.get(key);
    }

    /**
     * Delete the corresponding object in cache.
     *
     * @param key is the key of the object.
     */
    public void delete(String key) {
        if (!mRamMode.equals(DualCacheRamMode.DISABLE)) {
            mRamCacheLru.remove(key);
        }
        if (!mDiskMode.equals(DualCacheDiskMode.DISABLE)) {
            try {
                mInvalidationReadWriteLock.readLock().lock();
                getLockForGivenEntry(key).lock();
                mDiskLruCache.remove(key);
            } catch (IOException e) {
                logger.logError(e);
            } finally {
                getLockForGivenEntry(key).unlock();
                mInvalidationReadWriteLock.readLock().unlock();
            }
        }
    }

    /**
     * Remove all objects from cache (both RAM and disk).
     */
    public void invalidate() {
        invalidateDisk();
        invalidateRAM();
    }

    /**
     * Remove all objects from RAM.
     */
    public void invalidateRAM() {
        if (!mRamMode.equals(DualCacheRamMode.DISABLE)) {
            mRamCacheLru.evictAll();
        }
    }

    /**
     * Remove all objects from Disk.
     */
    public void invalidateDisk() {
        if (!mDiskMode.equals(DualCacheDiskMode.DISABLE)) {
            try {
                mInvalidationReadWriteLock.writeLock().lock();
                mDiskLruCache.delete();
                mDiskLruCache =
                    DiskLruCache.open(mDiskCacheFolder, mAppVersion, 1, mDiskCacheSizeInBytes);
            } catch (IOException e) {
                logger.logError(e);
            } finally {
                mInvalidationReadWriteLock.writeLock().unlock();
            }
        }
    }

    /**
     * Set the way objects are stored in the disk layer.
     *
     * @param diskMode is the value to set.
     */
    public void setDiskMode(DualCacheDiskMode diskMode) {
        this.mDiskMode = diskMode;
    }

    // Logging helpers
    private void logEntrySavedForKey(String key) {
        logger.logInfo(LOG_PREFIX + key + " is saved in cache.");
    }

    private void logEntryForKeyIsInRam(String key) {
        logger.logInfo(LOG_PREFIX + key + " is in RAM.");
    }

    private void logEntryForKeyIsNotInRam(String key) {
        logger.logInfo(LOG_PREFIX + key + " is not in RAM.");
    }

    private void logEntryForKeyIsOnDisk(String key) {
        logger.logInfo(LOG_PREFIX + key + " is on disk.");
    }

    private void logEntryForKeyIsNotOnDisk(String key) {
        logger.logInfo(LOG_PREFIX + key + " is not on disk.");
    }
}
