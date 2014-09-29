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
public final class DualCacheLogUtils {

    private static boolean isLogEnabled = false;
    private static final String DEFAULT_LOG_TAG = "dualcache";
    private DualCacheLogUtils() {

    }

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
        if (isLogEnabled) {
            Log.println(lvl, tag, msg);
        }
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
     * Default log info using tag {@link #DEFAULT_LOG_TAG}.
     * @param msg is the msg to log.
     */
    public static void logInfo(String msg) {
        log(Log.INFO, DEFAULT_LOG_TAG, msg);
    }

    /**
     * Log with lvl verbose and tag {@link #DEFAULT_LOG_TAG}.
     * @param msg is the msg to log.
     */
    public static void logVerbose(String msg) {
        log(Log.VERBOSE, DEFAULT_LOG_TAG, msg);
    }

    /**
     * Log with lvl warning and tag {@link #DEFAULT_LOG_TAG}.
     * @param msg is the msg to log.
     */
    public static void logWarning(String msg) {
        log(Log.WARN, DEFAULT_LOG_TAG, msg);
    }

    /**
     * Log with lvl error and tag {@link #DEFAULT_LOG_TAG}.
     * @param error is the error to log.
     */
    public static void logError(Throwable error) {
        if (isLogEnabled) {
            Log.e(DEFAULT_LOG_TAG, "error : ", error);
        }
    }
}
