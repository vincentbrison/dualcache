package com.vincentbrison.openlibraries.android.dualcache;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vb.openlibraries.android.dualcache.R;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCache;

import java.util.UUID;


public class DemoActivity extends Activity {

    public final static String EXTRA_DISK_CACHE_SIZE = "EXTRA_DISK_CACHE_SIZE";
    public final static String EXTRA_RAM_CACHE_SIZE = "EXTRA_RAM_CACHE_SIZE";
    public final static String EXTRA_ID_CACHE = "EXTRA_ID_CACHE";

    private int mDiskCacheSize;
    private int mRamCacheSize;
    private String mCacheId;
    private DualCache<String> mCache;

    private Handler mHandler;

    private Button mButtonAddObjectA;
    private Button mButtonAddObjectB;
    private Button mButtonAddRandomObject;
    private Button mButtonDisplayObjectB;
    private Button mButtonDisplayObjectA;
    private Button mButtonDisplayRandomObject;
    private Button mButtonInvalidateCache;
    private TextView mTextViewDataRam;
    private TextView mTextViewDataDisk;
    private TextView mTextViewDataTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCacheId = getIntent().getStringExtra(EXTRA_ID_CACHE);
        mDiskCacheSize = getIntent().getIntExtra(EXTRA_DISK_CACHE_SIZE, 100);
        mRamCacheSize = getIntent().getIntExtra(EXTRA_RAM_CACHE_SIZE, 50);

        setContentView(R.layout.activity_demo);

        mCache = new DualCache<String>(mCacheId, 1, mRamCacheSize, mDiskCacheSize, true, String.class);

        mButtonAddObjectA = (Button) findViewById(R.id.buttonAddObjectAToCache);
        mButtonAddObjectB = (Button) findViewById(R.id.buttonAddObjectBToCache);
        mButtonAddRandomObject = (Button) findViewById(R.id.buttonAddRandomObjectToCache);
        mButtonInvalidateCache = (Button) findViewById(R.id.buttonInvalidateCache);
        mButtonDisplayObjectA = (Button) findViewById(R.id.buttonDisplayObjectA);
        mButtonDisplayObjectB = (Button) findViewById(R.id.buttonDisplayObjectB);
        mButtonDisplayRandomObject = (Button) findViewById(R.id.buttonDisplayRandomObject);
        mTextViewDataDisk = (TextView) findViewById(R.id.textViewDataSizeDisk);
        mTextViewDataRam = (TextView) findViewById(R.id.textViewDataSizeRam);
        mTextViewDataTime = (TextView) findViewById(R.id.textViewDataTime);

        mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshCacheSize();
                mHandler.postDelayed(this, 500);
            }
        });

        mButtonAddObjectA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCache.put("a", "objectA");
            }
        });

        mButtonAddObjectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCache.put("b", "objectB");
            }
        });

        mButtonAddRandomObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCache.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            }
        });

        mButtonDisplayObjectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long start = System.currentTimeMillis();
                String result = mCache.get("b");
                long end = System.currentTimeMillis();
                long time = end - start;
                if (result != null) {
                    Toast.makeText(DemoActivity.this, result, Toast.LENGTH_SHORT).show();
                }
                mTextViewDataTime.setText("Last access time : " + time + " ms");
            }
        });

        mButtonDisplayObjectA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long start = System.currentTimeMillis();
                String result = mCache.get("a");
                long end = System.currentTimeMillis();
                long time = end - start;
                if (result != null) {
                    Toast.makeText(DemoActivity.this, result, Toast.LENGTH_SHORT).show();
                }
                mTextViewDataTime.setText("Last access time : " + time + " ms");
            }
        });

        mButtonDisplayRandomObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mButtonInvalidateCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCache.invalidate();
            }
        });

    }

    private void refreshCacheSize() {
        mTextViewDataRam.setText("Ram : " + mCache.getRamSize() + "/" + mRamCacheSize + " B");
        mTextViewDataDisk.setText("Disk : " + mCache.getDiskSize() + "/" + mDiskCacheSize + " B");
    }
}

