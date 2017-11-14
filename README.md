Deprecated
==========
This library is now deprecated in favor of [Store](https://github.com/NYTimes/Store) which fulfill all the goal of this library.

Android dualcache
=================
[![API](https://img.shields.io/badge/API-9%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=9)
[![Build Status](https://travis-ci.org/vincentbrison/dualcache.svg?branch=master)](https://travis-ci.org/vincentbrison/dualcache)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-dualcache-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1377)

This android library provide a cache with 2 layers, one in RAM in top of one on local storage.
This library is highly configurable :


| Configurations | Disk : `Specific serializer` | Disk : `disable` |
| -------------- | -------------------------- | ---------------- |
| Ram : `Specific serializer` | YES | YES |
| Ram : `References` | YES | YES |
| Ram : `disable` | YES | NO |

 - `Specific serializer` : the object stored in cache will be serialized through a serializer provided by yourself.
 - `References` : the objects stored in Ram are cached through there references (no serialization is done).
 - `Disable` : the corresponding layer (Ram or disk) is disable.

If you work with `specific serializer` or `references` you will have to provide (through an interface) the
way of compute the size of cached objects, to be able to correctly execute the [LRU policy] (http://en.wikipedia.org/wiki/Cache_algorithms).

If you do not want to write your own serializer and a json serializer is enough for you, you can use
`dualcache-jsonserializer` which will serialize object using [Jackson](https://github.com/FasterXML/jackson-databind)

The following diagrams are showing how the `dualcache` is working :
- DualCache with specific serializer in RAM and specific serializer in disk.
![dualcache-serializer](doc-assets/dualcache-serializer.png)


- DualCache with references in RAM and specific serializer in disk.
![dualcache-serializer-ref](doc-assets/dualcache-serializer-ref.png)

To get the best performance from this library, I recommend that you use larger size for the disk layer than
for the Ram layer. When you try to get an object from the cache which is already in the Ram layer, the disk wont be
use to keep the best performance from the Ram. If you try to get an object from the cache which is on disk and not on Ram,
the object will be loaded into RAM, to ensure better further access time.

The Philosophy behind this library
==================================
When you want to use a [cache] (http://en.wikipedia.org/wiki/Cache_\(computing\)) on Android today, you have two possibilities. You whether use :
 - The [LruCache] (http://developer.android.com/reference/android/util/LruCache.html) included into the Android SDK.
 - The [DiskLruCache] (https://github.com/JakeWharton/DiskLruCache) of Jake Wharton.

The thing is the first one only works in RAM, and the second one only on disk (internal memory of the phone). So you need to choose
whether if you will use the LruCache (RAM) :
 - Very fast access to your cache.
 - High resources constraints, since the RAM allocated to your application is used for caching.
 - Not persistent among different execution of your app.

Or you will use the DiskLruCache (Disk) :
 - Slower access time than the LruCache.
 - Almost no resources constraints, since the size used on the disk (internal memory), will not impact your application.
 - Persistent among different execution of your app.

The purpose of this library is to provide both features of these two caches, by making them working together. You do not need
to ask yourself anymore "Should I use this one or this one ? But this one is persistent, but the other one is faster...".
With this library you only use one cache, with two layers, one in RAM, and one in Disk and you configure how they have to work
to provide exactly what you need in term of caching for you application.

What's new in 3.0.0
===================
- Only one dependency to [DiskLruCache](https://github.com/JakeWharton/DiskLruCache) because apk size matters.
- Non coupled serializer, with cleaner implementation. Previously `default` json serializer
is now a specific serializer which is available at `com.vincentbrison.openlibraries.android:dualcache-jsonserializer:3.1.1`.
- Internal optimizations for better performances.
- All the configuration is now done through `Builder`.
- Better access modifiers to fully hide internal classes.

Concurrent access
=================
Starting with version `2.2.1`, the cache is supporting concurrent access. You can perform whatever operations from multiple threads and the cache
takes care of the synchronization. More than that, this synchronization is optimized to block the threads only if needed, to get the best performances.
In fact, `put` and `get` are synchronized on each entry, and the cache itself is locked trough a `ReadWriteLock` for invalidation operations.

Setup
=====

- Ensure you can pull artifacts from Maven Central :
```gradle
repositories {
    mavenCentral()
}
```
- And add to your module gradle file :
```gradle
android {
    packagingOptions {
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/NOTICE'
    }
}

dependencies {
    compile 'com.vincentbrison.openlibraries.android:dualcache:3.1.1'

    //compile 'com.vincentbrison.openlibraries.android:dualcache-jsonserializer:3.1.1' // If you
    // want a ready to use json serializer
}
```

All the configuration of the cache is done when you are building the cache through its `Builder` class.

Basic examples
==============

Build your cache
---------------
 First of all, you need to build you cache, through the `Builder` class.
 1. A cache with a serializer for RAM and disk disable :

```Java
cache = new Builder<>(CACHE_NAME, TEST_APP_VERSION, AbstractVehicule.class)
    .enableLog()
    .useSerializerInRam(RAM_MAX_SIZE, new SerializerForTesting())
    .noDisk()
    .build();
```

 2. A cache with references in RAM and a default serializer on disk :

```Java
cache = new Builder<>(CACHE_NAME, TEST_APP_VERSION, AbstractVehicule.class)
    .enableLog()
    .useReferenceInRam(RAM_MAX_SIZE, new SizeOfVehiculeForTesting())
    .useSerializerInDisk(DISK_MAX_SIZE, true, new DualCacheTest.SerializerForTesting(), getContext())
    .build();
```
You can note that when you build the cache, you need to provide an `app version` number. When the cache
is loaded, if data exist with a inferior number, it will be invalidate. It can be extremely useful when
you update your app, and change your model, to avoid crashes. This feature is possible because the DiskLruCache of Jake Wharton
implemented this feature.

Put
---
To put an object into your cache, simply call `put` :

```Java
DummyClass object = new DummyClass();
object = cache.put("mykey", object);
```

Get
---
To get an object from your cache, simply call `get` :

```Java
DummyClass object = null;
object = cache.get("mykey");
```

Use cases
=========
 - Using default serialization on RAM and on disk can be very useful for caching network exchange of data.
 - Using references in RAM and serialization on disk can be very useful to cache bitmaps.

Javadoc
=======
The javadoc provided with this library is fully written and released on Maven.

Testing
=======
All the configurations of the cache are (almost) fully tested through automated tests. If you fork
this repo, you can launch them with the gradle command `connectedAndroidTest`.
You need to have a device connected since the tests will be run on every device connected to your computer.
An emulator or a [GenyMotion] instance is enough.
A report will be available at : `/{location of your fork}/lib/build/outputs/reports/androidTests/connected/index.html`

License
=======

    Copyright 2016 Vincent Brison.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
