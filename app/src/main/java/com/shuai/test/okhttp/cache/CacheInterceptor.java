package com.shuai.test.okhttp.cache;

import com.shuai.test.MyApplication;
import com.shuai.test.util.NetWorkUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OfflineCache;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author shuaiweican
 * @date 2019/3/4
 */
public class CacheInterceptor implements Interceptor {
    private OfflineCache mOfflineCache;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean netAvailable = NetWorkUtil.isNetAvailable(MyApplication.getContext());

        if (netAvailable) {
            request = request.newBuilder()
                    //网络可用 强制从网络获取数据
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();
        } else {
            request = request.newBuilder()
                    //网络不可用 从缓存获取
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = chain.proceed(request);
        if (netAvailable) {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    // 有网络时 设置缓存超时时间1个小时
                    .header("Cache-Control", "public, max-age=" + 60 * 60)
                    .build();
        } else {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    // 无网络时，设置超时为1周
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 7 * 24 * 60 * 60)
                    .build();
        }
        return response;
    }
}
