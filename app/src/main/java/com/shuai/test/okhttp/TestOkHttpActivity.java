package com.shuai.test.okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shuai.test.MyApplication;
import com.shuai.test.R;
import com.shuai.test.okhttp.cache.CacheInterceptor;
import com.shuai.test.okhttp.cache.CachePolicy;
import com.shuai.test.okhttp.cache.CacheResult;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TestOkHttpActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = TestOkHttpActivity.class.getSimpleName();
    private Button mBtnTest;
    private OkHttpClient mClient;
    private RxGitHubService mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ok_http);
        mBtnTest = findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);

        initOkHttp();
        Retrofit retrofit = new Retrofit.Builder()
                .client(mClient)
                //.baseUrl("https://api.github.com/")
                .baseUrl("http://10.113.21.105")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addCallAdapterFactory(CallAdapterFactory.create())
                .build();
        mApi = retrofit.create(RxGitHubService.class);
    }

    private void initOkHttp(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        mClient = new OkHttpClient.Builder()
                .addInterceptor(new CacheInterceptor(MyApplication.getContext(),10*1024*1024))
                .addInterceptor(logging)
                .build();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_test) {
            testRx();
        }
    }

    private void testRx() {
        getTestObservable("tom","123qwe",50*1000)
                .subscribe(new Observer<CacheResult<List<Repo>>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                    }

                    @Override
                    public void onNext(CacheResult<List<Repo>> result) {
                        List<Repo> repos = result.getData();
                        Log.d(TAG, "onNext，isFromCache：" + result.isFromCache() +",data:" + repos);
                    }
                });
//        mApi.test500("tom","123qwe",new CachePolicy().toString())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<List<Repo>>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d(TAG, "onCompleted");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError", e);
//                    }
//
//                    @Override
//                    public void onNext(List<Repo> repos) {
//                        Log.d(TAG, "onNext:" + repos);
//                    }
//                });
//
//        service.test500("tom","123qwe",new CachePolicy(50*1000).toString())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<List<Repo>>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d(TAG, "onCompleted");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError", e);
//                    }
//
//                    @Override
//                    public void onNext(List<Repo> repos) {
//                        Log.d(TAG, "onNext:" + repos);
//                    }
//                });
    }

    private Observable<CacheResult<List<Repo>>> getTestObservable(final String user, final String token, final long expireTime){
        Observable<CacheResult<List<Repo>>> online = mApi.test500(user,token, CachePolicy.online().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<List<Repo>, CacheResult<List<Repo>>>() {
                    @Override
                    public CacheResult<List<Repo>> call(List<Repo> repos) {
                        return new CacheResult<>(false,repos);
                    }
                });

        final Observable<CacheResult<List<Repo>>> cache = mApi.test500(user,token,CachePolicy.cache(expireTime).toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<List<Repo>, CacheResult<List<Repo>>>() {
                    @Override
                    public CacheResult<List<Repo>> call(List<Repo> repos) {
                        return new CacheResult<>(true,repos);
                    }
                });

        return online.onErrorResumeNext(new Func1<Throwable, Observable<? extends CacheResult<List<Repo>>>>() {
                    @Override
                    public Observable<? extends CacheResult<List<Repo>>> call(Throwable throwable) {
                        return cache.onErrorResumeNext(Observable.<CacheResult<List<Repo>>>error(throwable));
                    }
                });
    }

    private void test() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        Call<List<Repo>> repos = service.listRepos("octocat");
    }
}
