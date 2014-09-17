package com.vincentbrison.openlibraries.android.dualcache.lib;

/**
 * TODO: Add a class header comment!
 */
public interface Serializer<T> {
    public T fromString(String data);
    public String toString(T object);
}
