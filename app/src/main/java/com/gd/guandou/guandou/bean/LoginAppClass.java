package com.gd.guandou.guandou.bean;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.gd.guandou.guandou.untils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/10/13.
 */

public class LoginAppClass {
    private Context c;

    public LoginAppClass(Context baseContext) {
        this.c = baseContext;
    }

    @JavascriptInterface
    public void getUserKey(String userKey) {
//        Toast.makeText(c, userKey + "", Toast.LENGTH_SHORT).show();
        try {
            JSONObject myJsonObject = new JSONObject(userKey);
            SharedPreferencesUtil.putString(c,"accessToken", myJsonObject.getJSONObject("data").get("accessToken").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Tag", "读取到userKey : " + userKey);
    }
}
