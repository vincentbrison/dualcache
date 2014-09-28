package com.vincentbrison.openlibraries.android.dualcache.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@Config(manifest = "./src/main/AndroidManifest.xml", emulateSdk = 16)
@RunWith(RobolectricTestRunner.class)
public class DualCacheTest {

    @Test
    public void testSomething() throws Exception {
        assertTrue(1 == 2);
    }


}
