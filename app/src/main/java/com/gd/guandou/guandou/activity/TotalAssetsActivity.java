package com.gd.guandou.guandou.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

@ContentView(R.layout.activity_total_assets)
public class TotalAssetsActivity extends AppCompatActivity implements View.OnClickListener{

    private Context context;

    @ViewInject(R.id.tv_total_assets_gd)
    private TextView tv_total_assets_gd;

    @ViewInject(R.id.tv_total_assets_jf)
    private TextView tv_total_assets_jf;

    @ViewInject(R.id.tv_total_assets_mygd)
    private TextView tv_total_assets_mygd;

    @ViewInject(R.id.tv_total_assets_hidegd)
    private TextView tv_total_assets_hidegd;

    @ViewInject(R.id.pv_total_assets_info)
    private PieChartView pv_total_assets_info;

    //数据
    private PieChartData pieChardata;

    List<SliceValue> values = new ArrayList<SliceValue>();

    private int[] colorData;

    private double[] valueData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        context=this;

        init();
    }

    private void init() {
        getTotalAssetsInfo();


    }

    /**
     * 初始化饼图
     */
    private void initPieChart() {
        pieChardata = new PieChartData();
        pieChardata.setHasLabels(true);//显示表情
        pieChardata.setHasLabelsOnlyForSelected(true);//不用点击显示占的百分比
        pieChardata.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
        pieChardata.setHasCenterCircle(true);//是否是环形显示
        pieChardata.setValues(values);//填充数据
        pieChardata.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        pieChardata.setCenterCircleScale(0.5f);//设置环形的大小级别
        pv_total_assets_info.setPieChartData(pieChardata);
        pv_total_assets_info.setValueSelectionEnabled(true);//选择饼图某一块变大
        pv_total_assets_info.setAlpha(0.9f);//设置透明度
        pv_total_assets_info.setCircleFillRatio(1f);//设置饼图大小
        pv_total_assets_info.startDataAnimation(2500);

    }

    /**
     * 获取数据
     */

    private void setPieChartData() {
        colorData=new int[]{
                ContextCompat.getColor(context,R.color.colorred),
                ContextCompat.getColor(context,R.color.colororange)
        };

        try {
            valueData=new double[]{
                    Double.parseDouble(tv_total_assets_mygd.getText().toString()),
                    Double.parseDouble(tv_total_assets_hidegd.getText().toString())
            };
        }catch (Exception e){
            valueData=new double[]{};
        }

        for (int i = 0; i < valueData.length; i++) {
//            Log.e("valueData1",valueData[i]+"");
//            Log.e("valueData1",(float)345456.98+"");
            SliceValue sliceValue = new SliceValue((float) valueData[i], colorData[i]);
            values.add(sliceValue);
        }


    }


    /**
     * 获取总资产信息
     * @param
     */
    private void getTotalAssetsInfo(){
        JSONObject data =new JSONObject();
        try {
            data.put("accessToken", SharedPreferencesUtil.getString(context,"accessToken",""));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_FindTotalAssetsInfo, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);

                    if (jobject.get("code").toString().equals("0")){
                        tv_total_assets_jf.setText(jobject.getJSONObject("data").getString("pointcount").toString());
                        tv_total_assets_mygd.setText(jobject.getJSONObject("data").getString("gdnumber").toString());
                        tv_total_assets_hidegd.setText(jobject.getJSONObject("data").getString("cbnumber").toString());
                        tv_total_assets_gd.setText(jobject.getJSONObject("data").getDouble("cbnumber")+jobject.getJSONObject("data").getDouble("gdnumber")+"");
                    }else{
                        T.show(context,jobject.get("message").toString(),1000);
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
                setPieChartData();
                initPieChart();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }

    }


    //返回键返回
    public void backFinish(View v) {
        finish();

    }
}
