package com.vincentbrison.openlibraries.android.dualcache.lib.configurationsToTest;

import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheBuilder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheTest;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.AbstractVehicule;

/**
 * Created by Brize on 04/10/2014.
 */
public class RamDefaultSerializerDiskDefaultSerializer extends DualCacheTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCache = new DualCacheBuilder<AbstractVehicule>(CACHE_NAME, TEST_APP_VERSION, AbstractVehicule.class)
                .useDefaultSerializerInRam(RAM_MAX_SIZE)
                .useDefaultSerializerInDisk(DISK_MAX_SIZE, true);
    }
}
