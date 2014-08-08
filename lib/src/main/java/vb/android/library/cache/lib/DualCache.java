package vb.android.library.cache.lib;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by Vincent Brison.
 */
public class DualCache<T> {

    /**
     * Define the general behaviour of the cache.
     */
    public enum DualCacheMode {
        /**
        * ONLY_RAM means only RAM must be used (no disk used).
         */
        ONLY_RAM,

        /**
         * BOTH_RAM_AND_DISK is the default behaviour where both RAM and dik are used for cache.
         */
        BOTH_RAM_AND_DISK
    }

    /**
     * Defined the sub folder from {@link android.content.Context#getCacheDir()} used to store all
     * the data generated from the use of this class.
     */
    private static String CACHE_FILE_PREFIX = "dualcache";

    /**
     * Unique ID which define a cache.
     */
    private String mId;

    /**
     * RAM cache.
     */
    private StringCacheLru mRamCacheLru;

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
     * Define the app version of the application (allow you to automatically invalidate data from different app version on disk).
     */
    private int mAppVersion;

    /**
     * The default behaviour use both RAM and disk fro cache.
     */
    private DualCacheMode mMode = DualCacheMode.BOTH_RAM_AND_DISK;

    /**
     * Gson serializer used to save data and load data. Can be used by multiple threads.
     */
    private static ObjectMapper sMapper = new ObjectMapper();

    /**
     * Construct a DualCache object. The #DualCacheMode is set to BOTH_RAM_AND_DISK by default.
     * @param id is the unique id of this cache.
     * @param appVersion is the app version of the application.
     * @param ramCacheSizeInBytes is the max size of the ram to use.
     * @param diskCacheSizeInBytes is the max size of disk to use.
     * @param clazz is the class of object to cache.
     */
    public DualCache(String id, int appVersion, int ramCacheSizeInBytes, int diskCacheSizeInBytes, Class<T> clazz) {
        sMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        sMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        sMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mRamCacheLru = new StringCacheLru(ramCacheSizeInBytes);
        mId = id;
        mClazz = clazz;
        mAppVersion = appVersion;
        mDiskCacheSizeInBytes = diskCacheSizeInBytes;
        File folder = new File(VBLibCacheContextUtils.getContext().getCacheDir().getPath() + "/" + CACHE_FILE_PREFIX + "/" + mId);
        try {
            mDiskLruCache = DiskLruCache.open(folder, appVersion, 1, diskCacheSizeInBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Put an object in cache.
     * @param key is the key of the object.
     * @param object is the object to put in cache.
     */
    public void put(String key, T object) {
        VBLibCacheLogUtils.logInfo("Object " + key + " is saved in cache.");
        String stringObject = null;

        try {
            stringObject = sMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        mRamCacheLru.put(key, stringObject);

        if (mMode == DualCacheMode.BOTH_RAM_AND_DISK) {
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(getDiskFileNameFromKey(key));
                editor.set(0, stringObject);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return the object of the corresponding key from the cache. In no object is available, return null.
     * @param key is the key of the object.
     * @return the object of the corresponding key from the cache. In no object is available, return null.
     */
    public T get(String key) {

        String stringObject;
        DiskLruCache.Snapshot snapshotObject = null;

        // Try to get the object from RAM.
        stringObject = mRamCacheLru.get(key);

        if (stringObject == null) {
            if (mMode == DualCacheMode.BOTH_RAM_AND_DISK) {
                // Try to get the cached object from disk.
                VBLibCacheLogUtils
                        .logInfo("Object " + key + " is not in the RAM. Try to get it from disk.");
                try {
                    snapshotObject = mDiskLruCache.get(getDiskFileNameFromKey(key));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (snapshotObject != null) {
                    VBLibCacheLogUtils.logInfo("Object " + key + " is on disk.");

                    // Refresh object in ram.
                    try {
                        String snapshotObjectAsString = snapshotObject.getString(0);
                        mRamCacheLru.put(key, snapshotObjectAsString);
                        return sMapper.readValue(snapshotObjectAsString, mClazz);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else
                    VBLibCacheLogUtils.logInfo("Object " + key + " is not on disk.");
            }
        } else {
            VBLibCacheLogUtils.logInfo("Object " + key + " is in the RAM.");
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
     * @param key is the key of the object.
     */
    public void delete(String key) {
        mRamCacheLru.remove(key);

        if (mMode == DualCacheMode.BOTH_RAM_AND_DISK) {
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
        if (mMode == DualCacheMode.BOTH_RAM_AND_DISK) {
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
            File folder = new File(VBLibCacheContextUtils.getContext().getCacheDir().getPath() + "/" + CACHE_FILE_PREFIX + "/" + mId);
            mDiskLruCache = DiskLruCache.open(folder, mAppVersion, 1, mDiskCacheSizeInBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return a hashed name for the file to use for disk cache from a key.
     * @param key is the key to hash.
     * @return a hashed name for the file to use for disk cache from a key.
     */
    private String getDiskFileNameFromKey(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return CACHE_FILE_PREFIX + "-" + mId + "-" + sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return the mapper if you want to do more configuration on it.
     * @return the mapper if you want to do more configuration on it.
     */
    public static ObjectMapper getMapper() {
        return sMapper;
    }

    /**
     * Return the size used in bytes of the RAM cache.
     * @return the size used in bytes of the RAM cache.
     */
    public long getRamSize() {
        return mRamCacheLru.size();
    }

    /**
     * Return the size used in bytes of the disk cache.
     * @return the size used in bytes of the disk cache.
     */
    public long getDiskSize() {
        return mDiskLruCache.size();
    }

    /**
     * Return the DualCacheMode in used from this instance of cache.
     * @return the DualCacheMode in used from this instance of cache.
     */
    public DualCacheMode getMode() {
        return mMode;
    }

    /**
     * Set the DualCacheMode in used from this instance of cache.
     * mMode is the mode to use in this instance of cache.
     */
    public void setMode(DualCacheMode mMode) {
        this.mMode = mMode;
    }
}
