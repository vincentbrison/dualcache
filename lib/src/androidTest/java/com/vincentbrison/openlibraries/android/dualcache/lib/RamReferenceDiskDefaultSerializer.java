package com.vincentbrison.openlibraries.android.dualcache.lib;

import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.Vehicule;

/**
 * Created by Brize on 04/10/2014.
 */
public class RamReferenceDiskDefaultSerializer extends DualCacheTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCache = new DualCacheBuilder<Vehicule>(CACHE_NAME, TEST_APP_VERSION, Vehicule.class)
                .useReferenceInRam(RAM_MAX_SIZE, new SizeOf<Vehicule>() {
                    @Override
                    public int sizeOf(Vehicule object) {
                        int size = 0;
                        size += object.getName().length() * 2; // we suppose that char = 2 bytes
                        size += 4; // we suppose that int = 4 bytes
                        return size;
                    }
                })
                .useJsonInDisk(DISK_MAX_SIZE, true);
    }
}
