package com.shuai.test.okhttp;

import com.shuai.test.okhttp.cache.CacheConst;
import com.shuai.test.okhttp.cache.CacheResult;
import com.shuai.test.okhttp.cache.NetConstants;
import com.shuai.test.okhttp.cache.retrofit.Code;
import com.shuai.test.okhttp.cache.retrofit.EnableCache;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface RxGitHubService {
    @GET("users/{user}/repos")
    Observable<List<Repo>> listRepos(@Path("user") String user);

    //@EnableCache(useAfterRequest = true, expireTime = 500000, excludeKeys = {"token","sign"})
    @GET("test500.txt")
    Observable<List<Repo>> test500(@Query("user") String user, @Query("token") String token, @Query(CacheConst.CACHE_POLICY) String cachePolicy);

    @EnableCache
    @Code(NetConstants.NO_CODE)
    @GET("test500.txt")
    Observable<CacheResult<List<Repo>>> testCacheResult(@Query("user") String user, @Query("token") String token, @Query(CacheConst.CACHE_POLICY) String cachePolicy);
}
