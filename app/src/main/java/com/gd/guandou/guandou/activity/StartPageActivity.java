package com.gd.guandou.guandou.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.zxinglibrary.common.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;


public class StartPageActivity extends AppCompatActivity {
    private  Intent it;
    private Context context;

    private Timer timer;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        context=this;
        SharedPreferencesUtil.putString(context, Content.isShowUpdate,"");
        it = new Intent(this, MainActivity.class); //你要转向的Activity
         timer = new Timer();
         task = new TimerTask() {
            @Override
            public void run() {
                startActivity(it); //执行
                finish();
            }
        };

        //判断正常打开还是从别的APP启动
        if (isStartByPage()){
            finish();

            //获取指定参数值
            Uri uri = getIntent().getData();
            String tk = uri.getQueryParameter("tk");
            SharedPreferencesUtil.putString(context,"accessToken",tk);
            Log.e("tk", "tk: " + tk);
            String id = uri.getQueryParameter("id");
            SharedPreferencesUtil.putString(context,"user_id",id);
            Log.e("id", "id: " + id);

            //启动APP
//                Log.e("check","is");
//                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.gd.guandou.guandou");
//                Bundle bundle = new Bundle();
//                bundle.putString("user_id", "user_id");
//                bundle.putString("accessToken", "accessToken");
//                LaunchIntent.putExtras(bundle);
//                startActivity(LaunchIntent);


        }

         if(null==SharedPreferencesUtil.getString(this,"accessToken","")||
                SharedPreferencesUtil.getString(this,"accessToken","").equals("")){
           it = new Intent(this, MainActivity.class); //你要转向的Activity
            timer.schedule(task, 1000 * 2); //2秒后
        }else {
            checkToken();
        }

        //获取从页面返回的数据

//        Bundle bundle=getIntent().getExtras();
//        if (bundle!=null){
//            bundle.getString("user_id");
//            bundle.getString("accessToken");
//            Log.e("bundle",bundle.toString());
//        }

    }

    /**
     * 判断是否由页面启动APP
     */
    public boolean isStartByPage(){
        Intent result = getIntent();
        String scheme = result.getScheme();
        Uri uri = result.getData();
        String host="";
        if (uri!=null){
            host= uri.getHost();
            Log.e("host",host+"");
        }
        Log.e("scheme",scheme+"");

        //判断是否有指定页面转入
        if (null!=scheme&&null!=host&&scheme.equals("gd")&&host.equals("startPage")){
            return  true;
        }

        return false;
    }


    /**
     * 验证TOKEN
     */
    public void checkToken(){
        RequestParams requestParams=new RequestParams(Content.GDAPI+Content.checkToken+"/accessToken="+SharedPreferencesUtil.getString(this,"accessToken",""));
        requestParams.setConnectTimeout(10000);
        requestParams.addHeader("charset","utf-8");
        requestParams.addHeader("Content-Type","application/x-www-form-urlencoded");
//        requestParams.addHeader("Content-Type","application/json");
//        requestParams.setAsJsonContent(true);
//        requestParams.addBodyParameter("accessToken",SharedPreferencesUtil.getString(this,"accessToken","") );
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(result);
                    if (!jsonObject.has("code")) {
//                        T.show(context,jsonObject.getString("msg"),1000);
                        it = new Intent(context, MainActivity.class); //你要转向的Activity

                    }else if(jsonObject.getInt("code")!=0){
//                        T.show(context,jsonObject.getString("msg"),1000);
                        it = new Intent(context, MainActivity.class); //你要转向的Activity

                    }
                    else {
                        it = new Intent(context, ShouYeActivity.class); //你要转向的Activity

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                timer.schedule(task, 1000 * 2); //2秒后
            }
        });
    }


}
