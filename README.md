CAUTION
=======
 - This project is in alpha state. You should not used it except for experimental purposes.
 - This project is only supported for Gradle build system (if you use an IDE, only Android Studio is supported).

vb-android-library-cache
========================

This android library provide a cache with 2 layers, one in RAM in top of one on local storage.
The particularity of this cache is to provide an expiry date for the cached object.

Setup
-----
 - [Download the .aar](aar/vb-android-library-cache.aar).
 - Put it into your /libs folder.
 - Add to your repositories :
 
   ```gradle
   flatDir {
              dirs 'libs'
          }
   ```
   and to your dependencies :
   
   ```gradle
    compile (name:'vb-android-library-cache', ext:'aar')
   ```

 - If you want activate the log of this library :
 
  ```Java
   VBLibCacheLogUtils.enableLog();
  ```
 - You have to provide a Context to the cache. Please use the [application context] (http://developer.android.com/reference/android/content/Context.html#getApplicationContext())
 to avoid ugly memory leak : 
 
  ```Java
   VBLibCacheContextUtils.setContext(getApplicationContext());
  ```
  
 - You are good to go !
  
Put
---
 - You can only cache Serializable object.
 - You have to use a CacheWrapper object to cache an object. With the CacheWrapper you can set a expiry date to your object. If you set null as the expiry date, the object
 will remain in the cache until the end times.
 - Basic example :
 
 ```Java
 // Strings are serializable so they can be cached.
 CacheWrapper wrapper = new CacheWrapper("object10sec", date);
 CacheManager cache = CacheManager.getCacheManager("mycache");
 cache.put("object10sec", wrapper);
 ```

    
Get
---
 - Basic example :
 
 ```Java
 CacheManager cache = CacheManager.getCacheManager("mycache");
 String object = null;
 object = cache.get("object10sec", String.class);
  ```
 