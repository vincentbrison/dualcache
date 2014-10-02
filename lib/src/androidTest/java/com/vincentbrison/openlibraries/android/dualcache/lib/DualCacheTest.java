package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolBike;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.CoolCar;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.MotorBike;
import com.vincentbrison.openlibraries.android.dualcache.lib.testobjects.Vehicule;

public class DualCacheTest extends ApplicationTestCase<Application> {


    public DualCacheTest() {
        super(Application.class);
    }

    public void testOnlyRAM() throws Exception {
        DualCache<Vehicule> cache = new DualCacheBuilder<Vehicule>("test", 1, Vehicule.class).useJsonInRam(1000).noDisk();

        CoolCar car = new CoolCar();
        cache.put("key", car);
        assertEquals(car, cache.get("key"));

        cache.invalidateRAM();
        assertNull(cache.get("key"));

        cache.put("key", car);
        assertEquals(car, cache.get("key"));

        cache.invalidate();
        assertNull(cache.get("key"));

        CoolBike bike = new CoolBike();
        cache.put("car", car);
        cache.put("bike", bike);
        assertEquals(cache.get("car"), car);
        assertEquals(cache.get("bike"), bike);
    }

    public void testDebug() throws Exception {
        assertTrue(1 == 1);
    }


}
