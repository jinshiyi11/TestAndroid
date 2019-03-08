package com.shuai.test.okhttp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCache {
    String[] excludeKeys() default {"token", "signkey"};

    /**
     * 缓存数据有效期，单位毫秒
     *
     * @return 缓存数据有效期
     */
    long expireTime() default Long.MAX_VALUE;

    boolean useAfterRequest() default true;

    boolean useBeforeRequest() default false;

    boolean onlyUseCache() default false;
}
