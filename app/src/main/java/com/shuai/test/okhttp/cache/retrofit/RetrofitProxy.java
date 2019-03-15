package com.shuai.test.okhttp.cache.retrofit;

import com.shuai.test.okhttp.cache.CacheConst;
import com.shuai.test.okhttp.cache.CachePolicy;
import com.shuai.test.okhttp.cache.CacheResult;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * retrofit代理
 * 在okHttp和业务层之间，使支持缓存的接口调用更方便
 *
 * @param <T>
 */
public class RetrofitProxy<T> {
    private static final String TAG = RetrofitProxy.class.getSimpleName();
    private Object mOriginalObject;

    private RetrofitProxy(Object originalObject) {
        mOriginalObject = originalObject;
    }

    public static <T> T create(final Class<T> service, Object object) {
        final RetrofitProxy cacheProxy = new RetrofitProxy(object);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return cacheProxy.invoke(proxy, method, args);
                    }
                });
    }

    private Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!isCacheApi(method)) {
            //非缓存接口，走原逻辑
            return (T) method.invoke(mOriginalObject, args);
        } else {
            //缓存接口，处理缓存策略以及包装返回值为CacheResult
            int policyArgIndex = getCachePolicyIndex(args);
            CachePolicy cachePolicy = CachePolicy.parse((String) args[policyArgIndex]);

            Object[] onlineArgs = args.clone();
            onlineArgs[policyArgIndex] = cachePolicy.forceOnline().toString();
            final Observable<CacheResult> online = ((Observable<CacheResult>) method.invoke(mOriginalObject, onlineArgs))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<CacheResult, CacheResult>() {
                        @Override
                        public CacheResult call(CacheResult cacheResult) {
                            //更新isFromCache字段，之前写进去的是假的
                            return new CacheResult(false, cacheResult.getData());
                        }
                    });

            Object[] cacheArgs = args.clone();
            cacheArgs[policyArgIndex] = cachePolicy.forceCache().toString();
            final Observable<CacheResult> cache = ((Observable<CacheResult>) method.invoke(mOriginalObject, cacheArgs))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).map(new Func1<CacheResult, CacheResult>() {
                        @Override
                        public CacheResult call(CacheResult cacheResult) {
                            //更新isFromCache字段，之前写进去的是假的
                            return new CacheResult(true, cacheResult.getData());
                        }
                    });

            if (cachePolicy.isForceOnline()) {
                return online;
            } else if (cachePolicy.isForceCache()) {
                return cache;
            } else if (cachePolicy.getType() == CachePolicy.FIRST_ONLINE) {
                return online.onErrorResumeNext(new Func1<Throwable, Observable<CacheResult>>() {
                    @Override
                    public Observable call(Throwable throwable) {
                        //如果缓存不存在，返回网络请求时的异常
                        return cache.onErrorResumeNext(Observable.<CacheResult>error(throwable));
                    }
                });
            } else if (cachePolicy.getType() == CachePolicy.FIRST_CACHE) {
                return cache.onErrorResumeNext(new Func1<Throwable, Observable<CacheResult>>() {
                    @Override
                    public Observable call(Throwable throwable) {
                        //缓存不存在，继续网络请求
                        return online;
                    }
                });
            } else {
                throw new IllegalArgumentException("illegal cachePolicy type");
            }
        }
    }

    /**
     * 该请求是否需要使用缓存
     *
     * @param method Method对象
     * @return true-使用缓存，false-不使用
     */
    private boolean isCacheApi(Method method) {
        return method.getAnnotation(EnableCache.class) != null;
    }

    /**
     * 获取cachePolicy参数的索引
     *
     * @param args 方法调用的所有参数
     * @return cachePolicy参数的索引
     */
    private int getCachePolicyIndex(Object[] args) {
        int index = -1;
        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i] instanceof String && ((String) args[i]).contains(CacheConst.CACHE_POLICY)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new IllegalArgumentException("cache policy argument not found");
        }
        return index;
    }
}
