package com.gd.guandou.guandou.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.ElseUtils;
import com.gd.guandou.guandou.untils.IntentUtil;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;
import com.gd.guandou.guandou.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_my_wealth)
public class MyWealthActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.tv_mywealth_name)
    private TextView tv_mywealth_name;

    @ViewInject(R.id.ll_bottom_my)
    private LinearLayout ll_bottom_my;

    @ViewInject(R.id.ll_bottom_main)
    private LinearLayout ll_bottom_main;

    @ViewInject(R.id.ll_bottom_trade)
    private LinearLayout ll_bottom_trade;

    @ViewInject(R.id.ll_bottom_friend)
    private LinearLayout ll_bottom_friend;

    @ViewInject(R.id.tv_mywealth_beans)
    private TextView tv_mywealth_beans;

    @ViewInject(R.id.tv_my_bottom)
    private TextView tv_my_bottom;

    @ViewInject(R.id.iv_my_bottom)
    private ImageView iv_my_bottom;

    @ViewInject(R.id.civ_mywealth_head_picture)
    private CircleImageView civ_mywealth_head_picture;

    private final int headPictueRequestCode=0;

    private   byte[] img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_my_wealth);
        //添加ACTIVITY到栈堆中
        AppManager.getAppManager().addActivity(this);
        x.view().inject(this);

        init();
    }

    private void init() {
        tv_my_bottom.setTextColor(ContextCompat.getColor(MyWealthActivity.this, R.color.colorblue));
        iv_my_bottom.setImageResource(R.mipmap.my_icon_at);
        ll_bottom_main.setOnClickListener(this);
        ll_bottom_trade.setOnClickListener(this);
        ll_bottom_friend.setOnClickListener(this);
        civ_mywealth_head_picture.setOnClickListener(this);
        getUserInfo();
        getbeansInfo();
        getHeadPicture();
    }

    //获取用户信息
    private void getUserInfo() {
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_user_info);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.addBodyParameter("accessToken", SharedPreferencesUtil.getString(MyWealthActivity.this,"accessToken",""));
        params.addHeader("charset","utf-8");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        tv_mywealth_name.setText(jobject.getJSONObject("data").getString("username"));
                    }else{
                        tv_mywealth_name.setText("...");
//                        Log.e("as",jobject.toString());
                        Toast.makeText(MyWealthActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MyWealthActivity.this,"获取用户信息错误!",Toast.LENGTH_SHORT).show();
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

            }
        });
    }

    //获取用户贯豆信息
    private void getbeansInfo() {
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_user_GD);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.addBodyParameter("accessToken", SharedPreferencesUtil.getString(MyWealthActivity.this,"accessToken",""));
        params.addHeader("charset","utf-8");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        tv_mywealth_beans.setText(jobject.getJSONObject("data").getString("gdnumber"));
                    }else{
                        tv_mywealth_beans.setText("...");
                        Toast.makeText(MyWealthActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MyWealthActivity.this,"获取贯豆用户信息错误!",Toast.LENGTH_SHORT).show();
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

            }
        });
    }


    //获取用头像信息
    private void getHeadPicture() {
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_user_GetHeadPhoto);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.addBodyParameter("user_id", SharedPreferencesUtil.getString(MyWealthActivity.this,"user_id",""));
        params.addHeader("charset","utf-8");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){

                       img= ElseUtils.hex2byte(jobject.getString("data"));
                        if (img!=null){
                       civ_mywealth_head_picture.setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.length));
                        }
                    }else{
                        Toast.makeText(MyWealthActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MyWealthActivity.this,"获取用户头像信息错误!",Toast.LENGTH_SHORT).show();
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

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //我的财富
            case R.id.ll_bottom_main:
                IntentUtil.goIntent(MyWealthActivity.this,ShouYeActivity.class,null,true);
                break;
            //贯豆指数
            case R.id.ll_bottom_trade:
                IntentUtil.goIntent(MyWealthActivity.this,ValueIndexActivity.class,null,true);
                break;
            //朋友
            case R.id.ll_bottom_friend:
//                  IntentUtil.goIntent(ShouYeActivity.this,ValueIndexActivity.class,null,true);
                T.cshow(MyWealthActivity.this,"功能即将启用！",1000);
                break;
            //消费记录
            case R.id.ll_mywealth_recoed:
                IntentUtil.goIntent(MyWealthActivity.this,PurchaseHistoryActivity.class,null,false);
                break;
            //退出按钮
            case R.id.btn_mywealth_exit:
                SharedPreferencesUtil.remove(MyWealthActivity.this,"accessToken");
                AppManager.getAppManager().finishAllActivity();
                IntentUtil.goIntent(MyWealthActivity.this,MainActivity.class,null,true);
                break;
            //安全设置
            case R.id.ll_mywealth_safety:
                IntentUtil.goIntent(MyWealthActivity.this,PaymentSettingActivity.class,null,false);
                break;

            //总资产
            case R.id.ll_mywealth_money:
                IntentUtil.goIntent(MyWealthActivity.this,TotalAssetsActivity.class,null,false);
                break;

            //头像
            case R.id.civ_mywealth_head_picture:
                Bundle bundle=new Bundle();
                bundle.putByteArray("img",img);
                IntentUtil.goResultIntent(MyWealthActivity.this,HeadPictureActivity.class,
                        bundle,false,headPictueRequestCode);
                break;



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==headPictueRequestCode){
            init();
        }
    }

    //监听返回键
    @Override
    public void onBackPressed() {
        //super.onBackPressed()会自动调用finish()方法,关闭
//        super.onBackPressed();

        ElseUtils.ontwiceBackPressed(this,2000);
    }
}
