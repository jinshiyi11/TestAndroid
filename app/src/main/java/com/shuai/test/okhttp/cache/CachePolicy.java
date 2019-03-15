package com.shuai.test.okhttp.cache;

import android.text.TextUtils;

import com.shuai.test.okhttp.util.Util;


/**
 * 缓存策略
 */
public class CachePolicy {
    private static final String TAG = CachePolicy.class.getSimpleName();

    /**
     * 先请求网络，请求网络失败再查询缓存
     */
    public static final int FIRST_ONLINE = 1;

    /**
     * 先查询缓存，查询缓存失败再请求网络
     */
    public static final int FIRST_CACHE = 2;

    /**
     * 只请求网络，不用缓存
     */
    public static final int FORCE_ONLINE = 3;

    /**
     * 只用缓存，不请求网络
     */
    public static final int FORCE_CACHE = 4;

    /**
     * 缓存策略
     */
    private int mType;

    /**
     * 不参与缓存key计算的参数列表，使用逗号分隔
     */
    private String[] excludeKeys;

    /**
     * 缓存过期时间
     */
    private long expireTime;

    public CachePolicy forceCache() {
        return copy().setType(FORCE_CACHE);
    }

    public CachePolicy forceOnline() {
        return copy().setType(FORCE_ONLINE);
    }

    private CachePolicy copy() {
        CachePolicy cachePolicy = new CachePolicy();
        cachePolicy.mType = mType;
        cachePolicy.excludeKeys = excludeKeys;
        cachePolicy.expireTime = expireTime;
        return cachePolicy;
    }

    private CachePolicy() {
    }

    public CachePolicy(int type, String[] excludeKeys, long expireTime) {
        this.mType = type;
        this.excludeKeys = excludeKeys;
        this.expireTime = expireTime;
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

    public int getType() {
        return mType;
    }

    public CachePolicy setType(int type) {
        this.mType = type;
        return this;
    }

    public boolean isForceOnline() {
        return mType == FORCE_ONLINE;
    }

    public boolean isForceCache() {
        return mType == FORCE_CACHE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(CacheConst.CACHE_POLICY + ";");
        if (excludeKeys != null) {
            sb.append(CacheConst.EXCLUDE_KEYS + "=" + TextUtils.join(",", excludeKeys)).append(";");
        }
        sb.append(CacheConst.EXPIRE_TIME + "=" + expireTime).append(";");
        sb.append(CacheConst.CACHE_TYPE + "=" + mType).append(";");
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
                    } else if (key.equals(CacheConst.CACHE_TYPE)) {
                        cachePolicy.setType(Integer.parseInt(value));
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
