package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolBike;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolCar;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.Vehicule;

public abstract class DualCacheTest extends ApplicationTestCase<Application> {

    protected static final int RAM_MAX_SIZE = 1000;
    protected static final int DISK_MAX_SIZE = 2000;
    protected static final String CACHE_NAME = "test";
    protected static final int TEST_APP_VERSION = 0;
    protected DualCache<Vehicule> mCache;

    public DualCacheTest() {
        super(Application.class);
    }

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
}
