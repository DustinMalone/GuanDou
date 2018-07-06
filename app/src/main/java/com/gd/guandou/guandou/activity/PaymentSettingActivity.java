package com.gd.guandou.guandou.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.HttpUtils;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_payment_setting)
public class PaymentSettingActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.tv_payment_setting_mm)
    private EditText tv_payment_setting_mm;

    @ViewInject(R.id.tv_payment_setting_new_mm)
    private EditText tv_payment_setting_new_mm;

    @ViewInject(R.id.tv_payment_setting_confirm_mm)
    private EditText tv_payment_setting_confirm_mm;

    @ViewInject(R.id.ll_payment_setting_mm)
    private LinearLayout ll_payment_setting_mm;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        context=this;
//        setContentView(R.layout.activity_payment_setting);

        init();
    }

    private void init() {
        isSetMM();
//        tv_payment_setting_mm.setFilters(new InputFilter[]{ElseUtils.noChineseAndEmptyFilter()});
//        tv_payment_setting_new_mm.setFilters(new InputFilter[]{ElseUtils.noChineseAndEmptyFilter()});
//        tv_payment_setting_confirm_mm.setFilters(new InputFilter[]{ElseUtils.noChineseAndEmptyFilter()});

    }

    /**
     * 检查是否设置支付密码
     * @param
     */
    private void isSetMM(){
        JSONObject data =new JSONObject();
        try {
            data.put("user_id", SharedPreferencesUtil.getString(context,"user_id",""));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_IsSetPayMM, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        if(jobject.getJSONObject("data").getInt("type")==0){
                            ll_payment_setting_mm.setVisibility(View.VISIBLE);
                        }else {
                            ll_payment_setting_mm.setVisibility(View.GONE);
                        }
                    }else {
                        T.show(context, jobject.get("message").toString(), 1500);
                    }
                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    // ...
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    // ...
                    Toast.makeText(x.app(), "服务繁忙:"+ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 设置支付密码
     * @param
     */
    private void settingPaymentMM(String password){
        JSONObject data =new JSONObject();
        try {
            data.put("accessToken", SharedPreferencesUtil.getString(context,"accessToken",""));

            data.put("password",password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_SetPayMM, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    T.show(context,jobject.get("message").toString(),1500);
                    if (jobject.get("code").toString().equals("0")){
                        finish();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    // ...
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    // ...
                    Toast.makeText(x.app(), "服务繁忙:"+ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }


    /**
     * 修改支付密码
     * @param
     */
    private void updatePaymentMM(String password,String oldPwd){
        JSONObject data =new JSONObject();
        try {
            data.put("user_id", SharedPreferencesUtil.getString(context,"user_id",""));

            data.put("password",password);
            data.put("oldpwd",oldPwd);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_UpdatePayMM, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    T.show(context,jobject.get("message").toString(),1500);
                    if (jobject.get("code").toString().equals("0")){
                        finish();
                    }


                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    // ...
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    // ...
                    Toast.makeText(x.app(), "服务繁忙:"+ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_payment_setting_confirm:
                if (TextUtils.isEmpty(tv_payment_setting_new_mm.getText().toString().trim())
                    ||TextUtils.isEmpty(tv_payment_setting_confirm_mm.getText().toString().trim()))
                {
                    T.show(context,"密码或确认密码不能为空！",1000);
                    return;
                }

                if (!tv_payment_setting_new_mm.getText().toString().trim().equals(tv_payment_setting_confirm_mm.getText().toString().trim()))
                {
                    T.show(context,"密码与确认密码不相同！",1000);
                    return;
                }

                if (ll_payment_setting_mm.getVisibility()==View.VISIBLE&&TextUtils.isEmpty(tv_payment_setting_mm.getText().toString().trim())){
                    T.show(context,"旧密码不能为空！",1000);
                    return;
                }

                if (tv_payment_setting_new_mm.getText().toString().trim().length()!=6){
                    T.show(context,"密码长度为6位数字！",1000);
                    return;
                }

                if (ll_payment_setting_mm.getVisibility()==View.VISIBLE){
                    updatePaymentMM(tv_payment_setting_new_mm.getText().toString().trim(),tv_payment_setting_mm.getText().toString().trim());
                }else{
                    settingPaymentMM(tv_payment_setting_new_mm.getText().toString().trim());

                }
                break;
            //后退
            case R.id.iv_payment_setting_back:
                finish();
                break;
        }

    }
}
