package com.gd.guandou.guandou.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.ElseUtils;
import com.gd.guandou.guandou.untils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.gd.guandou.guandou.content.Content.verifySms;

@ContentView(R.layout.login_pop)
public class RegActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.iv_reg_vcode)
    private ImageView iv_reg_vcode;

    private Context context;

    @ViewInject(R.id.reg_vcode_page)
    private LinearLayout reg_vcode_page;

    @ViewInject(R.id.reg_register_page)
    private LinearLayout reg_register_page;

    @ViewInject(R.id.reg_register_page_finish)
    private LinearLayout reg_register_page_finish;

    @ViewInject(R.id.et_reg_zz)
    private EditText et_reg_zz;

    @ViewInject(R.id.et_reg_checkcode)
    private EditText et_reg_checkcode;

    @ViewInject(R.id.et_reg_check_sms_code)
    private EditText et_reg_check_sms_code;

    @ViewInject(R.id.btn_register_smscode)
    private Button btn_register_smscode;

    @ViewInject(R.id.et_reg_mm)
    private EditText et_reg_mm;

    @ViewInject(R.id.et_reg_mm_again)
    private EditText et_reg_mm_again;

    @ViewInject(R.id.tv_reg_register_page_phone)
    private TextView tv_reg_register_page_phone;

    @ViewInject(R.id.tv_title)
    private TextView tv_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注解注释View的绑定
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        context=this;
        init();
        
    }

    private void init() {
        tv_title.setText("注册");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //获取验证码
            case R.id.iv_reg_vcode:
                break;
            //返回
            case R.id.btn_back_login:
                finish();
                break;
            //下一步
            case R.id.btn_reg_next:
                break;
            //注册下一步
            case R.id.btn_reg_register:
                break;
            //注册
            case R.id.btn_reg_finish:
                if (et_reg_mm.getText().toString().length()<6||!ElseUtils.isMMYes(et_reg_mm.getText().toString())){
                    Toast.makeText(this,"密码6位至20位，至少包含数字，大写字母，小写字母，其中2种!",Toast.LENGTH_SHORT);
                    return;
                }
                if (!et_reg_mm.getText().toString().equals(et_reg_mm_again.getText().toString())){
                    Toast.makeText(this,"输入的两次密码不相同!",Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(et_reg_zz.getText().toString())){
                    Toast.makeText(this,"账号不能为空!",Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(et_reg_check_sms_code.getText().toString())){
                    Toast.makeText(this,"验证码不能为空!",Toast.LENGTH_SHORT);
                    return;
                }
                signup();
                break;
            //获取短信验证码
            case R.id.btn_register_smscode:
                sendSMS();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 获取验证码
     * @param
     */
    public void getVcode(){
        JSONObject data =new JSONObject();
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_reg_vcode, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                Log.e("asd",result);
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        String s=jobject.getJSONObject("data").getString("vcode");
                        iv_reg_vcode.setImageBitmap(ElseUtils.stringtoBitmap(s.substring(s.indexOf(","))));
                    }else{
                        Toast.makeText(context,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(x.app(), "服务繁忙", Toast.LENGTH_LONG).show();
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
     * 效验图片验证码及账号
     * @param
     */
    public void verifyAccount(){
        JSONObject data =new JSONObject();
        try {
            data.put("account",et_reg_zz.getText().toString().trim());
            data.put("vcode_type","signup");
            data.put("verifycode",et_reg_checkcode.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+ Content.verifyAccount, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("on",result.toString());
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        sendSMS();
                        reg_register_page.setVisibility(View.VISIBLE);
                        reg_register_page_finish.setVisibility(View.GONE);
                        tv_reg_register_page_phone.setText(et_reg_zz.getText().toString().trim());
                    }else{
                        Toast.makeText(context,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    Toast.makeText(x.app(), "服务繁忙", Toast.LENGTH_LONG).show();
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
     * 发送短信
     * @param
     */
    public void sendSMS(){
        if(et_reg_zz.getText().toString().trim().isEmpty()){
            Toast.makeText(context,"手机号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject data =new JSONObject();
        try {
            data.put("mobile",et_reg_zz.getText().toString().trim());
            data.put("type","signup");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+ Content.gd_reg_sendSms, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                Log.e("sendSms",result.toString());
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        new CountDownTimer(120000,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                btn_register_smscode.setText("("+millisUntilFinished/1000+")可再次获取");
                                btn_register_smscode.setEnabled(false);//重新获得点击
                            }
                            @Override
                            public void onFinish() {
                                btn_register_smscode.setText("获取验证码");
                                btn_register_smscode.setEnabled(true);//重新获得点击
                            }
                        }.start();
                    }else{
                        Toast.makeText(context,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    Toast.makeText(x.app(), "服务繁忙", Toast.LENGTH_LONG).show();
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
     * 效验短信验证码
     * @param
     */
    public void verifySms(){
        JSONObject data =new JSONObject();
        try {
            data.put("account",et_reg_zz.getText().toString().trim());
            data.put("vcode_type","signup");
            data.put("vcode",et_reg_check_sms_code.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+ verifySms, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        reg_register_page.setVisibility(View.GONE);
                        reg_register_page_finish.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(context,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    Toast.makeText(x.app(), "服务繁忙", Toast.LENGTH_LONG).show();
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
     * 用户注册
     * @param
     */
    public void signup(){
        JSONObject data =new JSONObject();
        try {
            data.put("mobile",et_reg_zz.getText().toString().trim());
            data.put("type","signup");
            data.put("password_confirmation",et_reg_mm_again.getText().toString().trim());
            data.put("password",et_reg_mm.getText().toString().trim());
            data.put("testcode",et_reg_check_sms_code.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+ Content.signup, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("signup",result.toString());
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        Toast.makeText(context,"注册成功！",Toast.LENGTH_SHORT).show();
                        finish();
//                        signPay(jobject.getJSONObject("data").getString("payment_id"));
                    }else{
                        Toast.makeText(context,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    Toast.makeText(x.app(), "服务繁忙", Toast.LENGTH_LONG).show();
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
     * 用户注册支付
     * @param
     */
    public void signPay(String payment_id){
        JSONObject data =new JSONObject();
        try {
            data.put("payment_id",payment_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+ Content.pay, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    Toast.makeText(context,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(context,"JSON解析错误!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    Toast.makeText(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG).show();

                } else { // 其他错误
                    Toast.makeText(x.app(), "服务繁忙", Toast.LENGTH_LONG).show();
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
}
