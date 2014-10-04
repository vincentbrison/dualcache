package com.vincentbrison.openlibraries.android.dualcache.lib;

import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.Vehicule;

/**
 * Created by Brize on 04/10/2014.
 */
public class RamDefaultSerializerDiskDefaultSerializer extends DualCacheTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCache = new DualCacheBuilder<Vehicule>(CACHE_NAME, TEST_APP_VERSION, Vehicule.class)
                .useJsonInRam(RAM_MAX_SIZE)
                .useJsonInDisk(DISK_MAX_SIZE, true);
    }
}
