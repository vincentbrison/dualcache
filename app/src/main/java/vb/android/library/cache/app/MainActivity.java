package vb.android.library.cache.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import vb.android.library.cache.lib.VBLibCacheContextUtils;
import vb.android.library.cache.lib.VBLibCacheLogUtils;


public class MainActivity extends Activity {

    private Button mButtonAddObjectA;
    private Button mButtonAddObjectB;
    private Button mButtonDisplayObjectB;
    private Button mButtonDisplayObjectA;
    private Button mButtonInvalidateCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VBLibCacheLogUtils.enableLog();
        VBLibCacheContextUtils.setContext(getApplicationContext());

        mButtonAddObjectA = (Button) findViewById(R.id.buttonAddObjectAToCache);
        mButtonAddObjectB = (Button) findViewById(R.id.buttonAddObjectBToCache);
        mButtonInvalidateCache = (Button) findViewById(R.id.buttonInvalidateCache);
        mButtonDisplayObjectA = (Button) findViewById(R.id.buttonDisplayObjectB);
        mButtonDisplayObjectB = (Button) findViewById(R.id.buttonDisplayObjectA);

        mButtonAddObjectA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mButtonAddObjectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        mButtonDisplayObjectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mButtonDisplayObjectA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mButtonInvalidateCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

    }
}

