package com.shuai.test.okhttp.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import com.shuai.test.okhttp.data.CachePolicy;
import com.shuai.test.okhttp.data.Const;
import com.shuai.test.okhttp.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OfflineCache;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.adapter.rxjava.HttpException;

/**
 * 自定义OkHttp缓存Interceptor
 */
public class CacheInterceptor implements Interceptor {
    private static final String TAG = CacheInterceptor.class.getSimpleName();
    private static final String CACHE_DIR = "network_cache";
    private Context mAppContext;
    private OfflineCache mCache;

    public CacheInterceptor(Context context, long maxSize) {
        mAppContext = context.getApplicationContext();
        mCache = new OfflineCache(new File(context.getCacheDir() + "/" + CACHE_DIR), maxSize, getAppVersion(mAppContext));
    }

    private static int getAppVersion(Context context) {
        int versionCode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Throwable ignored) {
        }
        return versionCode;
    }

//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//        Response response = null;
//        try {
//            response = doIntercept(chain, request);
//        } catch (Exception e) {
//            //缓存出现异常，跳过缓存逻辑
//            Util.logE(TAG, e.getMessage(), e);
//        }
//
//        if (response == null) {
//            response = chain.proceed(request);
//        }
//
//        return response;
//    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //TODO:网络请求前查询缓存
//        boolean netAvailable = NetWorkUtil.isNetAvailable(MyApplication.getContext());

//        if (netAvailable) {
//            request = request.newBuilder()
//                    //网络可用 强制从网络获取数据
//                    .cacheControl(CacheControl.FORCE_NETWORK)
//                    .build();
//        } else {
//            request = request.newBuilder()
//                    //网络不可用 从缓存获取
//                    .cacheControl(CacheControl.FORCE_CACHE)
//                    .build();
//        }

//        String cachePolicyHeader = request.header(Const.HEAD_CACHE_POLICY);
//        if (cachePolicyHeader == null) {
//            //不使用缓存
//            return chain.proceed(request);
//        }

        CachePolicy cachePolicy = null;
        if (request.tag() instanceof CachePolicy) {
            cachePolicy = (CachePolicy) request.tag();
        }

        if (cachePolicy == null) {
            //不使用缓存
            return chain.proceed(request);
        }

        request = request.newBuilder().removeHeader(Const.HEAD_CACHE_POLICY).build();
        Response response = null;
        //在发送网络请求之前，取出自定义head数据，并删除这些head
        boolean useCacheAfterRequest = cachePolicy.isUseAfterRequest();
        boolean needCacheNetworkResponse = cachePolicy.isUseBeforeRequest() || cachePolicy.isUseAfterRequest();
        String cacheKey = getCacheKey(request, cachePolicy);
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            if (useCacheAfterRequest && isNetworkUnavailable(e)) {
                response = getCache(cacheKey, request, cachePolicy);
                if (response == null) {
                    //不存在缓存，继续透传异常
                    throw e;
                } else {
                    return response;
                }
            } else {
                //非网络原因或者不需要使用缓存，继续透传异常
                throw e;
            }
        }

        if (response != null && response.isSuccessful()) {
            //网络请求成功，缓存数据
            if (needCacheNetworkResponse) {
                updateCache(cacheKey, response);
            }
        } else if (useCacheAfterRequest && isServerException(response)) {
            //服务异常，尝试缓存
            Response cacheResponse = getCache(cacheKey, request, cachePolicy);
            if (cacheResponse != null) {
                //存在缓存
                response = cacheResponse;
            }
        }

