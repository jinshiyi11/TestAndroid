package com.shuai.test.okhttp.data;

public class Const {
    public static final int INT_FALSE = 0;
    public static final int INT_TRUE = 1;

    /**
     * http头，内部使用，用于传递缓存策略信息，在发送网络请求前会删除
     */
    public static final String HEAD_CACHE_POLICY = "_cache_policy";
    public static final String EXCLUDE_KEYS = "excludeKeys";
    public static final String EXPIRE_TIME = "expireTime";
    public static final String USE_CACHE_AFTER_REQUEST = "useCacheAfterRequest";
    public static final String USE_CACHE_BEFORE_REQUEST = "useCacheBeforeRequest";
    public static final String ONLY_USE_CACHE = "onlyUseCache";

}
