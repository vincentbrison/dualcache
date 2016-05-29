package com.vincentbrison.openlibraries.android.dualcache.lib.configurationsToTest;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheTest;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.AbstractVehicule;

public class RamReferenceDiskDefaultSerializer extends DualCacheTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mCache = new Builder<AbstractVehicule>(CACHE_NAME, TEST_APP_VERSION, AbstractVehicule.class)
            .logEnabled(true)
            .useReferenceInRam(RAM_MAX_SIZE, new SizeOfVehiculeForTesting())
            .useSerializerInDisk(DISK_MAX_SIZE, true, defaultCacheSerializer, getContext())
            .build();
    }
}
