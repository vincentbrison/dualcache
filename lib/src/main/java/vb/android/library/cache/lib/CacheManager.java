package vb.android.library.cache.lib;

import android.content.Context;
import android.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vincent Brison.
 */
public class CacheManager {

    private int mRamCacheSize = 4194304;

    private static String CACHE_FILE_PREFIX = "vb.cache";

    private String mId;

    static private Map<String, WeakReference<CacheManager>> mCacheManagerMap;

    private LruCache<String, Serializable> mRamCacheLru;


    private CacheManager(String id) {
        mId = id;
        // 4MiB of cache.
        mRamCacheLru = new LruCache<String, Serializable>(mRamCacheSize);
    }

    /**
     * Return a cache manager identified by the id. Cache manager with different id do not share
     * any
     * data.
     * Cache manager can be used over the lifetime of the application since the data are cache in
     * RAM
     * (a buffer of 4MiB is used with the LRU policy) and on the disk (with unlimited size, in
     * private mode).
     *
     * @param id is the unique id of the cache.
     * @return the cache manager according the unique id.
     */
    public static CacheManager getCacheManager(String id) {
        if (mCacheManagerMap == null) {
            mCacheManagerMap = new HashMap<String, WeakReference<CacheManager>>();
        }

        CacheManager cacheManager;

        if (mCacheManagerMap.containsKey(id)) {
            cacheManager = mCacheManagerMap.get(id).get();
            if (cacheManager == null) {
                cacheManager = new CacheManager(id);
                mCacheManagerMap.put(id, new WeakReference<CacheManager>(cacheManager));
            }
        } else {
            cacheManager = new CacheManager(id);
            mCacheManagerMap.put(id, new WeakReference<CacheManager>(cacheManager));
        }
        return cacheManager;
    }

    /**
     * Cache an object put in the wrapper. If the expiry date is set to null, the object will
     * always persist in the cache. If the expiry date is not null, the object will be cache until
     * the expiry date is outdated.
     *
     * @param key     is an unique identifier to cache the object.
     * @param wrapper contains the object to cache and the expiry date.
     */
    public void put(String key, CacheWrapper wrapper) {
        if (wrapper.mExpiryDate == null || wrapper.mExpiryDate.after(new Date())) {
            VBLibCacheLogUtils.logInfo("Object " + key + " is saved in cache.");
            mRamCacheLru.put(key, wrapper);
            putToDisk(key, wrapper);

        } else {
            VBLibCacheLogUtils.logInfo(
                    "Object " + key + " is not saved in cache because of the expiry date.");
        }
    }

    /**
     * Get an object from the cache.
     *
     * @param key  is the unique identifier of the cached object.
     * @param type is the class of the object.
     * @param <T>  is the class of the object.
     * @return the object from the cache, or null if the object is not (or no more) in the cache.
     */
    public <T> T get(String key, Class<T> type) {
        CacheWrapper wrapper;

        // Try to get the object from RAM.
        wrapper = (CacheWrapper) mRamCacheLru.get(key);

        if (wrapper == null) {
            // Try to get the cached object from disk.
            VBLibCacheLogUtils
                    .logInfo("Object " + key + " is not in the RAM. Try to get it from disk.");
            wrapper = getFromDisk(key);
            if (wrapper != null) {
                VBLibCacheLogUtils.logInfo("Object " + key + " is on disk.");
            } else
                VBLibCacheLogUtils.logInfo("Object " + key + " is not on disk.");

        } else {
            VBLibCacheLogUtils.logInfo("Object " + key + " is in the RAM.");
        }
        if (wrapper != null) {

            if (wrapper.mExpiryDate == null || wrapper.mExpiryDate.after(new Date())) {
                if (wrapper.mExpiryDate != null) {
                    VBLibCacheLogUtils
                            .logInfo("Object " + key + " is valid until : " + wrapper.mExpiryDate);
                } else {
                    VBLibCacheLogUtils
                            .logInfo("Object " + key + " is valid until the end of times.");
                }
                // Refresh object in RAM.
                mRamCacheLru.put(key, wrapper);

                return type.cast(wrapper.mObject);
            } else {
                VBLibCacheLogUtils.logInfo("Object " + key + " is no more valid.");
                deleteFromDisk(key);
                mRamCacheLru.remove(key);
            }

        } else {
            VBLibCacheLogUtils.logInfo("Object " + key + " is not cached.");
        }

        // No data are available.
        return null;
    }

