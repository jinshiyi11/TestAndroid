package com.shuai.test.okhttp;

import com.shuai.test.okhttp.annotation.EnableCache;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface RxGitHubService {
    @GET("users/{user}/repos")
    Observable<List<Repo>> listRepos(@Path("user") String user);

    @EnableCache(useAfterRequest = true, expireTime = 5000, excludeKeys = {"token","sign"})
    @GET("test500.txt")
    Observable<List<Repo>> test500(@Query("user") String user, @Query("token") String token);
}
