package vb.android.library.cache.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

    private EditText mEditTextSizeRam;
    private EditText mEditTextSizeDisk;
    private EditText mEditTextIdCache;
    private Button mButtonDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mEditTextSizeRam = (EditText) findViewById(R.id.activity_settings_edittext_ram_cache_size);
        mEditTextSizeDisk = (EditText) findViewById(R.id.activity_settings_edittext_disk_cache_size);
        mEditTextIdCache = (EditText) findViewById(R.id.activity_settings_edittext_cache_id);
        mButtonDemo = (Button) findViewById(R.id.activity_settings_button_demo);

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
    }

}
