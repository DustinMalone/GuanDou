package com.gd.guandou.guandou.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.AutoDialogUtil;
import com.gd.guandou.guandou.untils.ElseUtils;
import com.gd.guandou.guandou.untils.HttpUtils;
import com.gd.guandou.guandou.untils.IntentUtil;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;
import com.gd.guandou.guandou.view.CenterPassWordDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

//贯豆价值指数
@ContentView(R.layout.activity_value_index)
public class ValueIndexActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.ll_bottom_my)
    private LinearLayout ll_bottom_my;

    @ViewInject(R.id.ll_bottom_main)
    private LinearLayout ll_bottom_main;

    @ViewInject(R.id.ll_bottom_trade)
    private LinearLayout ll_bottom_trade;

    @ViewInject(R.id.ll_bottom_friend)
    private LinearLayout ll_bottom_friend;

    @ViewInject(R.id.lc_value_index_chart)
    private LineChartView lc_value_index_chart;

    @ViewInject(R.id.tv_value_index_guandou)
    private TextView tv_value_index_guandou;

    @ViewInject(R.id.tv_value_index_num)
    private TextView tv_value_index_num;

    @ViewInject(R.id.tv_value_index_min)
    private TextView tv_value_index_min;

    @ViewInject(R.id.tv_value_index_hour)
    private TextView tv_value_index_hour;

    @ViewInject(R.id.tv_value_index_day)
    private TextView tv_value_index_day;

    @ViewInject(R.id.tv_value_index_week)
    private TextView tv_value_index_week;

    @ViewInject(R.id.tv_trade_bottom)
    private TextView tv_trade_bottom;

    @ViewInject(R.id.iv_trade_bottom)
    private ImageView iv_trade_bottom;

    //定义 日，周，分，小时 视图数组
    private TextView[] textViews;

    private LineChartData  lineData;

    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    private Context context;


    private CenterPassWordDialog centerPassWordDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
