package com.hitasoft.epiccacher;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CacheManager {
   public static final String TAG = CacheManager.class.getSimpleName();

   private DiskCache mCache;
   private DiskLruCache mDiskLruCache;
   private final Context mContext;

   public CacheManager(@NonNull Context context) throws IOException {
      this.mContext = context;
      setUp();
      mCache = DiskCache.getInstanceUsingDoubleLocking(mDiskLruCache);
   }

   private void setUp() throws IOException {
      File cacheInFiles = mContext.getCacheDir();
      int version = BuildConfig.VERSION_CODE;

      int KB = 1024;
      int MB = 1024 * KB;
      int cahceSize = 400 * MB;

      mDiskLruCache = DiskLruCache.open(cacheInFiles, version, 1, cahceSize);
   }

   public DiskCache getCache() {
      return mCache;
   }

   public static class DiskCache implements Cache<String, String> {

      private static DiskLruCache mDiskLruCache;
      private static DiskCache instance = null;

      public static DiskCache getInstanceUsingDoubleLocking(DiskLruCache diskLruCache) {
         mDiskLruCache = diskLruCache;
         if (instance == null) {
            synchronized (DiskCache.class) {
               if (instance == null) {
                  instance = new DiskCache();
               }
            }
         }
         return instance;
      }

      @Override
      public synchronized void put(String key, String value) {
         try {
            if (mDiskLruCache != null) {
               DiskLruCache.Editor editor = mDiskLruCache.edit(getMd5Hash(key));
               if (editor != null) {
                  editor.set(0, value);
                  editor.commit();
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      @Override
      public synchronized String get(String key) {
         try {
            if (mDiskLruCache != null) {
               DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getMd5Hash(key));
               if (snapshot == null) return null;
               return snapshot.getString(0);
            }

         } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
         }
         return null;
      }

      @Override
      public String remove(String key) {
         String it = null;
         try {
            if (mDiskLruCache != null) {
               DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getMd5Hash(key));
               if (snapshot != null) it = snapshot.getString(0);
               return it;
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
         return it;
      }

      @Override
      public void clear() {
         try {
            if (mDiskLruCache != null) {
               mDiskLruCache.delete();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      static String getMd5Hash(String input) {
         try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
               md5 = "0" + md5;
            return md5;
         } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
         }
      }
   }

   public interface Cache<K, V> {

      void put(K key, V value);

      V get(K key);

      V remove(K key);

      void clear();
   }
}