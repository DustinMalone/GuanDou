package com.gd.guandou.guandou.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.adapter.GameTreasureAdapter;
import com.gd.guandou.guandou.bean.GameTreasure;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.AutoDialogUtil;
import com.gd.guandou.guandou.untils.HttpUtils;
import com.gd.guandou.guandou.untils.IntentUtil;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;
import com.gd.guandou.guandou.view.CenterPassWordDialog;
import com.gd.guandou.guandou.xListview.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_game_treasure)
public class GameTreasureActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.xlv_game_treasure_xlist)
    private XListView xlv_game_treasure_xlist;

    private GameTreasureAdapter gameTreasureAdapter;

    private List<GameTreasure> datalist;

    private Context context;

    @ViewInject(R.id.tv_game_treasure_gd)
    private TextView tv_game_treasure_gd;

    //保存贯豆数量
    private String GDnum="0";

    @ViewInject(R.id.tv_game_treasure_yjf)
    private TextView tv_game_treasure_yjf;


    @ViewInject(R.id.tv_game_treasure_sumjf)
    private TextView tv_game_treasure_sumjf;

    private CenterPassWordDialog centerPassWordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        context=this;
        AppManager.getAppManager().addActivity(this);
        // setContentView(R.layout.activity_game_farm);
        init();
    }

    private void init() {
        datalist=new ArrayList<>();

        getGameGD();
        getProfit();
        getGDGainJNrecoreds();



        xlv_game_treasure_xlist.setPullRefreshEnable(false);
        xlv_game_treasure_xlist.setPullLoadEnable(false);
        xlv_game_treasure_xlist.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xlv_game_treasure_xlist.stopLoadMore();
                        gameTreasureAdapter.notifyDataSetChanged();
                    }
                }, 2000);



            }
        });
    }

    /**
     * 获取分红记录
     * @param
     */
    public void getGDGainJNrecoreds(){
        final JSONObject data =new JSONObject();
        try {
//            data.put("type","1");
//            data.put("recource","藏宝游戏分红贝母");
            data.put("userId", SharedPreferencesUtil.getString(context,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_FHRecords, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        Log.e("getGDGainJNrecoreds",jobject.toString());
                        if (jobject.getJSONArray("data").length()!=0){
                            JSONArray jsonoArray=jobject.getJSONArray("data");
                            Gson gson = new Gson();
                                datalist= gson.fromJson(jsonoArray.toString(),new TypeToken<List<GameTreasure>>(){}.getType());
                        }
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
                gameTreasureAdapter=new GameTreasureAdapter(GameTreasureActivity.this,datalist);
                xlv_game_treasure_xlist.setAdapter(gameTreasureAdapter);
            }
        });

    }
    /**
     * 查询藏宝贯豆的数量
     * @param
     */
    public void getGameGD(){
            JSONObject data =new JSONObject();
            try {
                data.put("user_id", SharedPreferencesUtil.getString(context,"user_id",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_HideGDCounts, data.toString(), new Callback.CacheCallback<String>() {

                @Override
                public boolean onCache(String result) {
                    return false;
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jobject = new JSONObject(result);
                        if (jobject.get("code").toString().equals("0")){
                            GDnum=jobject.getJSONObject("data").get("gdnumber").toString();
                            tv_game_treasure_gd.setText(jobject.getJSONObject("data").get("cbnumber").toString());
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
     * 查询累计收益和昨日收益
     * @param
     */
    public void getProfit(){
        JSONObject data =new JSONObject();
        try {
            data.put("ownerId", SharedPreferencesUtil.getString(context,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_FindInComeInfo, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){

                        tv_game_treasure_yjf.setText(jobject.getJSONObject("data").get("yesterdaySy").toString());

                        tv_game_treasure_sumjf.setText(jobject.getJSONObject("data").get("sumSy").toString());

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
     * 贯豆转入转出
     * @param
     */
    public void changeOrTurnGD(int type,String number){
        JSONObject data =new JSONObject();
        try {
            data.put("number",number);
            data.put("type",type);
            data.put("user_id", SharedPreferencesUtil.getString(context,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_ChanceOrTurn, data.toString(), new Callback.CacheCallback<String>() {

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
                init();
                AutoDialogUtil.dismissScanNumberDialog();
            }
        });

    }


    //转入转出
    private void exchange(final int type, final String gd){
        centerPassWordDialog=new CenterPassWordDialog();
        centerPassWordDialog.setConvertViewOnClickListener(new CenterPassWordDialog.ConvertViewListener() {
            @Override
            public void ViewListener(String pass) {
                centerPassWordDialog.dismiss();
                checkPaymentPass(pass,type,gd);

            }
        });
        centerPassWordDialog.initDialog(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerPassWordDialog.dismiss();
            }
        });

    }


    /**
     *验证支付密码
     */
    private void checkPaymentPass(String pass, final int type, final String gd){
        JSONObject data =new JSONObject();

        try {
            data.put("password",pass);
            data.put("user_id", SharedPreferencesUtil.getString(context,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_VerifyPwd, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")) {
                        changeOrTurnGD(type,gd);
                    }else{

                        exchange(type,gd);
                        T.show(context,jobject.get("message").toString(),1500);
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
     * 检查是否设置支付密码
     * @param
     */
    private void isSetMM(final int t){
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
                            if (t==0) {
                                AutoDialogUtil.showScanNumberDialog(context, "转入", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if (Double.parseDouble(AutoDialogUtil.getEditTextValue())>=1&&Double.parseDouble(AutoDialogUtil.getEditTextValue()) <= Double.parseDouble(GDnum)) {
//                            changeOrTurnGD(0, AutoDialogUtil.getEditTextValue());
                                            exchange(0, AutoDialogUtil.getEditTextValue());
                                        } else {
                                            T.cshow(context, "贯豆不足或转入贯豆数量必须大于等于1", 1000);
                                        }
                                    }
                                });
                                AutoDialogUtil.setEditTextHintValue("可以转入" + GDnum + "贯豆");
                            }else{
                                AutoDialogUtil.showScanNumberDialog(context, "转出", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try{
                                            Double.parseDouble(tv_game_treasure_gd.getText().toString());
                                        }catch (Exception e){
                                            T.cshow(context,"服务繁忙",1000);
                                            return;
                                        }
                                        if (Double.parseDouble(AutoDialogUtil.getEditTextValue())>=1&&Double.parseDouble(AutoDialogUtil.getEditTextValue())<=Double.parseDouble(tv_game_treasure_gd.getText().toString())) {
//                            changeOrTurnGD(1,AutoDialogUtil.getEditTextValue());
                                            exchange(1,AutoDialogUtil.getEditTextValue());
                                        }else {
                                            T.cshow(context,"贯豆不足或转出贯豆数量必须大于等于1",1000);
                                        }
                                    }
                                });
                                AutoDialogUtil.setEditTextHintValue("可以转出"+tv_game_treasure_gd.getText().toString()+"贯豆");

                            }
                        }
                        else {
                            T.show(context,"请设置支付密码！",1000);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回键
            case R.id.iv_game_treasure_back:
                IntentUtil.goIntent(GameTreasureActivity.this,ShouYeActivity.class,null,true);
                break;
            //返回键
            case R.id.tv_game_treasure_detail:
                IntentUtil.goIntent(GameTreasureActivity.this,GameTreasureDetailActivity.class,null,false);
                break;
            //贯豆转入
            case R.id.btn_game_treasure_in:
                //转入:0
                isSetMM(0);
                break;
            //贯豆转出
            case R.id.btn_game_treasure_out:
                //转出:1
                isSetMM(1);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
