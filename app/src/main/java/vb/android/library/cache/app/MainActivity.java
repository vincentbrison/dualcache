package vb.android.library.cache.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

import vb.android.library.cache.lib.SimpleCache;
import vb.android.library.cache.lib.CacheWrapper;
import vb.android.library.cache.lib.VBLibCacheContextUtils;
import vb.android.library.cache.lib.VBLibCacheLogUtils;


public class MainActivity extends Activity {

    private Button mButtonAddObject10Sec;
    private Button mButtonAddObjectInfinite;
    private Button mButtonDisplayObject10Sec;
    private Button mButtonDisplayObjectInfinite;
    private Button mButtonClearCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VBLibCacheLogUtils.enableLog();
        VBLibCacheContextUtils.setContext(getApplicationContext());

        mButtonAddObject10Sec = (Button) findViewById(R.id.buttonAddObject10ToCache);
        mButtonAddObjectInfinite = (Button) findViewById(R.id.buttonAddObjectInfiniteLifetime);
        mButtonClearCache = (Button) findViewById(R.id.buttonClearCache);
        mButtonDisplayObjectInfinite = (Button) findViewById(R.id.buttonDisplayObjectInfiniteLifetime);
        mButtonDisplayObject10Sec = (Button) findViewById(R.id.buttonDisplayObject10Sec);

        mButtonAddObject10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                date.setTime(date.getTime() + 10000);

                // String is serializable so can be cached.
                CacheWrapper wrapper = new CacheWrapper("object10sec", date);
                SimpleCache cache = SimpleCache.getCacheManager("mycache");
                cache.put("object10sec", wrapper);
            }
        });

        mButtonAddObjectInfinite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // String is serializable so can be cached.
                CacheWrapper wrapper = new CacheWrapper("objectInfinite", null);
                SimpleCache cache = SimpleCache.getCacheManager("mycache");
                cache.put("objectInfinite", wrapper);
            }
        });

        mButtonDisplayObject10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleCache cache = SimpleCache.getCacheManager("mycache");
                String object = null;
                object = cache.get("object10sec", String.class);
                if (object != null) {
                    Toast.makeText(MainActivity.this, object, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "object10sec is not in cache", Toast.LENGTH_SHORT).show();
            }
        });

        mButtonDisplayObjectInfinite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleCache cache = SimpleCache.getCacheManager("mycache");
                String object = null;
                object = cache.get("objectInfinite", String.class);
                if (object != null) {
                    Toast.makeText(MainActivity.this, object, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "objectInfinite is not in cache", Toast.LENGTH_SHORT).show();
            }
        });

        mButtonClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleCache cache = SimpleCache.getCacheManager("mycache");
                cache.invalidate();
            }
        });

    }
}