    /**
     * Save object to disk.
     *
     * @param key     is the key to identify the object to cache.
     * @param wrapper is the object to save.
     */
    private void putToDisk(String key, CacheWrapper wrapper) {

        FileOutputStream fos;
        ObjectOutputStream out;

        try {
            String diskKey = getDiskFileNameFromKey(key);
            VBLibCacheContextUtils.getContext().deleteFile(diskKey);
            fos = VBLibCacheContextUtils.getContext().openFileOutput(diskKey,
                    Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(wrapper);
            out.close();
            fos.close();
            VBLibCacheLogUtils.logInfo("Object " + key + " is saved on disk.");
        } catch (Exception e) {
            VBLibCacheLogUtils
                    .logInfo("Object " + key + " is not saved on disk. See stack trace.");
            e.printStackTrace();
        }
    }

    /**
     * Remove the cached object with this key from disk.
     *
     * @param key is the unique identifier of the object.
     */
    private void deleteFromDisk(String key) {
        String diskKey = getDiskFileNameFromKey(key);
        VBLibCacheContextUtils.getContext().deleteFile(diskKey);
    }

    /**
     * Delete all cached object from this cache.
     */
    public void deleteCache() {
        String[] files = VBLibCacheContextUtils.getContext().fileList();

        VBLibCacheLogUtils.logInfo("Delete cache " + mId);

        // Delete cached objects from disk.
        for (String file : files) {
            if (file.startsWith(CACHE_FILE_PREFIX + "-" + mId)) {
                VBLibCacheContextUtils.getContext().deleteFile(file);
            }
        }

        // Delete cached object from RAM.
        mRamCacheLru.evictAll();
    }

    /**
     * Delete all the caches, without considering the ids.
     */
    public static void deleteAllCache() {
        VBLibCacheLogUtils.logInfo("Delete all cache.");
        String[] files = VBLibCacheContextUtils.getContext().fileList();

        // Delete cached objects from disk.
        for (String file : files) {
            if (file.startsWith(CACHE_FILE_PREFIX)) {
                VBLibCacheContextUtils.getContext().deleteFile(file);
            }
        }

        // Delete cached object from RAM.
        for (WeakReference<CacheManager> cacheRef : mCacheManagerMap.values()) {
            CacheManager cache = cacheRef.get();
            if (cache != null) {
                cache.mRamCacheLru.evictAll();
            }
        }
    }

    private CacheWrapper getFromDiskWithFullName(String name) {
        FileInputStream fis;
        ObjectInputStream in;
        CacheWrapper wrapper;
        try {
            File cacheFile = VBLibCacheContextUtils.getContext().getFileStreamPath(name);
            if (cacheFile.exists()) {
                fis = VBLibCacheContextUtils.getContext().openFileInput(name);
                in = new ObjectInputStream(fis);
                wrapper = (CacheWrapper) in.readObject();

                in.close();
                fis.close();

                return wrapper;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get object from disk.
     *
     * @param key is the key to use to retrieve the cached object.
     * @return the wrapper containing the cached object.
     */
    private CacheWrapper getFromDisk(String key) {
        String diskKey = getDiskFileNameFromKey(key);
        return getFromDiskWithFullName(diskKey);
    }

    /**
     * Generate the name of the file in which the object is cached.
     *
     * @param key is the key of the cached object.
     * @return the name of the file in which the object is cached.
     */
    private String getDiskFileNameFromKey(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(key.getBytes());
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            return CACHE_FILE_PREFIX + "-" + mId + "-" + sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
