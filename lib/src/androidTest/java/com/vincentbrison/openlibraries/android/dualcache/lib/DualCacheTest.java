package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.app.Application;
import android.test.ApplicationTestCase;

public class DualCacheTest extends ApplicationTestCase<Application> {


    public DualCacheTest() {
        super(Application.class);
    }

    public void testOnlyRAM() throws Exception {
        DualCache<String> cache = new DualCacheBuilder<String>("test", 1, String.class).useJsonInRam(100).noDisk();

        cache.put("key", "test string");

        assertEquals("test string", cache.get("key"));
    }

    public void testDebug() throws Exception {
        assertTrue(1 == 1);
    }


}
