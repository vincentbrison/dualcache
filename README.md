Android dualcache
=================

[![Build Status](https://travis-ci.org/vincentbrison/android-easy-cache.svg?branch=dev_travis)](https://travis-ci.org/vincentbrison/android-easy-cache)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--easy--cache-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1377)

This android library provide a cache with 2 layers, one in RAM in top of one on local storage.
This library is highly configurable :

| Configurations | Disk : `default serializer` (json) | Disk : `Custom serializer` | Disk : `disable` |
| -------------- | ---------------------------------- | -------------------------- | ---------------- |
| Ram : `default serializer` (json) | YES | YES | YES |
| Ram : `custom serializer` | YES | YES | YES |
| Ram : `References` | YES | YES | YES |
| Ram : `disable` | YES | YES | NO |

 - `Default serializer` : the objects stored in cache will be serialized in json through internal json mapper.
 - `Custom serializer` : the object stored in cache will be serialized through a serializer provided by yourself.
 - `References` : the objects stored in Ram are cached through there references (no serialization is done).
 - `Disable` : the corresponding layer (Ram or disk) is disable.

If you work with `custom serializer` or `references` you will have to provide (through an interface) the
way of compute the size of cached objects, to be able to correctly execute the [LRU policy] (http://en.wikipedia.org/wiki/Cache_algorithms).

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

2.2.1 is adding concurrent access
=================================
Starting with version `2.2.1`, the cache is now supporting concurrent access. You can perform whatever operations from multiple threads and the cache
takes care of the synchronization. More than that, this synchronization is optimized to block the threads only if needed, to get the best performances.
In fact, `put` and `get` are synchronized on each entry, and the cache itself is locked trough a `ReadWriteLock` for invalidation operations.

Setup
=====

 - Add to your repositories the following url :
 
   ```gradle
   maven {
       url 'https://oss.sonatype.org/content/groups/public/'
   }
   ```
   and to your module gradle file :
   
   ```gradle
     android {
       packagingOptions {
          exclude 'META-INF/LICENSE'
          exclude 'META-INF/NOTICE'
       }
     }
     dependencies {
       compile ('com.vincentbrison.openlibraries.android:dualcache:2.2.1@jar') {
         transitive = true
       }
     }
   ```

 - If you want activate the log of this library :
 
  ```Java
   DualCacheLogUtils.enableLog();
  ```
 - You have to provide a Context to the cache. Please use the [application context] (http://developer.android.com/reference/android/content/Context.html#getApplicationContext())
 to avoid ugly memory leak : 
 
  ```Java
  DualCacheContextUtils.setContext(getApplicationContext());
  ```
  
 - You are good to go !
  
Basic examples
==============

Build your cache
---------------
 First of all, you need to build you cache. Since the cache object is highly configurable (have a lot of parameters)
 I use the [builder pattern] (http://en.wikipedia.org/wiki/Builder_pattern).
 You have to build your cache through the `DualCacheBuilder` class.
 1. A cache with default serializer in RAM and disk disable :
 
 ```Java
 DualCache<AbstractVehicule> cache = new DualCacheBuilder<AbstractVehicule>(CACHE_NAME, TEST_APP_VERSION, AbstractVehicule.class)
                                                     .useDefaultSerializerInRam(RAM_MAX_SIZE)
                                                     .noDisk();
 ```

 2. A cache with references in RAM and a default serializer on disk :

 ```Java
 DualCache<AbstractVehicule> cache = new DualCacheBuilder<AbstractVehicule>(CACHE_NAME, TEST_APP_VERSION, AbstractVehicule.class)
                                                     .useReferenceInRam(RAM_MAX_SIZE, new SizeOfVehiculeForTesting())
                                                     .useDefaultSerializerInDisk(DISK_MAX_SIZE, true);
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
 - Using default serialization on RAM + disk can be very useful to cache network exchange of data.
 - Using references in RAM + serialization on disk can be very useful to cache bitmaps.

Javadoc
=======
The javadoc provided with this library is fully written for every public class, methods, fields, feel
free to use it :).

Testing
=======
All the configurations of the cache are (almost) fully tested through automated tests. If you fork
this repo, you can launch them with the gradle command `connectedAndroidTest`.
You need to have a device connected since the tests will be run on every device connected to your computer.
I recommend the use of easy to use [GenyMotion] (http://www.genymotion.com/) VMs to do these tests.
A report will be available at : `/{location of your fork}/lib/build/outputs/reports/androidTests/connected/index.html`

License
=======

    Copyright 2015 Vincent Brison.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
