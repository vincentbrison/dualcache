package com.vincentbrison.openlibraries.android.dualcache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class DualCacheLock {

    private final ConcurrentMap<String, Lock> editionLocks = new ConcurrentHashMap<>();
    private final ReadWriteLock invalidationReadWriteLock = new ReentrantReadWriteLock();

    void lockDiskEntryWrite(String key) {
        invalidationReadWriteLock.readLock().lock();
        getLockForGivenDiskEntry(key).lock();
    }

    void unLockDiskEntryWrite(String key) {
        getLockForGivenDiskEntry(key).unlock();
        invalidationReadWriteLock.readLock().unlock();
    }

    void lockFullDiskWrite() {
        invalidationReadWriteLock.writeLock().lock();
    }

    void unLockFullDiskWrite() {
        invalidationReadWriteLock.writeLock().unlock();
    }

    private Lock getLockForGivenDiskEntry(String key) {
        if (!editionLocks.containsKey(key)) {
            editionLocks.putIfAbsent(key, new ReentrantLock());
        }
        return editionLocks.get(key);
    }
}
