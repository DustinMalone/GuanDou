package com.gd.guandou.guandou.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.bean.LoginAppClass;
import com.gd.guandou.guandou.untils.IntentUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @ViewInject(R.id.wv_login_page)
    private WebView wv_login_page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注解注释View的绑定
        x.view().inject(this);
        init();

    }

    private void init() {

        wv_login_page.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        wv_login_page.getSettings().setJavaScriptEnabled(true);

//设置自适应屏幕，两者合用
        wv_login_page.getSettings().setUseWideViewPort(true); //将图片调整到适合webview的大小
        wv_login_page.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//其他细节操作
        wv_login_page.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        wv_login_page.getSettings().setAllowFileAccess(true); //设置可以访问文件
        wv_login_page.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        wv_login_page.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片

        wv_login_page.getSettings().setDomStorageEnabled(true);
        wv_login_page.getSettings().setAppCacheMaxSize(1024*1024*8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        wv_login_page.getSettings().setAppCachePath(appCachePath);
        wv_login_page.getSettings().setDatabaseEnabled(true);
        wv_login_page.getSettings().setAllowFileAccess(true);
        wv_login_page.getSettings().setAppCacheEnabled(true);
        wv_login_page.getSettings().setGeolocationEnabled(true);



        // 监听点击的每一个url，做自己的特殊处理用
        MyWebViewClient myWebViewClient=new MyWebViewClient();
        wv_login_page.setWebViewClient(myWebViewClient);

        wv_login_page.addJavascriptInterface(new LoginAppClass(LoginActivity.this), "android");

        // 加载项目asset文件夹下的本地html
        String fileUrl = "file:///android_asset/login.html";
        wv_login_page.loadUrl(fileUrl);



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
                Log.e("login",url);
//                String jsUrl = "javascript:(function({ window.android.getUserKey(localStorage.getItem" +
//                        "(\"login\"));})";
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    view.evaluateJavascript(jsUrl, null);
//                    Log.e("login","evaluateJavascript");
//                } else {
//                    Log.e("login","loadUrl");
//                    view.loadUrl(jsUrl);
//                }
                IntentUtil.goIntent(LoginActivity.this,ShouYeActivity.class,null,true);
                return true;
            }else
            {
                Log.e("login",url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        // 监听加载资源
        @Override
        public void onLoadResource(WebView view, String url) {
                Log.e("login",url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }

    /**
     *避免WebView内存泄露
     */
    @Override
    protected void onDestroy() {
        if (wv_login_page != null) {
            wv_login_page.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            wv_login_page.clearHistory();

            ((ViewGroup) wv_login_page.getParent()).removeView(wv_login_page);
            wv_login_page.destroy();
            wv_login_page = null;
        }
        super.onDestroy();
    }
}
