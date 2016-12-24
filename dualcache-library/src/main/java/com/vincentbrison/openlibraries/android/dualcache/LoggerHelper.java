package com.vincentbrison.openlibraries.android.dualcache;

class LoggerHelper {

    private static final String LOG_PREFIX = "Entry for ";

    private final Logger logger;

    LoggerHelper(Logger logger) {
        this.logger = logger;
    }

    void logEntrySavedForKey(String key) {
        logger.logInfo(LOG_PREFIX + key + " is saved in cache.");
    }

    void logEntryForKeyIsInRam(String key) {
        logger.logInfo(LOG_PREFIX + key + " is in RAM.");
    }

    void logEntryForKeyIsNotInRam(String key) {
        logger.logInfo(LOG_PREFIX + key + " is not in RAM.");
    }

    void logEntryForKeyIsOnDisk(String key) {
        logger.logInfo(LOG_PREFIX + key + " is on disk.");
    }

    void logEntryForKeyIsNotOnDisk(String key) {
        logger.logInfo(LOG_PREFIX + key + " is not on disk.");
    }
}
