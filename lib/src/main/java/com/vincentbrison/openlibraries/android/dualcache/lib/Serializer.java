package com.vincentbrison.openlibraries.android.dualcache.lib;

/**
 * This interface describe the way an object should be serialized/deserialized into String.
 * @param <T> is the class of object to serialized/deserialized.
 */
public interface Serializer<T> {
    /**
     * Deserialization of a String into an object.
     * @param data is the string representing the serialized data.
     * @return the deserialized data.
     */
    public T fromString(String data);

    /**
     * Serialization of an object into String.
     * @param object is the object to serialize.
     * @return the result of the serialization into a String.
     */
    public String toString(T object);
}
