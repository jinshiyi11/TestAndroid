package com.shuai.test.okhttp.cache;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.shuai.test.okhttp.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 自定义OkHttp缓存Interceptor
 */
public class CacheInterceptor implements Interceptor {
    private static final String TAG = CacheInterceptor.class.getSimpleName();
    private static final String CACHE_DIR = "network_cache";
    private Context mAppContext;
    private OfflineCache mCache;

    /**
     * 构造函数
     * @param context context对象
     * @param maxSize 缓存大小，单位字节
     */
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

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String cachePolicyString = request.url().queryParameter(CacheConst.CACHE_POLICY);

        CachePolicy cachePolicy = null;
        if (cachePolicyString == null || (cachePolicy = CachePolicy.parse(cachePolicyString)) == null) {
            //不使用缓存
            return chain.proceed(request);
        }

        //设置了缓存策略
        //在发送网络请求之前，取出自定义cache数据，并删除
        HttpUrl url = request.url().newBuilder().removeAllQueryParameters(CacheConst.CACHE_POLICY).build();
        request = request.newBuilder().url(url).build();
        Response response = null;
        String cacheKey = getCacheKey(request, cachePolicy);
        //只使用缓存或网络请求前使用缓存
        if (cachePolicy.isForceCache()) {
            response = getCache(cacheKey, request, cachePolicy);
            if (response != null) {
                Util.d(TAG, "hit cache:" + url.toString());
                return response;
            } else {
                Util.d(TAG, "no cache:" + url.toString());
                throw new NoCacheException();
            }
        }

        //发送网络请求
        response = chain.proceed(request);
        if (response != null && response.isSuccessful()) {
            //网络请求成功，缓存数据
            updateCache(cacheKey, response);
        }

        return response;
    }

    private boolean isValid(Response cacheResponse, CachePolicy cachePolicy) {
        if (cacheResponse == null) {
            return false;
        }
        //TODO:测试，都是用的System.currentTimeMillis()吗？
        return System.currentTimeMillis() - cacheResponse.receivedResponseAtMillis() <= cachePolicy.getExpireTime();
    }

    private Response getCache(String cacheKey, Request request, CachePolicy cachePolicy) {
        Response response = null;
        try {
            response = mCache.get(cacheKey, request);
            //缓存存在，检查有效性
            if (response != null && !isValid(response, cachePolicy)) {
                response = null;
                //TODO:移除过期缓存
            }
        } catch (Exception e) {
            Util.e(TAG, e.toString(), e);
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
            Util.e(TAG, e.toString(), e);
        }
    }

    private String getCacheKey(Request request, CachePolicy cachePolicy) {
        String result = null;
        try {
            //有序map
            TreeMap<String, String> sortedMap = new TreeMap<>();
            HttpUrl url = request.url();
            int querySize = url.querySize();
            for (int i = 0; i < querySize; i++) {
                sortedMap.put(url.queryParameterName(i), url.queryParameterValue(i));
            }

            if (request.method().equals("POST")) {
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
                    Util.d(TAG, "unsupport request,type:" + contentType + ",url:" + url);
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

            for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                builder.addQueryParameter(key, value);
            }
            String cacheUrl = builder.toString();
            result = CacheUtil.md5Hex(cacheUrl);
            Util.d(TAG, "cacheUrl:" + cacheUrl + ",cacheKey:" + result);
        } catch (Exception e) {
            Util.e(TAG, "getCacheKey exception:", e);
        }

        return result;
    }

    /**
     * x-www-form-urlencoded类型的POST参数解析
     *
     * @param parms 字符串参数
     * @param map   解析的数据存入该map
     * @throws UnsupportedEncodingException
     */
    private void decodeParms(String parms, Map<String, String> map) throws UnsupportedEncodingException {
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

            String values = map.get(key);
            if (values == null) {
                map.put(key, value);
            } else {
                map.put(key, values + value);
            }
        }
    }

}
