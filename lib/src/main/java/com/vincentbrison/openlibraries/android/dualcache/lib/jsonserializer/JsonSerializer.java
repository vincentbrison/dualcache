package com.vincentbrison.openlibraries.android.dualcache.lib.jsonserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincentbrison.openlibraries.android.dualcache.lib.Serializer;

import java.io.IOException;

public class JsonSerializer<T> implements Serializer<T> {
    private final ObjectMapper mapper;
    private final Class<T> clazz;

    public JsonSerializer(Class<T> clazz) {
        this.clazz = clazz;
        mapper = new ObjectMapper();
    }

    @Override
    public T fromString(String data) {
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @Override
    public String toString(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }
}
