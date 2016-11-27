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

    public void lockDiskEntryWrite(String key) {
        invalidationReadWriteLock.readLock().lock();
        getLockForGivenEntry(key).lock();
    }

    public void unLockDiskEntryWrite(String key) {
        getLockForGivenEntry(key).unlock();
        invalidationReadWriteLock.readLock().unlock();
    }

    public void lockFullDiskWrite() {
        invalidationReadWriteLock.writeLock().lock();
    }

    public void unLockFullDiskWrite() {
        invalidationReadWriteLock.writeLock().unlock();
    }

    // Let concurrent modification on different keys.
    private Lock getLockForGivenEntry(String key) {
        if (!editionLocks.containsKey(key)) {
            editionLocks.putIfAbsent(key, new ReentrantLock());
        }
        return editionLocks.get(key);
    }
}
