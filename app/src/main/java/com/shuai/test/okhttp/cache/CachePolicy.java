package com.shuai.test.okhttp.cache;

import android.text.TextUtils;

import com.shuai.test.okhttp.util.Util;

/**
 * 缓存策略
 */
public class CachePolicy {
    private static final String TAG = CachePolicy.class.getSimpleName();
    private String[] excludeKeys;
    private long expireTime;
    private boolean forceCache;

    private static final CachePolicy onlinePolicy = new CachePolicy();

    private static final CachePolicy cachePolicy = new CachePolicy(Long.MAX_VALUE);

    public static CachePolicy online() {
        return onlinePolicy;
    }

    public static CachePolicy cache() {
        return cachePolicy;
    }

    public static CachePolicy cache(long expireTime) {
        return new CachePolicy(expireTime);
    }

    public CachePolicy forceCache() {
        return copy().setForceCache(true);
    }

    public CachePolicy forceOnline() {
        return copy().setForceCache(false);
    }

    private CachePolicy copy() {
        CachePolicy cachePolicy = new CachePolicy();
        cachePolicy.excludeKeys = excludeKeys;
        cachePolicy.expireTime = expireTime;
        cachePolicy.forceCache = forceCache;
        return cachePolicy;
    }

    public CachePolicy() {

    }

    public CachePolicy(long expireTime) {
        this(null, expireTime, false);
    }

    public CachePolicy(String[] excludeKeys, long expireTime, boolean forceCache) {
        this.excludeKeys = excludeKeys;
        this.expireTime = expireTime;
        this.forceCache = forceCache;
    }

    public String[] getExcludeKeys() {
        return excludeKeys;
    }

    public CachePolicy setExcludeKeys(String[] excludeKeys) {
        this.excludeKeys = excludeKeys;
        return this;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public CachePolicy setExpireTime(long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public boolean isForceCache() {
        return forceCache;
    }

    public CachePolicy setForceCache(boolean forceCache) {
        this.forceCache = forceCache;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (excludeKeys != null) {
            sb.append(CacheConst.EXCLUDE_KEYS + "=" + TextUtils.join(",", excludeKeys)).append(";");
        }
        sb.append(CacheConst.EXPIRE_TIME + "=" + expireTime).append(";");
        sb.append(CacheConst.QUERY_CACHE + "=" + (forceCache ? "1" : "0")).append(";");
        return sb.toString();
    }

    public static CachePolicy parse(String cachePolicyString) {
        if (cachePolicyString == null) {
            return null;
        }

        CachePolicy result = null;
        try {
            CachePolicy cachePolicy = new CachePolicy();
            String[] policies = cachePolicyString.split(";");
            for (int i = 0; i < policies.length; i++) {
                int sep = policies[i].indexOf('=');
                String key = null;
                String value = null;

                if (sep >= 0) {
                    key = policies[i].substring(0, sep);
                    value = policies[i].substring(sep + 1);
                    if (key.equals(CacheConst.EXCLUDE_KEYS)) {
                        cachePolicy.setExcludeKeys(value.split(","));
                    } else if (key.equals(CacheConst.EXPIRE_TIME)) {
                        cachePolicy.setExpireTime(Long.parseLong(value));
                    } else if (key.equals(CacheConst.QUERY_CACHE)) {
                        cachePolicy.setForceCache(Integer.parseInt(value) == CacheConst.INT_TRUE);
                    }
                } else {
                    continue;
                }
            }
            result = cachePolicy;
        } catch (Exception e) {
            Util.e(TAG, e.getMessage(), e);
        }

        return result;
    }
}
