package com.shuai.test.okhttp.data;


import android.text.TextUtils;

import com.shuai.test.okhttp.util.Util;

import java.util.Arrays;

public class CachePolicy {
    private static final String TAG = CachePolicy.class.getSimpleName();
    private String[] excludeKeys;
    private long expireTime;
    private boolean useAfterRequest;
    private boolean useBeforeRequest;
    private boolean onlyUseCache;

    public CachePolicy() {

    }

    public CachePolicy(String[] excludeKeys, long expireTime, boolean useAfterRequest, boolean useBeforeRequest, boolean onlyUseCache) {
        this.excludeKeys = excludeKeys;
        this.expireTime = expireTime;
        this.useAfterRequest = useAfterRequest;
        this.useBeforeRequest = useBeforeRequest;
        this.onlyUseCache = onlyUseCache;
    }

    public String[] getExcludeKeys() {
        return excludeKeys;
    }

    public void setExcludeKeys(String[] excludeKeys) {
        this.excludeKeys = excludeKeys;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isUseAfterRequest() {
        return useAfterRequest;
    }

    public void setUseAfterRequest(boolean useAfterRequest) {
        this.useAfterRequest = useAfterRequest;
    }

    public boolean isUseBeforeRequest() {
        return useBeforeRequest;
    }

    public void setUseBeforeRequest(boolean useBeforeRequest) {
        this.useBeforeRequest = useBeforeRequest;
    }

    public boolean isOnlyUseCache() {
        return onlyUseCache;
    }

    public void setOnlyUseCache(boolean onlyUseCache) {
        this.onlyUseCache = onlyUseCache;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (excludeKeys != null) {
            sb.append("excludeKeys=" + TextUtils.join(",", excludeKeys)).append(";");
        }
        sb.append("expireTime=" + expireTime).append(";");
        sb.append("useAfterRequest=" + (useAfterRequest ? "1" : "0")).append(";");
        sb.append("useBeforeRequest=" + (useBeforeRequest ? "1" : "0")).append(";");
        sb.append("onlyUseCache=" + (onlyUseCache ? "1" : "0")).append(";");
        return sb.toString();
    }

    public static CachePolicy parse(String header) {
        if (header == null) {
            return null;
        }

        CachePolicy result = null;
        try {
            CachePolicy cachePolicy = new CachePolicy();
            String[] policies = header.split(";");
            for (int i = 0; i < policies.length; i++) {
                int sep = policies[i].indexOf('=');
                String key = null;
                String value = null;

                if (sep >= 0) {
                    key = policies[i].substring(0, sep);
                    value = policies[i].substring(sep + 1);
                    if (key.equals(Const.EXCLUDE_KEYS)) {
                        cachePolicy.setExcludeKeys(value.split(","));
                    } else if (key.equals(Const.EXPIRE_TIME)) {
                        cachePolicy.setExpireTime(Long.parseLong(value));
                    } else if (key.equals(Const.USE_CACHE_AFTER_REQUEST)) {
                        cachePolicy.setUseAfterRequest(Integer.parseInt(value) == Const.INT_TRUE);
                    } else if (key.equals(Const.USE_CACHE_BEFORE_REQUEST)) {
                        cachePolicy.setUseBeforeRequest(Integer.parseInt(value) == Const.INT_TRUE);
                    } else if (key.equals(Const.ONLY_USE_CACHE)) {
                        cachePolicy.setOnlyUseCache(Integer.parseInt(value) == Const.INT_TRUE);
                    }
                } else {
                    continue;
                }
            }
            result = cachePolicy;
        } catch (Exception e) {
            Util.logE(TAG, e.getMessage(), e);
        }

        return result;
    }
}
