package com.hitasoft.epiccacher;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor {

    private CacheManager mCacheManager;

    public CacheInterceptor(Context context) throws IOException {
        mCacheManager = new CacheManager(context);
    }

    public boolean isCacheHit(String key) {
        return mCacheManager.getCache().get(key) != null;
    }

    public CacheManager getCacheManager() {
        return mCacheManager;
    }

}