package com.shuai.test.webview;

import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class TestJavascriptInterface {
    @JavascriptInterface
    public String getUserInfo(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("name","aa");
            jsonObject.put("id",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
