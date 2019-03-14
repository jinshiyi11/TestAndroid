package com.shuai.test.okhttp.cache;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;

import okio.ByteString;
import retrofit2.adapter.rxjava.HttpException;

/**
 * 缓存相关工具类
 */
public class CacheUtil {
    /**
     * 是否是网络问题或者服务异常，异常时使用缓存
     *
     * @param throwable 异常对象
     * @return true-是，false-不是
     */
    public static boolean isNetworkExceptionOrServerException(Throwable throwable) {
        return throwable instanceof HttpException
                || throwable instanceof SocketTimeoutException
                || throwable instanceof UnknownHostException
                || throwable instanceof ConnectException
                ;
    }

    /**
     * MD5计算
     *
     * @param s 字符串
     * @return 对应的MD5
     */
    public static String md5Hex(String s) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5bytes = messageDigest.digest(s.getBytes("UTF-8"));
            return ByteString.of(md5bytes).hex();
        } catch (Exception e) {
            return null;
        }
    }
}
