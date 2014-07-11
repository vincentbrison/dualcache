package vb.android.library.cache.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by Vincent Brison.
 */
public class SimpleCache<T> {

    private static String CACHE_FILE_PREFIX = "simplecache";

    private StringCacheLru mRamCacheLru;
    private DiskLruCache mDiskLruCache;

    private String mId;

    private static ObjectMapper mapper = new ObjectMapper();

    public SimpleCache(String id, int appVersion, int ramCacheSizeInBytes, int diskCacheSizeInBytes) {
        mRamCacheLru = new StringCacheLru(ramCacheSizeInBytes);
        mId = id;
        File folder = new File(VBLibCacheContextUtils.getContext().getCacheDir().getPath() + "/" + CACHE_FILE_PREFIX + "/" + mId);
        try {
            mDiskLruCache = DiskLruCache.open(folder, appVersion, 1, diskCacheSizeInBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void put(String key, T object) {
        VBLibCacheLogUtils.logInfo("Object " + key + " is saved in cache.");
        try {
            mRamCacheLru.put(key, mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        putToDisk(key, object);
    }

    /*
    public T get(String key) {

        T cachedObject;

        // Try to get the object from RAM.
        cachedObject = mRamCacheLru.get(key);

        if (cachedObject == null) {
            // Try to get the cached object from disk.
            VBLibCacheLogUtils
                    .logInfo("Object " + key + " is not in the RAM. Try to get it from disk.");
            cachedObject = getFromDisk(key);
            if (cachedObject != null) {
                VBLibCacheLogUtils.logInfo("Object " + key + " is on disk.");
            } else
                VBLibCacheLogUtils.logInfo("Object " + key + " is not on disk.");

        } else {
            VBLibCacheLogUtils.logInfo("Object " + key + " is in the RAM.");
        }
        if (cachedObject != null) {
            // Refresh object in RAM.
            mRamCacheLru.put(key, cachedObject);

            return cachedObject;
        } else {
            VBLibCacheLogUtils.logInfo("Object " + key + " is not cached.");
        }

        // No data are available.
        return null;
    }

    private void putToDisk(String key, T wrapper) {

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

    private void deleteFromDisk(String key) {
        String diskKey = getDiskFileNameFromKey(key);
        VBLibCacheContextUtils.getContext().deleteFile(diskKey);
    }

    public void invalidate() {
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

    public static void invalidateAllCache() {
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

    private T getFromDiskWithFullName(String name) {
        FileInputStream fis;
        ObjectInputStream in;
        try {
            File cacheFile = VBLibCacheContextUtils.getContext().getFileStreamPath(name);
            if (cacheFile.exists()) {

                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private T getFromDisk(String key) {
        String diskKey = getDiskFileNameFromKey(key);
        return getFromDiskWithFullName(diskKey);
    }

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
    */
}
