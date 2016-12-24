package com.vincentbrison.openlibraries.android.dualcache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vb.openlibraries.android.dualcache.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity ask user input to configure the demo.
 */
public class SettingsActivity extends Activity {

    //CHECKSTYLE:OFF
    @BindView(R.id.activity_settings_edittext_ram_cache_size) protected EditText mEditTextSizeRam;
    @BindView(R.id.activity_settings_edittext_disk_cache_size) protected EditText mEditTextSizeDisk;
    @BindView(R.id.activity_settings_edittext_cache_id) protected EditText mEditTextIdCache;
    @BindView(R.id.activity_settings_button_demo) protected Button mButtonDemo;
    //CHECKSTYLE:ON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mButtonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, DemoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra(
                    DemoActivity.EXTRA_DISK_CACHE_SIZE,
                    tryGetNumber(mEditTextSizeDisk, 100)
                );
                intent.putExtra(
                    DemoActivity.EXTRA_RAM_CACHE_SIZE,
                    tryGetNumber(mEditTextSizeRam, 50)
                );
                intent.putExtra(DemoActivity.EXTRA_ID_CACHE, mEditTextIdCache.getText().toString());
                startActivity(intent);
            }
        });
    }

    private int tryGetNumber(EditText editText, int defaultValue) {
        try {
            return Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}
