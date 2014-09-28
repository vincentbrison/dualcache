package com.vincentbrison.openlibraries.android.dualcache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vb.openlibraries.android.dualcache.R;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheLogUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This activity ask user input to configure the demo.
 */
public class SettingsActivity extends Activity {

    @InjectView(R.id.activity_settings_edittext_ram_cache_size) private EditText mEditTextSizeRam;
    @InjectView(R.id.activity_settings_edittext_disk_cache_size) private EditText mEditTextSizeDisk;
    @InjectView(R.id.activity_settings_edittext_cache_id) private EditText mEditTextIdCache;
    @InjectView(R.id.activity_settings_button_demo) private Button mButtonDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        DualCacheLogUtils.enableLog();
        DualCacheContextUtils.setContext(getApplicationContext());

        mButtonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, DemoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra(DemoActivity.EXTRA_DISK_CACHE_SIZE,
                        Integer.parseInt(mEditTextSizeDisk.getText().toString()));
                intent.putExtra(DemoActivity.EXTRA_RAM_CACHE_SIZE,
                        Integer.parseInt(mEditTextSizeRam.getText().toString()));
                intent.putExtra(DemoActivity.EXTRA_ID_CACHE, mEditTextIdCache.getText().toString());
                startActivity(intent);
            }
        });
    }

}
