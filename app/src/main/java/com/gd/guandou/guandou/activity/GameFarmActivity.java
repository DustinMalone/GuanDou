package com.gd.guandou.guandou.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.bean.LoginAppClass;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


@ContentView(R.layout.activity_game_farm)
public class GameFarmActivity extends AppCompatActivity {

    @ViewInject(R.id.wv_game_farm)
    private WebView wv_game_farm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
       // setContentView(R.layout.activity_game_farm);
        init();
    }

    /**
     * 初始化webview 允许js后要小心XSS攻击了
     *
     * @JavascriptInterface这个注解 在4.2以上修复了，但是需要注意兼容性
     */

    private void init() {

        wv_game_farm.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        wv_game_farm.getSettings().setJavaScriptEnabled(true);

//设置自适应屏幕，两者合用
        wv_game_farm.getSettings().setUseWideViewPort(true); //将图片调整到适合webview的大小
        wv_game_farm.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//其他细节操作
        wv_game_farm.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        wv_game_farm.getSettings().setAllowFileAccess(true); //设置可以访问文件
        wv_game_farm.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        wv_game_farm.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片

        wv_game_farm.getSettings().setDomStorageEnabled(true);
        wv_game_farm.getSettings().setAppCacheMaxSize(1024*1024*8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        wv_game_farm.getSettings().setAppCachePath(appCachePath);
        wv_game_farm.getSettings().setDatabaseEnabled(true);
        wv_game_farm.getSettings().setAllowFileAccess(true);
        wv_game_farm.getSettings().setAppCacheEnabled(true);
        wv_game_farm.getSettings().setGeolocationEnabled(true);

        // 设置拦截js中的三种弹框
       wv_game_farm.setWebChromeClient(new MyWebChromeClien());

        // 监听点击的每一个url，做自己的特殊处理用
        MyWebViewClient myWebViewClient=new MyWebViewClient();
        wv_game_farm.setWebViewClient(myWebViewClient);

        wv_game_farm.addJavascriptInterface(new LoginAppClass(GameFarmActivity.this),"game_farm");

        // 加载项目asset文件夹下的本地html
        String fileUrl = "file:///android_asset/pages/game_farm.html";
        wv_game_farm.loadUrl(fileUrl);





    }


    /**
     * 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
     */
    private class MyWebViewClient extends WebViewClient {
        // 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("----------------------shouldOverrideUrlLoading 。。 url:" + url);
            if (url.equals("file:///android_asset/pages/index.html")) {
                Log.e("wwwww",url);
                GameFarmActivity.this.finish();
                return true;
            }else
            {
                Log.e("wwwwwwww",url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        // 监听加载资源
        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.equals("index.html")){
                Log.e("dasdsa",url);
            }else {
                Log.e("dddddd",url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            Log.e("nioninidindo",url);
            if (url.equals("file:///android_asset/pages/game_farm.html")) {
                JSONObject object = new JSONObject();
                try {
                    object.put("user_id", SharedPreferencesUtil.getString(GameFarmActivity.this,
                            "user_id", ""));
                    object.put("accessToken", SharedPreferencesUtil.getString(GameFarmActivity.this,
                            "accessToken", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view.loadUrl("javascript:(function(){" +
                        "var localStorage = window.localStorage; " +
                        "var ob=JSON.stringify(" + object.toString() + ");" +
                        "  localStorage.setItem(\"login\",ob);" +
                        "window.game_farm.getUserKey(localStorage.getItem(\"login\"))})()");

                String js = "var newscript = document.createElement(\"script\");";
                js += "newscript.src=\"file:///android_asset/js/farm.js\";";
                js += "document.body.appendChild(newscript);";
                view.loadUrl("javascript:" + js);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }
    }

    /**
     * 拦截js的提示框
     */
    private class MyWebChromeClien extends WebChromeClient {
        // 提示框
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  final JsPromptResult result) {
            new AlertDialog.Builder(GameFarmActivity.this).setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 当弹出框点击的时候，手动用代码给js返回确认的信息
                            result.confirm("true");
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).show();
            // 返回false会弹出原声dialog，这里不让原声的弹出
            return true;
        }

        // 警告框
        @SuppressLint("NewApi")
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            // api 17以后才有onDismiss 接口，这里稍作处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                new AlertDialog.Builder(GameFarmActivity.this).setMessage(message)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface arg0) {
                                // TODO Auto-generated method stub
                                result.cancel();
                            }
                        }).show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(GameFarmActivity.this).setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 当弹出框点击的时候，手动用代码给js返回确认的信息
                                result.cancel();
                            }
                        }).show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
            }
            return true;
        }

        // 确认框
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(GameFarmActivity.this).setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 当弹出框点击的时候，手动用代码给js返回确认的信息
                            result.confirm();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            }).show();
            return true;
        }
    }

    /**
     *避免WebView内存泄露
     */
    @Override
    protected void onDestroy() {
        if (wv_game_farm != null) {
            wv_game_farm.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            wv_game_farm.clearHistory();

            ((ViewGroup) wv_game_farm.getParent()).removeView(wv_game_farm);
            wv_game_farm.destroy();
            wv_game_farm = null;
        }
        super.onDestroy();
    }
}
