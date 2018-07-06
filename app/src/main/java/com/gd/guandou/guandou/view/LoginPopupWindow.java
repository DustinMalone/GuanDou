package com.gd.guandou.guandou.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.gd.guandou.guandou.R;

/**
 * Created by Administrator on 2017/10/9.
 */


public class LoginPopupWindow  extends PopupWindow{

        private Context mContext;


        private View view;
        private WebView wv_login_reg;
        private Button btn_back_login;



        public LoginPopupWindow(Activity mContext) {

            this.mContext = mContext;

//          this.view = LayoutInflater.from(mContext).inflate(R.layout.login_pop, null);
//            this.wv_login_reg =(WebView) view.findViewById(R.id.wv_login_reg);
//            this.btn_back_login =(Button) view.findViewById(R.id.btn_back_login);

            // 设置按钮监听
           btn_back_login.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   switch (v.getId()){
                       case R.id.btn_back_login:
                           closeEditPopWin();
                           break;
                   }

               }
           });

            //初始化webview
            wv_login_reg.getSettings().setDefaultTextEncodingName("utf-8");
            wv_login_reg.getSettings().setJavaScriptEnabled(true);
            wv_login_reg.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv_login_reg.getSettings().setSupportMultipleWindows(true);

            wv_login_reg.getSettings().setAllowContentAccess(true);
            wv_login_reg.getSettings().setAppCacheEnabled(false);

            wv_login_reg.getSettings().setUseWideViewPort(true);
            wv_login_reg.getSettings().setLoadWithOverviewMode(true);
            wv_login_reg.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            wv_login_reg.setWebViewClient(new WebViewClient());
            wv_login_reg.setWebChromeClient(new WebChromeClient());
            //设置自适应屏幕，两者合用
            wv_login_reg.getSettings().setUseWideViewPort(true); //将图片调整到适合webview的大小
            wv_login_reg.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小

            //缩放操作
            wv_login_reg.getSettings().setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
            wv_login_reg.getSettings().setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
            wv_login_reg.getSettings().setDisplayZoomControls(false); //隐藏原生的缩放控件

            //其他细节操作
            wv_login_reg.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片




            // 设置外部可点击
            this.setOutsideTouchable(true);



            /* 设置弹出窗口特征 */
            // 设置视图
           this.setContentView(this.view);

            // 设置弹出窗体的宽和高
              /*
             * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
             * 对象,这样这可以以同样的方式改变这个Activity的属性.
             */
            Window dialogWindow = mContext.getWindow();

            WindowManager m = mContext.getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            this.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
            this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
            this.setWidth((int) (d.getWidth() * 1));

            // 设置弹出窗体可点击
            this.setFocusable(true);



        }


    /**
     * 设置WWEBVIEW访问路径
     */
    public void setWebViewURL(String url) {
        wv_login_reg.loadUrl(url);
    }


    /**
     * 关闭POPUP
     * @param
     */
    public void closeEditPopWin() {
        if (wv_login_reg != null) {
            wv_login_reg.clearCache(true);
            wv_login_reg.stopLoading();
            //防止系统在3.x的手机，在缩放按钮未完全关闭时候退出的闪退
            wv_login_reg.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (wv_login_reg != null) {
                        wv_login_reg.destroy();
                        wv_login_reg = null;
                    }
                }
            }, 3000);
        }
       if (this.isShowing()){
           this.dismiss();
       }
    }

}
