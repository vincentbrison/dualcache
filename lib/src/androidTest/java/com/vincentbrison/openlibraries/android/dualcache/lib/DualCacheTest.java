package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.test.AndroidTestCase;

import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.AbstractVehicule;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolBike;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolCar;

public abstract class DualCacheTest extends AndroidTestCase {

    protected static final int RAM_MAX_SIZE = 1000;
    protected static final int DISK_MAX_SIZE = 20 * RAM_MAX_SIZE;
    protected static final String CACHE_NAME = "test";
    protected static final int TEST_APP_VERSION = 0;
    protected DualCache<AbstractVehicule> mCache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DualCacheContextUtils.setContext(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        mCache.invalidate();
        super.tearDown();
    }

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

    public static class SerializerForTesting implements Serializer<AbstractVehicule> {

        @Override
        public AbstractVehicule fromString(String data) {
            if (data.equals(CoolBike.class.getSimpleName())) {
                return new CoolBike();
            } else if (data.equals(CoolCar.class.getSimpleName())) {
                return new CoolCar();
            } else {
                return null;
            }
        }

        @Override
        public String toString(AbstractVehicule object) {
            return object.getClass().getSimpleName();
        }
    }

    public static class SizeOfVehiculeForTesting implements SizeOf<AbstractVehicule> {

        @Override
        public int sizeOf(AbstractVehicule object) {
            int size = 0;
            size += object.getName().length() * 2; // we suppose that char = 2 bytes
            size += 4; // we suppose that int = 4 bytes
            return size;
        }
    }
}
