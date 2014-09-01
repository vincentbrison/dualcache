package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.content.Context;

/**
 * Created by Vincent Brison.
 * This class provide a context to the library.
 */
public class DualCacheContextUtils {
    private static Context mContext = null;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        if (mContext == null)
            DualCacheLogUtils.logInfo("The context provided to this library is null. Please provide"
                    + " a proper context according the lifecycle of the application.");
        return mContext;
    }

}
