package com.gd.guandou.guandou.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.Application.MainApplication;
import com.gd.guandou.guandou.R;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;




/**
 * Activity基类，所有的Activity均继承它
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements
        OnClickListener {

    protected MainApplication myApplication;

    protected final static int HTTP_ERROR = 999;

    protected Context mContext = this;
    // private String mPageName;
    // 是否ActivityGroup父视图
    protected boolean mIsActivityGroupBase = false;
    protected ProgressDialog progressDialog;

    protected AlertDialog myDialog;
    protected boolean isWindowFree = true;

    protected int IsHttpOK = 0; // 0 失败 1 成功
    protected Window window;
    private boolean isDestroy = false;
    protected Window dialogWindow;
    private Toast toast;

    protected KProgressHUD dia= null;

    //请求失败弹窗
    protected  AlertView httperror_view=null;

    private  Message message= null;

    //数据传输
    private  Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        myApplication = (MainApplication) getApplication();
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
        // 去除头部
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);//初始化一个toast，解决多次弹出toast冲突问题
        x.view().inject(this);
        init(savedInstanceState);
        window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //首先清除状态栏透明的属性
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //添加绘制系统状态栏背景颜色的权限
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

    }

    /**
     * @param savedInstanceState
     * @return void
     * @description: 主要用于控件等初始化工作
     */
    protected abstract void init(Bundle savedInstanceState);


    /**
     * 设置标题
     */
    public void setTitle(int id) {
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(id));
    }

    /**
     * 设置标题
     */
    public void setTitle(String t) {
        ((TextView) findViewById(R.id.title)).setText(t);
    }





    /**
     * 显示加载对话框
     *
     * @param message
     */
    public void showLoadingDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    /**
     * 重写setContentView，让子类传入的View上方再覆盖一层LoadingView
     */
    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(mContext).inflate(layoutResID, null);
        super.setContentView(view);
    }

    /**
     * 隐藏加载对话框
     */
    public void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setDialogCancelable(Boolean flag) {
        if (progressDialog != null) {
            progressDialog.setCancelable(flag);
        }
    }


    /**
     * 提示框
     */
    public void CToast(String message) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 提示框
     */
    public void SToast(String message) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * 提示框
     */
    public void SToast(int id) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, getResources().getString(id), Toast.LENGTH_SHORT);
            toast.show();
            //    Toast.makeText(mContext, getResources().getString(id), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 自定义布局提示框
     */
    public void SToast(View view) {
        synchronized (mContext) {
            toast.cancel();
            LinearLayout.LayoutParams patams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            Toast toast = new Toast(mContext);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setLayoutParams(patams);
            linearLayout.setBackgroundResource(R.color.transparent);
            linearLayout.addView(view);
            toast.setView(linearLayout);
            toast.show();
        }
    }


    /**
     * @param message 提示内容
     * @param cancel  取消的what
     * @param ok      确定的what
     */
//    public void getMyDialog(String message, final int cancel, final int ok) {
//        new AlertDialogUtils(mContext).builder()
//                .setTitle("提示")
//                .setMsg(message)
//                .setPositiveButton("确认", new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        handler.sendEmptyMessage(ok);
//                    }
//                })
//                .setNegativeButton("取消", new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        handler.sendEmptyMessage(cancel);
//                    }
//                }).show();
//
//
//    }

    /**
     * @param title   提示的标题
     * @param message 提示内容
     * @param cancel  取消的what
     * @param ok      确定的what
     */
//    public void getMyDialog(String title, String message, final int cancel, final int ok) {
//        new AlertDialogUtils(mContext).builder()
//                .setTitle(title)
//                .setMsg(message)
//                .setPositiveButton("确认", new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        handler.sendEmptyMessage(ok);
//                    }
//                })
//                .setNegativeButton("取消", new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        handler.sendEmptyMessage(cancel);
//                    }
//                }).show();
//
//
//    }

    /**
     * 隐藏键盘
     */
    public void unkeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(
                        ((Activity) mContext).getCurrentFocus()
                                .getWindowToken(), 0);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }




    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isDestroy) {
                return;
            }
            ObjectMessage(msg);
            dismissLoadingDialog();
            super.handleMessage(msg);

        }
    };

    /**
     * Handler 处理事件 HTTP_ERROR：999 请求网络失败返回处理
     */
    protected abstract void ObjectMessage(Message msg);

    /**
     * 提供给titbar 的back 放回事件
     */
    public void backfinish(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismissLoadingDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * POST请求
     */
    public  void httpPostJson( String url, String jsonData, final int what) {
        dia = KProgressHUD.create(mContext).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        RequestParams params = new RequestParams(url);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.setBodyContent(jsonData);
        params.addHeader("charset","utf-8");
        params.setConnectTimeout(10000);
        Logger.i(jsonData);
        Logger.i(url);
        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String result) {
                Logger.i("on success "+result);
                Gson gson= new Gson();
                JSONObject jresult=gson.fromJson(result, JSONObject.class);
                try {
                    if (jresult.get("code").toString().equals("0")) {
                        if (jresult.has("data")){
                            bundle.putString("data", jresult.get("data").toString());}
                        else {bundle.putString("data", "");}

                        bundle.putInt("what", what);
                        bundle.putString("allresult", jresult.toString());
                        message = Message.obtain();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }else{
                        SToast(jresult.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                httperror_view= new AlertView(mContext.getResources().getString(R.string.dialog_title), "网络连接超时，请检查网络",
                        null, new String[]{"确定"}, null, mContext, AlertView.Style.Alert,
                        null);

                if (httperror_view.isShowing())
                {
                    httperror_view.dismiss();
                    httperror_view.show();
                }
                else{
                    httperror_view.show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dismissDia();
            }
        });
    }


    //取消加载框
    protected void dismissDia()
    {
        try {
            if (dia!=null && dia.isShowing())
            {
                dia.dismiss();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
