package com.shuai.test.okhttp.cache;

/**
 * cache常量
 */
public class CacheConst {
    /**
     * 内部使用，用于传递缓存策略信息，在发送网络请求前会删除，放在query参数中
     */
    public static final String CACHE_POLICY = "_cache_policy";

    /**
     * 不参与缓存key计算的参数列表，使用逗号分隔
     */
    public static final String EXCLUDE_KEYS = "excludeKeys";

    /**
     * 缓存过期时间
     */
    public static final String EXPIRE_TIME = "expireTime";

    /**
     * 查询缓存还是走网络请求
     */
    public static final String CACHE_TYPE = "cacheType";

}
