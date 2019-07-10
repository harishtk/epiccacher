package com.hitasoft.epiccacher;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor {

    private CacheManager mCacheManager;

    public boolean isCacheHit(String key) {
        return mCacheManager.getCache().get(key) != null;
    }
}