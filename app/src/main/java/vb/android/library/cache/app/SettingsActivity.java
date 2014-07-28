package vb.android.library.cache.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

import vb.android.library.cache.lib.DualCache;
import vb.android.library.cache.lib.VBLibCacheContextUtils;
import vb.android.library.cache.lib.VBLibCacheLogUtils;

public class SettingsActivity extends Activity {

    private EditText mEditTextSizeRam;
    private EditText mEditTextSizeDisk;
    private EditText mEditTextIdCache;
    private Button mButtonDemo;
    private Button mButtonBench;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mEditTextSizeRam = (EditText) findViewById(R.id.activity_settings_edittext_ram_cache_size);
        mEditTextSizeDisk = (EditText) findViewById(R.id.activity_settings_edittext_disk_cache_size);
        mEditTextIdCache = (EditText) findViewById(R.id.activity_settings_edittext_cache_id);
        mButtonDemo = (Button) findViewById(R.id.activity_settings_button_demo);
        mButtonBench = (Button) findViewById(R.id.activity_settings_button_bench);

        //VBLibCacheLogUtils.enableLog();
        VBLibCacheContextUtils.setContext(getApplicationContext());

        mButtonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, DemoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(DemoActivity.EXTRA_DISK_CACHE_SIZE, Integer.getInteger(mEditTextSizeDisk.getText().toString(), 100));
                intent.putExtra(DemoActivity.EXTRA_RAM_CACHE_SIZE, Integer.getInteger(mEditTextSizeRam.getText().toString(), 50));
                intent.putExtra(DemoActivity.EXTRA_ID_CACHE, mEditTextIdCache.getText().toString());
                startActivity(intent);
            }
        });

        mButtonBench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Log.i("Bench", "Start bench");

                int maxRamSize = Integer.parseInt(mEditTextSizeRam.getText().toString());
                int maxDiskSize = Integer.parseInt(mEditTextSizeDisk.getText().toString());
                Log.i("Bench", "Max RAM size : " + maxRamSize);
                Log.i("Bench", "Max disk size : " + maxDiskSize);

                Log.i("Bench", "Bench using dummy object");
                LruCache<String, DummyClass> lruCacheTest = new LruCache<String, DummyClass>(maxRamSize);
                DualCache<DummyClass> dualCacheTest = new DualCache<DummyClass>("bench", 1, maxRamSize, maxDiskSize, DummyClass.class);
                //dualCacheTest.setMode(DualCache.DualCacheMode.ONLY_RAM);
                DummyClass dummyObject = new DummyClass();
                int numberOfActions = 50;
                long start, end, time;
                start = System.currentTimeMillis();
                for (int i = 0; i < numberOfActions; i++) {
                    lruCacheTest.put(UUID.randomUUID().toString(), dummyObject);
                    lruCacheTest.get(UUID.randomUUID().toString());
                }
                end = System.currentTimeMillis();
                time = end - start;
                Log.i("Bench", "LruCacheTest time for " + numberOfActions + " randoms puts and gets : " + time + " ms.");

                start = System.currentTimeMillis();
                for (int i = 0; i < numberOfActions; i++) {
                    dualCacheTest.put(UUID.randomUUID().toString(), dummyObject);
                    dualCacheTest.get(UUID.randomUUID().toString());
                }
                end = System.currentTimeMillis();
                time = end - start;
                Log.i("Bench", "DualCacheTest time for " + numberOfActions + " randoms puts and gets : " + time + " ms.");*/

                DualCache<Object> dualCacheTest = new DualCache<Object>("bench", 1, 500, 1000, Object.class);
                dualCacheTest.put("A", "test");
                dualCacheTest.invalidateRAM();
                Log.i("bench", (String)dualCacheTest.get("A"));
            }
        });



    }

}
