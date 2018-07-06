package com.gd.guandou.guandou.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.gd.guandou.guandou.zxinglibrary.android.CaptureActivity;
import com.gd.guandou.guandou.zxinglibrary.bean.ZxingConfig;
import com.gd.guandou.guandou.zxinglibrary.common.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;





@ContentView(R.layout.activity_shou_ye)
public class ShouYeActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.tv_sy_name)
    private TextView tv_sy_name;

    @ViewInject(R.id.tv_sy_points)
    private TextView tv_sy_points;

    @ViewInject(R.id.ll_bottom_my)
    private LinearLayout ll_bottom_my;

    @ViewInject(R.id.ll_bottom_main)
    private LinearLayout ll_bottom_main;

    @ViewInject(R.id.ll_bottom_trade)
    private LinearLayout ll_bottom_trade;

    @ViewInject(R.id.ll_bottom_friend)
    private LinearLayout ll_bottom_friend;

    @ViewInject(R.id.tv_sy_beans)
    private TextView tv_sy_beans;

    @ViewInject(R.id.tv_sy_bottom)
    private TextView tv_sy_bottom;

    @ViewInject(R.id.iv_sy_bottom)
    private ImageView iv_sy_bottom;

    @ViewInject(R.id.civ_sy_headPhoto)
    private CircleImageView civ_sy_headPhoto;

    private   byte[] img;

    private final int REQUEST_CODE_SCAN = 111;//扫一扫

    private boolean isOn=false;//宝藏是否开启

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //setContentView(R.layout.activity_shou_ye);
        //添加ACTIVITY到栈堆中
        AppManager.getAppManager().addActivity(this);
        init();
    }


    private void init() {
        tv_sy_bottom.setTextColor(ContextCompat.getColor(ShouYeActivity.this, R.color.colorblue));
        iv_sy_bottom.setImageResource(R.mipmap.index_icon_at);
        ll_bottom_my.setOnClickListener(this);
        ll_bottom_trade.setOnClickListener(this);
        ll_bottom_friend.setOnClickListener(this);

        getUserInfo();
       getUserPoints();
        getbeansInfo();
        getHeadPicture();
    }

    //获取用户信息
    private void getUserInfo() {
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_user_info);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.addBodyParameter("accessToken", SharedPreferencesUtil.getString(ShouYeActivity.this,"accessToken",""));
        params.addHeader("charset","utf-8");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        tv_sy_name.setText(jobject.getJSONObject("data").getString("username"));

                        try {

                            if (jobject.getJSONObject("data").getString("isOn").equals("0")){
                                isOn=true;
                            }else{
                                isOn=false;
                            }
                        }catch (Exception e){

                        }

                    }else{
                        tv_sy_name.setText("...");
                        Toast.makeText(ShouYeActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ShouYeActivity.this,"获取用户信息错误!",Toast.LENGTH_SHORT).show();
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



    //获取用户积分
    private void getUserPoints() {
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_user_BM);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.addBodyParameter("accessToken", SharedPreferencesUtil.getString(ShouYeActivity.this,"accessToken",""));
        params.addHeader("charset","utf-8");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        if (jobject.getJSONObject("data").has("point_count")) {
                            tv_sy_points.setText(jobject.getJSONObject("data").getString("point_count"));
                        }
                    }else{
                        tv_sy_points.setText("...");
                        Toast.makeText(ShouYeActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ShouYeActivity.this,"获取用户积分信息错误!",Toast.LENGTH_SHORT).show();
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
        params.addBodyParameter("accessToken", SharedPreferencesUtil.getString(ShouYeActivity.this,"accessToken",""));
        params.addHeader("charset","utf-8");
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        tv_sy_beans.setText(jobject.getJSONObject("data").getString("gdnumber"));
                    }else{
                        tv_sy_beans.setText("...");
                        Toast.makeText(ShouYeActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ShouYeActivity.this,"获取贯豆信息错误!",Toast.LENGTH_SHORT).show();
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
        params.addBodyParameter("user_id", SharedPreferencesUtil.getString(ShouYeActivity.this,"user_id",""));
        params.addHeader("charset","utf-8");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){

                        img= ElseUtils.hex2byte(jobject.getString("data"));
                        if (img!=null){
                            civ_sy_headPhoto.setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.length));
                        }
                    }else{
                        Toast.makeText(ShouYeActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ShouYeActivity.this,"获取用户头像信息错误!",Toast.LENGTH_SHORT).show();
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
            case R.id.ll_bottom_my:
                IntentUtil.goIntent(ShouYeActivity.this,MyWealthActivity.class,null,true);
                break;
            //贯豆指数
            case R.id.ll_bottom_trade:
                IntentUtil.goIntent(ShouYeActivity.this,ValueIndexActivity.class,null,true);
                break;
            //朋友
            case R.id.ll_bottom_friend:
//                IntentUtil.goIntent(ShouYeActivity.this,ValueIndexActivity.class,null,true);
               T.cshow(ShouYeActivity.this,"功能即将启用！",1000);
                break;

            //支付
            case R.id.ll_sy_payment:
                T.cshow(ShouYeActivity.this,"功能即将启用！",1000);
                break;
            //转赠
            case R.id.ll_sy_give:
                T.cshow(ShouYeActivity.this,"功能即将启用！",1000);
                break;
            //充值
            case R.id.ll_sy_recharge:
                T.cshow(ShouYeActivity.this,"功能即将启用！",1000);
                break;
            //种豆游戏
            case R.id.iv_sy_game_farm:
                IntentUtil.goIntent(ShouYeActivity.this,GameFarmActivity.class,null,false);
                break;
            //寻宝游戏
            case R.id.iv_sy_game_treasure:
                if (isOn){
                    IntentUtil.goIntent(ShouYeActivity.this,GameTreasureActivity.class,null,false);
                }else{
                    T.cshow(ShouYeActivity.this,"系统暂未开放该功能！",1000);

                }
                break;

            //消费记录
            case R.id.tv_sy_purchase_history:
                IntentUtil.goIntent(ShouYeActivity.this,PurchaseHistoryActivity.class,null,false);
                break;

            case R.id.iv_sy_right_top_icon:
//                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.gd.guandou.guandou");
//                startActivity(LaunchIntent);
                break;
            //扫一扫
            case R.id.ll_sy_scan:
                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                * 也可以不传这个参数
                * 不传的话  默认都为默认不震动  其他都为true
                * */
            ZxingConfig config = new ZxingConfig();
            config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
            config.setPlayBeep(false);//是否播放提示音
            config.setShake(false);//是否震动
            config.setShowAlbum(true);//是否显示相册
            config.setShowFlashLight(false);//是否显示闪光灯


            //如果不传 ZxingConfig的话，两行代码就能搞定了
                Intent intent = new Intent(ShouYeActivity.this, CaptureActivity.class);
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                String contentBitmap = data.getStringExtra(Constant.CODED_BITMAP);
                if (!TextUtils.isEmpty(content)){
                    try {
                        JSONObject jsonObject=new JSONObject(content);
//                        if (jsonObject.has("id")&&jsonObject.getString("id").equals("dao333")){
                        Bundle bundle=new Bundle();
                        bundle.putString(Content.QRCodeRusult,jsonObject.toString());
//                       T.show(ShouYeActivity.this,content,1000);
                        IntentUtil.goIntent(ShouYeActivity.this,ScanFoodSInfoActivity.class,bundle,false);
//                        }else{
//                            T.show(ShouYeActivity.this,"该二维码不是指定商城二维码",1000);
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        T.show(ShouYeActivity.this,"该二维码不是指定商城二维码",1000);
                    }
                }
//                Log.e("Stirng",content+"");

            }
        }
    }

    //监听返回键
    @Override
    public void onBackPressed() {
        //super.onBackPressed()会自动调用finish()方法,关闭
//        super.onBackPressed();

      ElseUtils.ontwiceBackPressed(this,2000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }
}
