package com.shuai.test.okhttp.cache;

/**
 * 缓存结果包装类，包含字段说明数据是来自缓存还是来自网络
 *
 * @param <T> 实际的数据类型
 */
public class CacheResult<T> {
    /**
     * 数据是来自缓存还是来自网络
     */
    private boolean mIsFromCache;

    /**
     * 实际的数据
     */
    private T mData;

    public CacheResult(boolean isFromCache, T data) {
        this.mIsFromCache = isFromCache;
        this.mData = data;
    }

    public boolean isFromCache() {
        return mIsFromCache;
    }

    public void setFromCache(boolean fromCache) {
        mIsFromCache = fromCache;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        this.mData = data;
    }
}
