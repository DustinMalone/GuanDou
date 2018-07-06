package com.gd.guandou.guandou.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.adapter.PurchaseHistoryAdapter;
import com.gd.guandou.guandou.bean.PurchashHistory;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.DateUtils;
import com.gd.guandou.guandou.untils.HttpUtils;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;
import com.gd.guandou.guandou.view.PinnedSectionListView;
import com.gd.guandou.guandou.view.UpPullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gd.guandou.guandou.content.Content.gd_user_FindConsumeRecords;
import static com.gd.guandou.guandou.untils.DateUtils.getdatebymyself;

@ContentView(R.layout.activity_purchase_history)
public class PurchaseHistoryActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.lv_purchase_history_bill)
    private PinnedSectionListView lv_purchase_history_bill;

    private PurchaseHistoryAdapter ph_adapter;

    private List<PurchashHistory> lv_data;

    @ViewInject(R.id.swrl_refresh)
    private UpPullRefreshLayout swrl_refresh;

    //临时保存加载月份
    private List<String> monthList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        
        init();
    }


    private void init() {
        monthList=new ArrayList<>();
        lv_data=new ArrayList<>();
//        for (int i=0;i<4;i++){
//            PurchashHistory purchashHistory=new PurchashHistory();
//            if (i%2==0){
//                purchashHistory.setTitle(i+"月");
//            }else{
//                purchashHistory.setDate("09/09");
//                purchashHistory.setTime("09:54");
//                purchashHistory.setGoodsName("是打开方式");
//                purchashHistory.setGoodsPrices("-80.00");
//                purchashHistory.setTitle("");
//            }
//
//            lv_data.add(purchashHistory);
//        }
        getRecords();



        // 设置下拉刷新时的颜色值,颜色值需要定义在xml中
        swrl_refresh.setColorSchemeResources(R.color.colorAccent,
                        R.color.coloryellow, R.color.colorblue,
                        R.color.colorred);
        // 设置下拉刷新监听器
        swrl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swrl_refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 更新数据

                        // 更新完后调用该方法结束刷新
                        swrl_refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        // 加载监听器
        swrl_refresh.setOnLoadListener(new UpPullRefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                swrl_refresh.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        //加载数据
                            getPreMonthPurchaseData();
                        // 加载完后调用该方法
                        swrl_refresh.setLoading(false);
                    }
                }, 1500);

            }
        });
    }


    //获取消费记录
    public void getRecords(){
        JSONObject data=new JSONObject();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String date=format.format(DateUtils.getNow());
        monthList.add(date);
        try {
            data.put("createDate",date);
            data.put("userId", SharedPreferencesUtil.getString(PurchaseHistoryActivity.this,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.e("data",data.toString());
        HttpUtils.xPostJson(PurchaseHistoryActivity.this, Content.GDAPI+ gd_user_FindConsumeRecords
                , data.toString(), new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jobject = new JSONObject(result);

                            if (jobject.get("code").toString().equals("0")){
                                PurchashHistory np=new PurchashHistory();
                                Date now=new Date();
                                np.setTitle(DateUtils.getdatebymyself(now, "MM")+"月消费记录");
                                np.setnDate(DateUtils.getdatebymyself(now, "yyyy-MM-dd"));
                                lv_data.add(np);
                               addRecordsdata(jobject.getJSONArray("data"));
                                ph_adapter=new PurchaseHistoryAdapter(PurchaseHistoryActivity.this,lv_data);
                                lv_purchase_history_bill.setAdapter(ph_adapter);
                            }else{
                                T.show(PurchaseHistoryActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT);
                            }
                        } catch (JSONException e) {
                            T.show(PurchaseHistoryActivity.this,"JSON解析错误!",Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (ex instanceof HttpException) { // 网络错误
                            // ...
                            T.show(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG);

                        } else { // 其他错误
                            // ...
                            T.show(x.app(), "服务繁忙", Toast.LENGTH_LONG);
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
     * 添加记录参数
     * @param data json数组
     */
    private void addRecordsdata(JSONArray data) {
//        Log.e("Predata",data.toString());
        if (data.length()==0){
//            Log.e("jobjectsss",data.toString());
            lv_data.remove(lv_data.size()-1);
            getPreMonthPurchaseData();
            return;
        }
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                PurchashHistory p = new PurchashHistory();
                if (object.has("recource")) {
                    p.setGoodsName(object.getString("recource"));
                }
                long createdate = object.getLong("createDate");
                Date date = new Date(createdate);
                p.setDate(getdatebymyself(date, "MM-dd"));
                p.setTime(getdatebymyself(date, "HH:mm"));
                p.setnDate(DateUtils.getdatebymyself(date, "yyyy-MM-dd"));

                //获取商品价格
                String GoodsPrices = "";
                if (object.getInt("type") == 5) {
                    //贯豆兑换贝母
                    if (object.has("gdNum")) {
                        GoodsPrices += "-" + object.getString("gdNum") + "贯豆";
                    }
                    if (object.has("jfNum")) {
                        GoodsPrices += ",+" + object.getString("jfNum") + "贝母";
                    }

                }else if(object.getInt("type") == 4){
                    //贝母兑换贯豆
                    if (object.has("jfNum")) {
                        GoodsPrices += "-" + object.getString("jfNum") + "贝母";
                    }
                    if (object.has("gdNum")) {
                        GoodsPrices += ",+" + object.getString("gdNum") + "贯豆";
                    }

                }
                else if(object.getInt("type") == 3){
                    //获取贯豆
                    if (object.has("gdNum")) {
                        GoodsPrices += "+" + object.getString("gdNum") + "贯豆";
                    }
                }
                else if(object.getInt("type") == 2){
                    //消费贯豆
                    if (object.has("gdNum")) {
                        GoodsPrices += "-" + object.getString("gdNum") + "贯豆";
                    }
                }
                else if(object.getInt("type") == 1){
                    //获取贝母
                    if (object.has("jfNum")) {
                        GoodsPrices += "+" + object.getString("jfNum") + "贝母";
                    }
                }
                else if(object.getInt("type") == 0){
                    if (object.has("jfNum")) {
                        GoodsPrices = "-" + object.getString("jfNum") + "贝母";
                    }
                }
                p.setGoodsPrices(GoodsPrices);
                lv_data.add(p);
            }
        }catch(Exception e){
        }
    }

    /**
     * 获取前一个月的数据
     * @param
     */
    public void getPreMonthPurchaseData(){
        JSONObject data=new JSONObject();
//          String date=lv_data.get(lv_data.size()-1).getnDate();
            String date=monthList.get(monthList.size()-1);
        Log.e("monthList",date);
        try {
            //最多获取1年前的记录
        if (DateUtils.getMonthCountsByDateAndDateT(date,DateUtils.getStringDateShort())>6){
            T.show(PurchaseHistoryActivity.this,"已加载到最后一条",1000);
            return;
        }
            date=DateUtils.getPreMonthDate(date);
            Log.e("getPreMonthPurchaseData",date);
            monthList.add(date);
            data.put("createDate",date);
            data.put("userId", SharedPreferencesUtil.getString(PurchaseHistoryActivity.this,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Log.e("getPreMonthPurchaseData",data.toString());
        final String finalDate = date;
        HttpUtils.xPostJson(PurchaseHistoryActivity.this, Content.GDAPI+Content.gd_user_FindConsumeRecords
                , data.toString(), new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jobject = new JSONObject(result);
                            if (jobject.get("code").toString().equals("0")){
                                PurchashHistory np=new PurchashHistory();
//                                Log.e("finalDate",DateUtils.getdatebymyself(finalDate,"yyyy")+"="+DateUtils.getdatebymyself(lv_data.get(0).getnDate(),"yyyy"));
                                if (DateUtils.getdatebymyself(finalDate,"yyyy").equals(DateUtils.getdatebymyself(DateUtils.getStringDateShort(),"yyyy"))){
                                np.setTitle(DateUtils.getdatebymyself(finalDate, "MM")+"月消费记录");
                                }else{
                                    np.setTitle(DateUtils.getdatebymyself(finalDate, "yy年MM")+"月消费记录");
                                }
//                                Log.e("finalDates",np.getTitle());
                                np.setnDate(DateUtils.getdatebymyself(finalDate, "yyyy-MM-dd"));
                                lv_data.add(np);
                                addRecordsdata(jobject.getJSONArray("data"));
                                ph_adapter.notifyDataSetChanged();
                            }else{
                                T.show(PurchaseHistoryActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT);
                            }
                        } catch (JSONException e) {
                            T.show(PurchaseHistoryActivity.this,"JSON解析错误!",Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (ex instanceof HttpException) { // 网络错误
                            // ...
                            T.show(x.app(), "网络异常"+ex.getMessage(), Toast.LENGTH_LONG);

                        } else { // 其他错误
                            // ...
                            T.show(x.app(), "服务繁忙", Toast.LENGTH_LONG);
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
            //返回按钮
            case R.id.iv_purchase_history_back:
                finish();
                break;
        }
    }
}