//        if (netAvailable) {
//            response = response.newBuilder()
//                    .removeHeader("Pragma")
//                    // 有网络时 设置缓存超时时间1个小时
//                    .header("Cache-Control", "public, max-age=" + 60 * 60)
//                    .build();
//        } else {
//            response = response.newBuilder()
//                    .removeHeader("Pragma")
//                    // 无网络时，设置超时为1周
//                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 7 * 24 * 60 * 60)
//                    .build();
//        }
        return response;
    }

    private boolean isValid(Response cacheResponse, CachePolicy cachePolicy) {
        if (cacheResponse == null) {
            return false;
        }
        //TODO:测试，都是用的System.currentTimeMillis()吗？
        return System.currentTimeMillis() - cacheResponse.receivedResponseAtMillis() <= cachePolicy.getExpireTime();
    }

    private boolean isNetworkUnavailable(Exception e) {
        return e instanceof HttpException
                || e instanceof ConnectException
                || e instanceof SocketTimeoutException;
    }

    private boolean cacheNetworkResponse(Request request) {
        return Util.isTrue(request.header(Const.USE_CACHE_AFTER_REQUEST))
                || Util.isTrue(request.header(Const.USE_CACHE_BEFORE_REQUEST));
    }

    private Response getCache(String cacheKey, Request request, CachePolicy cachePolicy) {
        Response response = null;
        try {
            response = mCache.get(cacheKey, request);
            //缓存存在，检查有效性
            if (response != null && !isValid(response, cachePolicy)) {
                response = null;
            }
        } catch (Exception e) {
            Util.logE(TAG, e.toString(), e);
        }
        return response;
    }

    private void updateCache(String cacheKey, Response response) {
        try {
//            Response cachedResponse = mCache.get(cacheKey, response.request());
            mCache.put(cacheKey, response);
            //TODO:要不要update
//            if (cachedResponse != null) {
//                mCache.update(cachedResponse, response);
//            } else {
//                mCache.put(cacheKey, response);
//            }
        } catch (Exception e) {
            Util.logE(TAG, e.toString(), e);
        }
    }

    private boolean isServerException(Response response) {
        boolean result = false;
        if (response != null) {
            int code = response.code();
            result = code >= 500 && code < 600;
        }
        return result;
    }

    private String getCacheKey(Request request, CachePolicy cachePolicy) {
        String result = null;
        try {
            TreeMap<String, String> sortedMap = new TreeMap<>();
            HttpUrl url = request.url();
            int querySize = url.querySize();
            for (int i = 0; i < querySize; i++) {
                sortedMap.put(url.queryParameterName(i), url.queryParameterValue(i));
            }

            if (request.method().equals("POST")) {
                //TODO：解析测试
                RequestBody body = request.body();
                MediaType contentType = body.contentType();
                //Content-Type: application/x-www-form-urlencoded
                if (contentType != null
                        && "application".equalsIgnoreCase(contentType.type())
                        && "x-www-form-urlencoded".equalsIgnoreCase(contentType.subtype())) {
                    Buffer requestBuffer = new Buffer();
                    body.writeTo(requestBuffer);
                    String requestBody = requestBuffer.readString(contentType.charset(Charset.forName("UTF-8")));
                    decodeParms(requestBody, sortedMap);
                } else {
                    //不支持其它类型的解析
                    return null;
                }
            }

            HttpUrl.Builder builder = new HttpUrl.Builder()
                    .scheme(url.scheme())
                    .encodedUsername(url.encodedUsername())
                    .encodedPassword(url.encodedPassword())
                    .host(url.host())
                    .port(url.port() != -1 ? url.port() : HttpUrl.defaultPort(url.scheme()))
                    .addEncodedPathSegments(url.encodedPath());

            //TODO：是否有序
            for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                builder.addQueryParameter(key, value);
            }
            result = builder.toString();
            Util.logD(TAG, "cacheUrl:" + result);

            result = okhttp3.internal.Util.md5Hex(result);
            Util.logD(TAG, "cacheKey:" + result);
        } catch (Exception e) {
            Util.logE(TAG, e.getMessage(), e);
        }

        Util.logD(TAG, "cacheKey:" + result);
        return result;
    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g.
     * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given Map.
     */
    private void decodeParms(String parms, Map<String, String> p) throws UnsupportedEncodingException {
        if (parms == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(parms, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            String key = null;
            String value = null;

            if (sep >= 0) {
                key = URLDecoder.decode(e.substring(0, sep), "UTF-8").trim();
                value = URLDecoder.decode(e.substring(sep + 1), "UTF-8");
            } else {
                key = URLDecoder.decode(e, "UTF-8").trim();
                value = "";
            }

            String values = p.get(key);
            if (values == null) {
                p.put(key, values);
            } else {
                p.put(key, values + value);
            }
        }
    }

}
