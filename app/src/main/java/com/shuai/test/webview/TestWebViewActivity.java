package com.shuai.test.webview;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.shuai.test.R;

public class TestWebViewActivity extends AppCompatActivity {
    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_web_view);

        mWebView = (WebView) findViewById(R.id.wv_test);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new TestJavascriptInterface(),"Common");
        mWebView.loadUrl("file:///android_asset/test.html");

    }

}
