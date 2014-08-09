CAUTION
=======
 - This project is in alpha state. You should not used it except for experimental purposes.
 - This project is only supported for Gradle build system (if you use an IDE, only Android Studio is supported).

vb.openlibraries.android.dualcache
========================

This android library provide a cache with 2 layers, one in RAM in top of one on local storage.
This library will convert each object into a String to be able to measure the size in byte of each cached object.
You have to be aware that the default implementation of [LruCache] (http://developer.android.com/reference/android/util/LruCache.html) does not fulfill this feature.
This library includes the following features :
 - A RAM cache layer.
 - A disk cache layer ([based on the awesome work of Jake Wharton] (https://github.com/JakeWharton/DiskLruCache)).
 - Whatever object you choose to put in cache, its size is automatically calculated, to ensure a limited used space by the cache (with LRU policy).
 - You can choose to use only the RAM layer or the RAM layer with the disk layer, to save data among different executions for example.
 - Limited size of the RAM and the disk layer.
 
Setup
-----
 - Add to your repositories the following url :
 
   ```gradle
   maven {
       url 'https://github.com/vincentbrison/vb-maven/raw/master/release/'
   }
   ```
   and to your dependencies :
   
   ```gradle
     compile 'com.vb.openlibraries.android.dualcache.lib:dualcache:0.0.+@aar'
     compile 'com.android.support:support-v4:19.1.+'
     compile 'com.jakewharton:disklrucache:2.0.+'
     compile 'com.fasterxml.jackson.core:jackson-databind:2.4.+'

   ```
    The alpha releases will use 0.0.+, the beta releases 0.1.+, the stables releases 1.+.+.
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
  
Put
---
 - You can cache whatever object. Be aware that each object will be convert to String in cache, so do not use this cache with Bitmap for example.
 - Basic example :
 
 ```Java
 // Whatever object can be cached, polymorphism is fully supported.
 DualCache<DummyClass> dualCache = new DualCache<DummyClass>("myCache", 1, maxRamSize, maxDiskSize, DummyClass.class);
 DummyClass object = new DummyClass();
 cache.put("mykey", object);
 ```

    
Get
---
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

 
