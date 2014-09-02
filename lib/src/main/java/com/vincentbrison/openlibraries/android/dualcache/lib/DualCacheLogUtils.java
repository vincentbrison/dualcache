/*
 * Copyright 2014 Vincent Brison.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vincentbrison.openlibraries.android.dualcache.lib;

import android.util.Log;

/**
 * Created by Vincent Brison.
 * This class provide a log utility to the library.
 */
public class DualCacheLogUtils {

    private static boolean isLogEnabled = false;

    /**
     * Enable the logs of this library. By default logs are disabled.
     */
    public static void enableLog() {
        isLogEnabled = true;
    }

    /**
     * Disable the logs of this library. By default logs are disabled.
     */
    public static void disableLog() {
        isLogEnabled = false;
    }

    private static void log(int lvl, String tag, String msg) {
        if (lvl != Log.ASSERT && lvl != Log.DEBUG && lvl != Log.ERROR && lvl != Log.INFO && lvl != Log.VERBOSE && lvl != Log.WARN)
            Log.w("", "Warning, wrong lvl used for logging. Must be Log.ASSERT, Log.DEBUG, Log.ERROR, Log.INFO, Log.VERBOSE, or Log.WARN");
        else if (isLogEnabled)
            Log.println(lvl, tag, msg);
    }

    /**
     * Log with lvl info.
     * @param tag is the tag to used.
     * @param msg is the msg to log.
     */
    public static void logInfo(String tag, String msg) {
        log(Log.INFO, tag, msg);
    }

    /**
     * Default log info using tag bfi.lib.cache.
     * @param msg is the msg to log.
     */
    public static void logInfo(String msg) {
        log(Log.INFO, "dualcache", msg);
    }
}
