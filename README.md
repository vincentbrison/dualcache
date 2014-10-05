Android dualcache
========================
This android library provide a cache with 2 layers, one in RAM in top of one on local storage.
This library is highly configurable :

| Configurations | Disk : `default serializer` (json) | Disk : `Custom serializer` | Disk : `disable` |
| -------------- | ---------------------------------- | -------------------------- | ---------------- |
| Ram : `default serializer` (json) | YES | YES | YES |
| Ram : `custom serializer` | YES | YES | YES |
| Ram : `References` | YES | YES | YES |
| Ram : `disable | YES | YES | NO |

 - `Default serializer` : the objects stored in cache will be serialized in json through internal json mapper.
 - `Custom serializer` : the object stored in cache will be serialized through a serializer provided by yourself.
 - `References` : the objects stored in Ram are cached through there references (no serialization is done).
 - `Disable` : the corresponding layer (Ram or disk) is disable.

If you work with `custom serializer` or `references` you will have to provide (through an interface) the
way of compute the size of cached objects, to be able to correctly execute the [LRU policy] (http://en.wikipedia.org/wiki/Cache_algorithms).

The Philosophy behind this library
----------------------------------
When you want to use a [cache] (http://en.wikipedia.org/wiki/Cache_\(computing\)) on Android today, you have two possibilities. You whether use :
 - The [LruCache] (http://developer.android.com/reference/android/util/LruCache.html) included into the Android SDK.
 - The [DiskLruCache] (https://github.com/JakeWharton/DiskLruCache) of Jake Wharton.

The thing is the first one only works in RAM, and the second one only on disk (internal memory of th phone). So you need to choose
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


Setup
-----
 - Add to your repositories the following url :
 
   ```gradle
   maven {
       url 'https://oss.sonatype.org/content/groups/public/'
   }
   ```
   and to your dependencies :
   
   ```gradle
     compile 'com.vincentbrison.openlibraries.android:dualcache:1.0.0'
     compile 'com.android.support:support-v4:19.1.+'
     compile 'com.jakewharton:disklrucache:2.0.+'
     compile 'com.fasterxml.jackson.core:jackson-databind:2.4.+'

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
--------------
 - You can cache whatever object. Be aware that each object will be convert to String in cache, so do not use this cache with Bitmap for example.
 - Basic example :
 
 ```Java
 // Whatever object can be cached, polymorphism is fully supported.
 DualCache<DummyClass> dualCache = new DualCache<DummyClass>("myCache", 1, maxRamSize, maxDiskSize, DummyClass.class);
 DummyClass object = new DummyClass();
 cache.put("mykey", object);
 ```


 - Basic example :
 
 ```Java
 DummyClass object = null;
 object = cache.get("mykey");
  ```
  
License
=======

    Copyright 2013 Vincent Brison.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 
