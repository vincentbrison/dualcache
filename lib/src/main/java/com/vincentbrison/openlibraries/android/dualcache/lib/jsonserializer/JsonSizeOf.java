package com.vincentbrison.openlibraries.android.dualcache.lib.jsonserializer;

import com.vincentbrison.openlibraries.android.dualcache.lib.RamSizeOf;

import java.nio.charset.Charset;

public class JsonSizeOf implements RamSizeOf<String> {

    @Override
    public int sizeOf(String object) {
        return object.getBytes(Charset.forName("UTF-8")).length;
    }
}
