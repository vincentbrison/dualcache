package vb.android.library.cache.lib;

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

    private static String CACHE_FILE_PREFIX = "dualcache";

    private StringCacheLru mRamCacheLru;
    private DiskLruCache mDiskLruCache;
    private Class<T> mClazz;

    private String mId;
    private int mDiskCacheSizeinBytes;
    private int mAppVersion;

    private static ObjectMapper mMapper = new ObjectMapper();

    public DualCache(String id, int appVersion, int ramCacheSizeInBytes, int diskCacheSizeInBytes, Class<T> clazz) {
        mRamCacheLru = new StringCacheLru(ramCacheSizeInBytes);
        mId = id;
        mClazz = clazz;
        mAppVersion = appVersion;
        mDiskCacheSizeinBytes = diskCacheSizeInBytes;
        File folder = new File(VBLibCacheContextUtils.getContext().getCacheDir().getPath() + "/" + CACHE_FILE_PREFIX + "/" + mId);
        try {
            mDiskLruCache = DiskLruCache.open(folder, appVersion, 1, diskCacheSizeInBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void put(String key, T object) {
        VBLibCacheLogUtils.logInfo("Object " + key + " is saved in cache.");
        String stringObject = null;

        try {
            stringObject = mMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        mRamCacheLru.put(key, stringObject);
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(getDiskFileNameFromKey(key));
            editor.set(0, stringObject);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public T get(String key) {

        String stringObject;
        DiskLruCache.Snapshot snapshotObject = null;

        // Try to get the object from RAM.
        stringObject = mRamCacheLru.get(key);

        if (stringObject == null) {
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
                    mRamCacheLru.put(key, snapshotObject.getString(0));

                    //JavaType type = mMapper.getTypeFactory().constructType(Type)
                    return mMapper.readValue(snapshotObject.getString(0), mClazz);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else
                VBLibCacheLogUtils.logInfo("Object " + key + " is not on disk.");

        } else {
            VBLibCacheLogUtils.logInfo("Object " + key + " is in the RAM.");
            try {
                return mMapper.readValue(stringObject, mClazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // No data are available.
        return null;
    }

    public void invalidate() {
        try {
            mDiskLruCache.delete();
            File folder = new File(VBLibCacheContextUtils.getContext().getCacheDir().getPath() + "/" + CACHE_FILE_PREFIX + "/" + mId);
            mDiskLruCache = DiskLruCache.open(folder, mAppVersion, 1, mDiskCacheSizeinBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRamCacheLru.evictAll();
    }

    private String getDiskFileNameFromKey(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("%02x", b&0xff));
            }
            return CACHE_FILE_PREFIX + "-" + mId + "-" + sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getRamSize() {
        return mRamCacheLru.size();
    }

    public long getDiskSize() {
        return mDiskLruCache.size();
    }
}
