package com.vincentbrison.openlibraries.android.dualcache.lib.configurationsToTest;

import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheBuilder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheTest;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.AbstractVehicule;

/**
 * Created by Brize on 04/10/2014.
 */
public class NoRamDiskCustomSerializer extends DualCacheTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mCache = new DualCacheBuilder<>(CACHE_NAME, TEST_APP_VERSION, AbstractVehicule.class, true)
                .noRam()
                .useCustomSerializerInDisk(DISK_MAX_SIZE, true, new DualCacheTest.SerializerForTesting(), getContext());
    }
}
