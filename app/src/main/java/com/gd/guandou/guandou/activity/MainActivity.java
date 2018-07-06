package com.gd.guandou.guandou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.ElseUtils;
import com.gd.guandou.guandou.untils.IntentUtil;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.view.LoginPopupWindow;
import com.gd.guandou.guandou.view.ProgressDialogView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.lg_zh)
    private EditText lg_zh;

    @ViewInject(R.id.lg_mm)
    private EditText lg_mm;

   private  LoginPopupWindow loginPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ssetContentView(R.layout.activity_main);
        //注解注释View的绑定
        x.view().inject(this);
        init();
    }

    private void init() {
        //捕捉密码输入框,禁止输入汉字
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (ElseUtils.isChinese(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        lg_mm.setFilters(new InputFilter[]{filter});
        lg_zh.setFilters(new InputFilter[]{filter});
        SharedPreferencesUtil.remove(MainActivity.this,"user_id");
        SharedPreferencesUtil.remove(MainActivity.this,"accessToken");
    }



    private void login() throws JSONException {
        //判断是否账号密码空值
        if (TextUtils.isEmpty(lg_zh.getText().toString())||TextUtils.isEmpty(lg_mm.getText().toString())){
           Toast.makeText(MainActivity.this,"账号或密码不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialogView.buildProgressDialog(MainActivity.this,"数据加载中...");
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_login);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.addHeader("charset","utf-8");
        params.addBodyParameter("account",lg_zh.getText().toString());
        params.addBodyParameter("password",lg_mm.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        SharedPreferencesUtil.putString(MainActivity.this,"user_id",jobject.getJSONObject("data").get("user_id").toString());
                        SharedPreferencesUtil.putString(MainActivity.this,"accessToken",jobject.getJSONObject("data").get("accessToken").toString());
                        Toast.makeText(MainActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(MainActivity.this, ShouYeActivity.class);
                        startActivity(it); //执行
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();
                } else { // 其他错误
                    // ...
                    Toast.makeText(x.app(), "服务繁忙", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                ProgressDialogView.cancelProgressDialog();

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lg_submit:
                try {
//                    IntentUtil.goIntent(MainActivity.this,ShouYeActivity.class,null,true);
                    login();
                } catch (Exception e) {
                e.printStackTrace();
            }

                break;
            case R.id.lg_reg:
//                showEditPopWin(v);
                IntentUtil.goIntent(MainActivity.this,RegActivity.class,null,false);
                break;
            case R.id.lg_fogget_mm:
//                showEditPopWin(v);
                IntentUtil.goIntent(MainActivity.this,FoggetActivity.class,null,false);

                break;
        }

    }


    /**
     * 显示输入框
     * @param view
     */
    public void showEditPopWin(View view) {
        if (loginPopupWindow==null){
        loginPopupWindow = new LoginPopupWindow(this);
        }
        switch (view.getId()){
            case R.id.lg_reg:
                loginPopupWindow.setWebViewURL(Content.regPage);
                break;
            case R.id.lg_fogget_mm:
                loginPopupWindow.setWebViewURL(Content.foggetMMPage);
                break;
        }
        if (!loginPopupWindow.isShowing()){
        loginPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
