package com.shuai.test.okhttp.cache.retrofit;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 缓存接口标记，使用缓存的接口需要添加该注解
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface EnableCache {
}
