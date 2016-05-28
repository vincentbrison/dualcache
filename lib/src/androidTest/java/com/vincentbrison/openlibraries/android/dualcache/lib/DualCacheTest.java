package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.util.Log;

import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.AbstractVehicule;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolBike;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolCar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public abstract class DualCacheTest extends AndroidTestCase {

    protected static final int RAM_MAX_SIZE = 1000;
    protected static final int DISK_MAX_SIZE = 20 * RAM_MAX_SIZE;
    protected static final String CACHE_NAME = "test";
    protected static final int TEST_APP_VERSION = 0;
    protected DualCache<AbstractVehicule> mCache;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry.getTargetContext());
    }

    @After
    @Override
    public void tearDown() throws Exception {
        mCache.invalidate();
        super.tearDown();
    }

    @Test
    public void testBasicOperations() throws Exception {
        CoolCar car = new CoolCar();
        mCache.put("key", car);
        if (mCache.getRAMMode().equals(DualCache.DualCacheRAMMode.DISABLE) &&
                mCache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE)) {
            assertNull(mCache.get("key"));
        } else {
            assertEquals(car, mCache.get("key"));
        }

        mCache.invalidateRAM();
        if (mCache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE)) {
            assertNull(mCache.get("key"));
        } else {
            assertEquals(car, mCache.get("key"));
        }

        mCache.put("key", car);
        if (mCache.getRAMMode().equals(DualCache.DualCacheRAMMode.DISABLE) &&
                mCache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE)) {
            assertNull(mCache.get("key"));
        } else {
            assertEquals(car, mCache.get("key"));
        }

        mCache.invalidate();
        assertNull(mCache.get("key"));

        CoolBike bike = new CoolBike();
        mCache.put("car", car);
        mCache.put("bike", bike);
        if (mCache.getRAMMode().equals(DualCache.DualCacheRAMMode.DISABLE) &&
                mCache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE)) {
            assertNull(mCache.get("car"));
            assertNull(mCache.get("bike"));
        } else {
            assertEquals(mCache.get("car"), car);
            assertEquals(mCache.get("bike"), bike);
        }
    }

    @Test
    public void testBasicOperations2() throws Exception {
        CoolCar car = new CoolCar();
        mCache.put("key", car);
        mCache.invalidateRAM();
        if (mCache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE)) {
            assertNull(mCache.get("key"));
        } else {
            assertEquals(car, mCache.get("key"));
            mCache.invalidateRAM();
        }

        mCache.invalidateDisk();
        assertNull(mCache.get("key"));

        mCache.put("key", car);
        mCache.invalidateRAM();
        if (mCache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE)) {
            assertNull(mCache.get("key"));
        } else {
            assertEquals(car, mCache.get("key"));
        }

        mCache.invalidate();
        assertNull(mCache.get("key"));

        CoolBike bike = new CoolBike();
        mCache.put("car", car);
        mCache.put("bike", bike);
        mCache.delete("car");
        mCache.delete("bike");
        assertNull(mCache.get("car"));
        assertNull(mCache.get("bike"));
    }

    @Test
    public void testLRUPolicy() {
        mCache.invalidate();
        CoolCar carToEvict = new CoolCar();
        mCache.put("car", carToEvict);
        long size = mCache.getRamSize();
        int numberOfItemsToAddForRAMEviction = (int) (RAM_MAX_SIZE / size);
        for (int i = 0; i < numberOfItemsToAddForRAMEviction; i++) {
            mCache.put("car" + i, new CoolCar());
        }
        mCache.invalidateDisk();
        assertNull(mCache.get("car"));

        mCache.put("car", carToEvict);
        for (int i = 0; i < numberOfItemsToAddForRAMEviction; i++) {
            mCache.put("car" + i, new CoolCar());
        }
        if (!mCache.getDiskMode().equals(DualCache.DualCacheDiskMode.DISABLE)) {
            assertEquals(carToEvict, mCache.get("car"));
        } else {
            assertNull(mCache.get("car"));
        }
    }

    @Test
    public void testConcurrentAccess() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(createWrokerThread(mCache));
        }
        Log.d("dualcachedebuglogti", "start worker threads");
        for (Thread thread : threads) {
            thread.start();
        }

        Log.d("dualcachedebuglogti", "joining worker threads");
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("dualcachedebuglogti", "join done");
        assertFalse("test", false);
    }

    private Thread createWrokerThread(final DualCache<AbstractVehicule> cache) {
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
                            cache.put("key", new CoolCar());
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

    public static class SerializerForTesting implements Serializer<AbstractVehicule> {

        @Override
        public AbstractVehicule fromBytes(byte[] data) {
            if (new String(data).equals(CoolBike.class.getSimpleName())) {
                return new CoolBike();
            } else if (new String(data).equals(CoolCar.class.getSimpleName())) {
                return new CoolCar();
            } else {
                return null;
            }
        }

        @Override
        public byte[] toBytes(AbstractVehicule object) {
            return object.getClass().getSimpleName().getBytes();
        }
    }

    public static class SizeOfVehiculeForTesting implements RamSizeOf<AbstractVehicule> {

        @Override
        public int sizeOf(AbstractVehicule object) {
            int size = 0;
            size += object.getName().length() * 2; // we suppose that char = 2 bytes
            size += 4; // we suppose that int = 4 bytes
            return size;
        }
    }
}
