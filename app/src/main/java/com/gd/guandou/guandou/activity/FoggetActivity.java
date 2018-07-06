package com.gd.guandou.guandou.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.base.BaseActivity;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.ElseUtils;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;

@ContentView(R.layout.activity_fogget)
public class FoggetActivity extends BaseActivity {

    @ViewInject(R.id.iv_forrget_vcode)
    private ImageView iv_fogget_vcode;

    @ViewInject(R.id.ll_forrget_page_vcode)
    private LinearLayout ll_forrget_page_vcode;

    @ViewInject(R.id.ll_forrget_page_sms)
    private LinearLayout ll_forrget_page_sms;

    @ViewInject(R.id.ll_forrget_page_finish)
    private LinearLayout ll_forrget_page_finish;

    @ViewInject(R.id.et_forrget_zz)
    private EditText et_forrget_zz;

    @ViewInject(R.id.et_forrget_checkcode)
    private EditText et_forrget_checkcode;

    @ViewInject(R.id.et_forrget_page_check_sms)
    private EditText et_forrget_page_check_sms;


    @ViewInject(R.id.et_forrget_mm)
    private EditText et_forrget_mm;

    @ViewInject(R.id.et_forrget_mm_again)
    private EditText et_forrget_mm_again;

    @ViewInject(R.id.tv_forrget_page_sms_phone)
    private TextView tv_forrget_page_sms_phone;

    @ViewInject(R.id.btn_forrget_smscode)
    private Button btn_forrget_smscode;

    //获取图片验证码
    private  final int GETVCODE=1;
    //获取短信验证码
    private  final int SENDSMS=2;
    //获取短信验证码
    private  final int VERIFYSMS=3;
    //重置密码
    private  final int RESETPASSWORD=4;




    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("忘记密码");

    }

    @Override
    protected void ObjectMessage(Bundle msg) {
        Gson gson= new Gson();
        JSONObject rjson=null;
        if (!TextUtils.isEmpty(msg.getString("data"))){
            rjson=gson.fromJson(msg.getString("data"),JSONObject.class);
        }
        switch (msg.getInt("what")){
            //获取图片验证码
            case GETVCODE:
                break;
            //获取短信验证码
            case SENDSMS:
                new CountDownTimer(120000,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        btn_forrget_smscode.setText("("+millisUntilFinished/1000+")可再次获取");
                        btn_forrget_smscode.setEnabled(false);//重新获得点击
                    }
                    @Override
                    public void onFinish() {
                        btn_forrget_smscode.setText("获取验证码");
                        btn_forrget_smscode.setEnabled(true);//重新获得点击
                    }
                }.start();
            break;
            //验证短信验证码
            case VERIFYSMS:
                break;
            //重置密码
            case RESETPASSWORD:
                SToast("提交成功!");
                finish();
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //图片验证码
            case R.id.btn_forrget_next:
                break;
            //短信验证码
            case R.id.btn_forrget_sms_next:
                break;
            //提交
            case R.id.btn_forrget_finish:
                if (et_forrget_mm.getText().toString().length()<6||!ElseUtils.isMMYes(et_forrget_mm.getText().toString())){
                    Toast.makeText(this,"密码6位至20位，至少包含数字，大写字母，小写字母，其中2种!",Toast.LENGTH_SHORT);
                    return;
                }
                if (!et_forrget_mm.getText().toString().equals(et_forrget_mm_again.getText().toString())){
                    Toast.makeText(this,"输入的两次密码不相同!",Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(et_forrget_mm.getText().toString())){
                    Toast.makeText(this,"账号不能为空!",Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(et_forrget_page_check_sms.getText().toString())){
                    Toast.makeText(this,"验证码不能为空!",Toast.LENGTH_SHORT);
                    return;
                }
                resetPassWord();
                break;
            //获取短信验证码
            case R.id.btn_forrget_smscode:
                sendSms();
                break;
        }

    }


    /**
     * 获取验证码
     * @param
     */
    public void getVcode(){
        httpPostMap(Content.GDAPI+Content.gd_reg_vcode,new HashMap<String,Object>(),GETVCODE);
    }


    /**
     * 获取短信验证码
     * @param
     */
    public void sendSms(){
        if(et_forrget_zz.getText().toString().trim().isEmpty()){
            Toast.makeText(mContext,"手机号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap data =new HashMap<String,Object>();
        data.put("mobile",et_forrget_zz.getText().toString().trim());
        data.put("type","forgot");
        httpPostMap(Content.GDAPI+Content.gd_reg_sendSms,data,SENDSMS);
    }


    /**
     * 效验短信验证码
     * @param
     */
    public void verifySms(){
        HashMap data =new HashMap<String,Object>();
        data.put("account",tv_forrget_page_sms_phone.getText().toString().trim());
        data.put("vcode_type","topapi-signin");
        data.put("vcode",et_forrget_page_check_sms.getText().toString().trim());
        httpPostMap(Content.GDAPI+Content.verifySms,data,VERIFYSMS);
    }


    /**
     * 效验短信验证码
     * @param
     */
    public void resetPassWord(){
        HashMap data =new HashMap<String,Object>();
        data.put("mobile",et_forrget_zz.getText().toString().trim());
        data.put("password",et_forrget_mm.getText().toString().trim());
        data.put("password_confirmation",et_forrget_mm_again.getText().toString().trim());
        data.put("testcode",et_forrget_page_check_sms.getText().toString().trim());
        httpPostMap(Content.GDAPI+Content.resetpassword,data,RESETPASSWORD);
    }
}
