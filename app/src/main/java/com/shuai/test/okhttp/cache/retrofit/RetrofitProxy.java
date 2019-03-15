package com.shuai.test.okhttp.cache.retrofit;

import com.shuai.test.okhttp.Repo;
import com.shuai.test.okhttp.cache.CachePolicy;
import com.shuai.test.okhttp.cache.CacheResult;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RetrofitProxy<T> {
    private static final String TAG = RetrofitProxy.class.getSimpleName();
    private Object mOriginalObject;

    private RetrofitProxy(Object originalObject) {
        mOriginalObject = originalObject;
    }

    public static <T> T create(final Class<T> service, Object object) {
        final RetrofitProxy cacheProxy = new RetrofitProxy(object);
        Class<?> clazz = object.getClass();
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return cacheProxy.invoke(proxy, method, args);
                    }
                });
    }

    private Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(!isCacheApi(method,args)){
            return (T) method.invoke(mOriginalObject, args);
        }else{
            CachePolicy cachePolicy = CachePolicy.parse((String) args[args.length-1]);
            Object[] onlineArgs = args.clone();
            onlineArgs[onlineArgs.length-1] = cachePolicy.forceOnline().toString();
            Observable online = ((Observable) method.invoke(mOriginalObject, onlineArgs))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Object[] cacheArgs = args.clone();
            cacheArgs[cacheArgs.length-1] = cachePolicy.forceCache().toString();
            final Observable cache = ((Observable) method.invoke(mOriginalObject, cacheArgs))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            return online.onErrorResumeNext(new Func1<Throwable, Observable>() {
                @Override
                public Observable call(Throwable throwable) {
                    return cache.onErrorResumeNext(Observable.<CacheResult<List<Repo>>>error(throwable));
                }
            });
        }
    }

    private boolean isCacheApi(Method method, Object[] args){
        boolean result = false;
        return true;
    }
}
