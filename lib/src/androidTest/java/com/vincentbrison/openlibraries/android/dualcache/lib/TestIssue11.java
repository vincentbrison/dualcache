package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Test issue 11.
 */
public class TestIssue11 extends AndroidTestCase {
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int CACHE_RAM_ENTRIES = 25;
    protected static final String CACHE_NAME = "test";
    protected static final int TEST_APP_VERSION = 0;
    protected DualCache<String> mCache;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry.getTargetContext());
        File cacheDir = new File(mContext.getCacheDir(), CACHE_NAME);
        mCache = new DualCacheBuilder<>(CACHE_NAME, 0, String.class, false)
            .useDefaultSerializerInRam(CACHE_RAM_ENTRIES)
            .useDefaultSerializerInDisk(CACHE_SIZE, cacheDir, getContext());
    }

    @After
    @Override
    public void tearDown() throws Exception {
        mCache.invalidate();
        super.tearDown();
    }

    @Test
    public void testConcurrentAccess() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(createWrokerThread(mCache));
        }
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertFalse("test", false);
    }

    private Thread createWrokerThread(final DualCache<String> cache) {
        return new Thread() {
            int sMaxNumberOfRun = 1000;
            @Override
            public void run() {
                try {
                    int numberOfRun = 0;
                    while (numberOfRun++ < sMaxNumberOfRun) {
                        Thread.sleep((long) (Math.random() * 2));
                        double choice = Math.random();
                        if (choice < 0.4) {
                            cache.put("key", "test");
                        } else if (choice < 0.5) {
                            cache.delete("key");
                        } else if (choice < 0.8) {
                            cache.get("key");
                        } else if (choice < 1) {
                            cache.invalidate();
                        } else {
                            // do nothing
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
