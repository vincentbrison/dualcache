package com.vincentbrison.openlibraries.android.dualcache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vb.openlibraries.android.dualcache.R;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheLogUtils;

import java.util.ArrayList;
import java.util.List;


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

        DualCacheLogUtils.enableLog();
        DualCacheContextUtils.setContext(getApplicationContext());

        mButtonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, DemoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(DemoActivity.EXTRA_DISK_CACHE_SIZE, Integer.parseInt(mEditTextSizeDisk.getText().toString()));
                intent.putExtra(DemoActivity.EXTRA_RAM_CACHE_SIZE, Integer.parseInt(mEditTextSizeRam.getText().toString()));
                intent.putExtra(DemoActivity.EXTRA_ID_CACHE, mEditTextIdCache.getText().toString());
                startActivity(intent);
            }
        });

        mButtonBench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("Bench", "Start bench");

                int maxRamSize = Integer.parseInt(mEditTextSizeRam.getText().toString());
                int maxDiskSize = Integer.parseInt(mEditTextSizeDisk.getText().toString());
                Log.i("Bench", "Max RAM size : " + maxRamSize);
                Log.i("Bench", "Max disk size : " + maxDiskSize);

                Log.i("Bench", "Bench using dummy object");
                LruCache<String, DummyClass> lruCacheTest = new LruCache<String, DummyClass>(maxRamSize);
                DualCache<DummyClass> dualCacheTest = new DualCache<DummyClass>("bench", 1, maxRamSize, maxDiskSize, true, true, DummyClass.class);

                int numberOfActions = 100;
                List<DummyClass> dummyObjects = new ArrayList<DummyClass>();
                for (int i = 0; i < numberOfActions; i++) {
                        dummyObjects.add(new DummyClass());
                }

                long start, end, time;
                start = System.currentTimeMillis();
                for (int i = 0; i < numberOfActions; i++) {
                    lruCacheTest.put("" + numberOfActions, dummyObjects.get(i));
                    lruCacheTest.get("" + numberOfActions);
                }
                end = System.currentTimeMillis();
                time = end - start;
                Log.i("Bench", "LruCacheTest time for " + numberOfActions + " randoms puts and gets : " + time + " ms.");

                start = System.currentTimeMillis();
                for (int i = 0; i < numberOfActions; i++) {
                    dualCacheTest.put("" + numberOfActions, dummyObjects.get(i));
                    dualCacheTest.get("" + numberOfActions);
                }
                end = System.currentTimeMillis();
                time = end - start;
                Log.i("Bench", "DualCacheTest time for " + numberOfActions + " randoms puts and gets : " + time + " ms.");
            }
        });



    }

}