//        setContentView(R.layout.activity_value_index);
        context=this;
        init();
    }

    private void init() {
        lineData=new LineChartData();


        tv_trade_bottom.setTextColor(ContextCompat.getColor(ValueIndexActivity.this, R.color.colorblue));
        iv_trade_bottom.setImageResource(R.mipmap.exponent_icon_at);
        ll_bottom_main.setOnClickListener(this);
        ll_bottom_my.setOnClickListener(this);
        ll_bottom_friend.setOnClickListener(this);

        //设置分线为选中
        tv_value_index_min.setEnabled(false);
        tv_value_index_min.setTextColor(Color.WHITE);
        //初始化数组
        textViews=new TextView[4];
        textViews[0]=tv_value_index_min;
        textViews[1]=tv_value_index_hour;
        textViews[2]=tv_value_index_day;
        textViews[3]=tv_value_index_week;

        getbeansInfo();
        getGDvalue();
        getGBValueLine(5);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //首页
            case R.id.ll_bottom_main:
                IntentUtil.goIntent(ValueIndexActivity.this,ShouYeActivity.class,null,true);
                break;
            //我的财富
            case R.id.ll_bottom_my:
                IntentUtil.goIntent(ValueIndexActivity.this,MyWealthActivity.class,null,true);
                break;
            //朋友
            case R.id.ll_bottom_friend:
//                  IntentUtil.goIntent(ShouYeActivity.this,ValueIndexActivity.class,null,true);
                T.cshow(context,"功能即将启用！",1000);
                break;

            //朋友
            case R.id.tv_value_index_business:
                T.cshow(context,"功能即将启用！",1000);
                break;

            //5日线
            case R.id.tv_value_index_min:
                changeState(tv_value_index_min);
                getGBValueLine(5);
                break;
            //10日线
            case R.id.tv_value_index_hour:
                changeState(tv_value_index_hour);
                getGBValueLine(10);
                break;
            //15日线
            case R.id.tv_value_index_day:
                changeState(tv_value_index_day);
                getGBValueLine(15);
                break;
            //30日线
            case R.id.tv_value_index_week:
                changeState(tv_value_index_week);
                getGBValueLine(30);
                break;

            //兑换按钮
            case R.id.btn_value_index_exchange:
                if (0 == Double.parseDouble(tv_value_index_num.getText().toString())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context).setMessage("当前价值指数为0,确定兑换吗?")

                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    isSetMM();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }else{
                    isSetMM();
                }

                break;
        }
    }

    /**
    *改变 日，小时，分，周 线 状态
     */
    public void changeState(TextView textView){
        for (TextView t:textViews){
            t.setEnabled(true);
        }
        textView.setEnabled(false);
        textView.setTextColor(Color.WHITE);
    }


    /**
     * 获取用户贯豆信息
     */

    private void getbeansInfo() {
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_user_GD);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.addBodyParameter("accessToken", SharedPreferencesUtil.getString(ValueIndexActivity.this,"accessToken",""));
        params.addHeader("charset","utf-8");
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        tv_value_index_guandou.setText(jobject.getJSONObject("data").getString("gdnumber"));
                    }else{
                        Toast.makeText(ValueIndexActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ValueIndexActivity.this,"获取贯豆JSON解析错误!",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(x.app(), "获取贯豆信息错误："+ex.getMessage(), Toast.LENGTH_LONG).show();
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
     *   获取贯豆指数
     */
    private void getGDvalue(){
        JSONObject data =new JSONObject();
        try {
            data.put("accessToken", SharedPreferencesUtil.getString(ValueIndexActivity.this,"accessToken",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(ValueIndexActivity.this, Content.GDAPI+Content.gd_user_GetGDValueIndex, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")){
                        tv_value_index_num.setText(jobject.getJSONObject("data").getString("number"));
                    }else{
                        Toast.makeText(ValueIndexActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ValueIndexActivity.this,"JSON解析错误!",Toast.LENGTH_SHORT).show();
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


    //获取贯豆兑换贝母
    private void exchangePoints(final String gd){
          centerPassWordDialog=new CenterPassWordDialog();
        centerPassWordDialog.setConvertViewOnClickListener(new CenterPassWordDialog.ConvertViewListener() {
            @Override
            public void ViewListener(String pass) {
                centerPassWordDialog.dismiss();
                    checkPaymentPass(pass,gd);

            }
        });
        centerPassWordDialog.initDialog(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerPassWordDialog.dismiss();
            }
        });

    }

    //兑换贝母
    private void trueExchangePoints(String gd){
        JSONObject data =new JSONObject();
        try {
            data.put("accessToken", SharedPreferencesUtil.getString(ValueIndexActivity.this,"accessToken",""));
            data.put("value",gd);

            Log.e("exgd",data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(ValueIndexActivity.this, Content.GDAPI+Content.gd_user_GDExchangeBM, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    Toast.makeText(ValueIndexActivity.this,jobject.get("message").toString(),Toast.LENGTH_SHORT).show();
                    getbeansInfo();

                } catch (JSONException e) {
                    Toast.makeText(ValueIndexActivity.this,"JSON解析错误!",Toast.LENGTH_SHORT).show();
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
     *验证支付密码
     */
    private void checkPaymentPass(String pass, final String gd){
        JSONObject data =new JSONObject();

        try {
            data.put("password",pass);
            data.put("user_id", SharedPreferencesUtil.getString(ValueIndexActivity.this,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(ValueIndexActivity.this, Content.GDAPI+Content.gd_user_VerifyPwd, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")) {

                        trueExchangePoints(gd);
                    }else{

                        exchangePoints(gd);
                        T.show(ValueIndexActivity.this,jobject.get("message").toString(),1500);
                    }
                } catch (JSONException e) {
                    Toast.makeText(ValueIndexActivity.this,"JSON解析错误!",Toast.LENGTH_SHORT).show();
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
                            AutoDialogUtil.showScanNumberDialog(ValueIndexActivity.this, "贯豆换取贝母", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId()==R.id.btn_hint_yes){

                                        if (Double.parseDouble(AutoDialogUtil.getEditTextValue())==0){
                                            T.show(v.getContext(),"请输入正确的数字",1000);
                                            return;
                                        }
//                            Toast.makeText(ValueIndexActivity.this,AutoDialogUtil.getEditTextValue(),Toast.LENGTH_SHORT).show();
                                        exchangePoints(AutoDialogUtil.getEditTextValue());
                                        AutoDialogUtil.dismissScanNumberDialog();
                                    }
                                }
                            });
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


    /**
    *获取实时指数图
     */
    private void getGBValueLine(int num){
        JSONObject data =new JSONObject();
        try {
            data.put("num",num);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(ValueIndexActivity.this, Content.GDAPI+Content.gd_user_GetGBValueIndexLine, data.toString(), new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jobject = new JSONObject(result);
                    if (jobject.get("code").toString().equals("0")) {
                        lineData=getData(jobject.getJSONArray("data"));

                    }else{
                        T.show(ValueIndexActivity.this,jobject.get("message").toString(),1500);
                    }
                } catch (JSONException e) {
                    Toast.makeText(ValueIndexActivity.this,"JSON解析错误!",Toast.LENGTH_SHORT).show();
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
                //显示折线图
                setupChart(lc_value_index_chart, lineData, Color.WHITE);

            }
        });
    }


    // 设置显示的样式
    public void setupChart(final LineChartView chart, LineChartData data, int color) {
        // if enabled, the chart will always start at zero on the y-axis
        //chart.setStartAtZero(false);

        //MPAnroidChart设置
        // disable the drawing of values into the chart
       //chart.setDrawYValues(false);

        //chart.setDrawBorder(false);

        // no description text
//        chart.setDescription(null);// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
//       chart.setNoDataTextDescription("You need to provide data for the chart.");

//        chart.setNoDataText("载入中");
        // enable / disable grid lines
//       chart.setDrawVerticalGrid(false); // 是否显示水平的表格
        // mChart.setDrawHorizontalGrid(false);

//        chart.getAxisRight().setEnabled(false);
        //
        // enable / disable grid background
//        chart.setDrawGridBackground(false); // 是否显示表格颜色
//        chart.setGridColor(Color.BLACK & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度
//        chart.setGridWidth(1.25f);// 表格线的线宽


        // enable touch gestures
//        chart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
//        chart.setDragEnabled(true);// 是否可以拖拽
//        chart.setScaleEnabled(true);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
//        chart.setPinchZoom(false);//

        chart.setBackgroundColor(color);// 设置背景

//       chart.setValueTypeface(Typeface.DEFAULT);// 设置字体

        // add data
//        chart.setData(data); // 设置数据
//        // get the legend (only possible after setting data)
//        Legend l = chart.getLegend(); // 设置标示，就是那个一组y的value的
//
//        // modify the legend ...
//        // l.setPosition(LegendPosition.LEFT_OF_CHART);
//        l.setForm(Legend.LegendForm.CIRCLE);// 样式
//        l.setFormSize(6f);// 字体
//        l.setTextColor(Color.BLACK);// 颜色
//        l.setTypeface(Typeface.DEFAULT);// 字体



//        YAxis y = chart.getAxisLeft(); // y轴的标示
//        y.setTextColor(Color.BLACK);
//        y.setTypeface(Typeface.DEFAULT);
//        y.setLabelCount(4,true); // y轴上的标签的显示的个数
//        y.setDrawGridLines(true);
//        y.setDrawAxisLine(true);
//        y.setAxisMinValue(0);




//        XAxis x = chart.getXAxis(); // x轴显示的标签
//        x.setTextColor(Color.BLACK);
//        x.setTypeface(Typeface.DEFAULT);
//        x.setPosition(XAxis.XAxisPosition.BOTTOM);
//        x.setDrawGridLines(false);

        // 隐藏纵横网格线
//        chart.getXAxis().setDrawGridLines(false);
//        chart.getAxisRight().setDrawGridLines(false);
//
//        YAxis rightAxis = chart.getAxisRight();
//        // 不显示图表的右边y坐标轴线
//        rightAxis.setEnabled(false);
//
//        // animate calls invalidate()...
//        chart.animateX(100); // 立即执行的动画,x轴


        //hellocharts设置

        chart.setZoomEnabled(true);//设置是否支持缩放
        chart.setInteractive(true);//设置图表是否可以与用户互动
        chart.setValueSelectionEnabled(true);//设置图表数据是否选中进行显示
        chart.setScrollEnabled(true);//设置是否可滑动。
        chart.setContentDescription("加载中");
        //Y轴
        Axis axisY = new Axis().setHasLines(true);// Y轴属性
        axisY.setHasLines(true);// 是否显示Y轴网格线
        axisY.setTextColor(Color.BLACK);// 设置Y轴文字颜色
        axisY.setLineColor(Color.BLACK);

        float maxlabel= 0;
        float minlabel= 0;
        if (mPointValues.size()>0) {
            maxlabel = mPointValues.get(0).getY();
            minlabel = mPointValues.get(0).getY();
            for (int j = 0; j < mPointValues.size(); j++) {
                if (mPointValues.get(j).getY() > maxlabel) {
                    maxlabel = mPointValues.get(j).getY();
                }
                if (mPointValues.get(j).getY() < minlabel) {
                    minlabel = mPointValues.get(j).getY();
                }
            }
        }

        java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
        List<AxisValue> values = new ArrayList<>();
        if (minlabel==maxlabel){
            minlabel=0f;
            data.setValueLabelBackgroundAuto(true);
        }else{
            data.setBaseValue(Float.parseFloat(df.format(minlabel)));
        }

        float [] label={minlabel,
                ((maxlabel-minlabel)/3+minlabel),
                ((maxlabel-minlabel)/3*2+minlabel),
                maxlabel};
        for(int i = 0; i < 4; i+= 1){
            AxisValue value = new AxisValue(i);
            value.setLabel(Float.parseFloat(df.format(label[i]))+"");
            Log.e("setLabel",Float.parseFloat(df.format(label[i]))+"");
            value.setValue(label[i]);
            values.add(value);
        }
        axisY.setValues(values);




//        axisY.setAutoGenerated(true);



        //X轴
        Axis axisX = new Axis();// X轴属性
        axisX.setValues(mAxisXValues);//为X轴显示的刻度值设置数据集合
        axisX.setLineColor(Color.BLACK);// 设置X轴轴线颜色
        axisY.setLineColor(Color.BLACK);// 设置Y轴轴线颜色
        axisX.setTextColor(Color.BLACK);// 设置X轴文字颜色
        axisX.setTextSize(10);// 设置X轴文字大小
        axisX.setTypeface(Typeface.DEFAULT);// 设置文字样式，此处为默认
//        axisX.setHasTiltedLabels(bolean isHasTit);// 设置X轴文字向左旋转45度
        axisX.setHasLines(false);// 是否显示X轴网格线
        //数据设置
        data.setAxisYLeft(axisY);// 将Y轴属性设置到左边
        data.setAxisXBottom(axisX);// 将X轴属性设置到底部
       // chartData.setBaseValue(20);// 设置反向覆盖区域颜色
        data.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
        data.setValueLabelBackgroundColor(Color.WHITE);// 设置数据背景颜色
        data.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
        data.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
        data.setValueLabelTextSize(12);// 设置数据文字大小
        data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        chart.setLineChartData(data);//为图表设置数据，数据类型为LineChartData
//        chart.setViewportCalculationEnabled(true);//设置是否允许在动画进行中或设置完表格数据后，自动计算viewport的大小。如果禁止，则需要可以手动设置。
        chart.setZoomEnabled(true);//设置是否支持缩放
        chart.setInteractive(true);//设置图表是否可以与用户互动
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chart.setZoomType(ZoomType.HORIZONTAL);
        Viewport viewport=chart.getCurrentViewport();
        if (viewport.height()==0){
        viewport.bottom=0;
        }
        if (maxlabel==0&&minlabel==0){
            data.setBaseValue(-1);
            viewport.bottom=-1;
        }
        chart.setMaximumViewport(viewport);
        chart.setCurrentViewport(viewport);



        Log.e("curry",chart.getCurrentViewport()+"");
        Log.e("sadasdas",chart.getMaximumViewport()+"");
    }

    // 生成一个数据，
    LineChartData  getData(JSONArray gdValueData) throws JSONException {
        // x轴的数据
//        ArrayList<String> xVals = new ArrayList<String>();
        // y轴的数据
//        ArrayList<Entry> yVals = new ArrayList<Entry>();

        //清除数据
        mAxisXValues.clear();
        mPointValues.clear();
            for (int i=0;i<gdValueData.length();i++){
                JSONObject object=gdValueData.getJSONObject(i);
                mAxisXValues.add(new AxisValue(i).setLabel(object.getString("date")));

//                yVals.add(new Entry((float) object.getDouble("number"),i));
                mPointValues.add(new PointValue(i, (float) object.getDouble("number")));
//                Log.e("y细则",yVals.get(i).getVal()+"");
            }



        // create a dataset and give it a type
        // y轴的数据集合
//        LineDataSet set1 = new LineDataSet(yVals,"");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);


//        set1.setLineWidth(1.75f); // 线宽
//        set1.setCircleSize(6f);// 显示的圆形大小
//        set1.setColor(Color.RED);// 显示颜色
//        set1.setCircleColor(Color.RED);// 圆形的颜色
//       set1.setHighLightColor(Color.TRANSPARENT); // 高亮的线的颜色
//        set1.setDrawCubic(true);// 改变折线样式，用曲线。
//        set1.setCubicIntensity(1.0f);//设置折线的平滑度
//        set1.setDrawValues(false);


//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//        dataSets.add(set1); // add the datasets

        // create a data object with the datasets


        Line line = new Line(mPointValues).setColor(Color.RED);  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        LineChartValueFormatter chartValueFormatter = new SimpleLineChartValueFormatter(2);//2代表是2位小数点
        line.setFormatter(chartValueFormatter);
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(false);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line.setStrokeWidth(2);
        lines.add(line);



        LineChartData data = new LineChartData(lines);



        return data;
    }

    //监听返回键
    @Override
    public void onBackPressed() {
        //super.onBackPressed()会自动调用finish()方法,关闭
//        super.onBackPressed();

        ElseUtils.ontwiceBackPressed(this,2000);
    }
}
