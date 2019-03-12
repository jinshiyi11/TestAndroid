package com.shuai.test.okhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shuai.test.MyApplication;
import com.shuai.test.R;
import com.shuai.test.okhttp.adapter.CallAdapterFactory;
import com.shuai.test.okhttp.cache.CacheInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TestOkHttpActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = TestOkHttpActivity.class.getSimpleName();
    private Button mBtnTest;
    private OkHttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ok_http);
        mBtnTest = findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);

        initOkHttp();
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

        Retrofit retrofit = new Retrofit.Builder()
                .client(mClient)
                //.baseUrl("https://api.github.com/")
                .baseUrl("http://10.113.21.105")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CallAdapterFactory.create())
                .build();

        RxGitHubService service = retrofit.create(RxGitHubService.class);
        service.test500("tom","123qwe")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Repo>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                    }

                    @Override
                    public void onNext(List<Repo> repos) {
                        Log.d(TAG, "onNext:" + repos);
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
