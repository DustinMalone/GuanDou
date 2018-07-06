package com.gd.guandou.guandou.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.bean.QRCodeResult;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.HttpUtils;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_scan_food_sinfo)
public class ScanFoodSInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Context context;

    @ViewInject(R.id.tv_scanfoods_goodsname)
    private TextView  tv_scanfoods_goodsname;

    @ViewInject(R.id.tv_scanfoods_goodsprice)
    private TextView  tv_scanfoods_goodsprice;

    @ViewInject(R.id.tv_scanfoods_orderdate)
    private TextView  tv_scanfoods_orderdate;

    @ViewInject(R.id.tv_scanfoods_ordernum)
    private TextView  tv_scanfoods_ordernum;

    @ViewInject(R.id.et_scanfoods_paymm)
    private EditText et_scanfoods_paymm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        context=this;

        init();
    }

    private void init() {
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            try {
                JSONObject object=new JSONObject(bundle.getString(Content.QRCodeRusult));
                Gson gson=new Gson();
                QRCodeResult qrCodeResult=gson.fromJson(object.toString(),QRCodeResult.class);
                Log.e("scan",object.toString()+"");
                Log.e("QRCodeResult",qrCodeResult.getGoodname()+"");
                tv_scanfoods_goodsname.setText(qrCodeResult.getGoodname());
                tv_scanfoods_goodsprice.setText(qrCodeResult.getGoodprice()+"贝母");
                tv_scanfoods_orderdate.setText(qrCodeResult.getDate());
                tv_scanfoods_ordernum.setText(qrCodeResult.getOrdernum());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 扫描支付
     * @param
     */
    private void ScanPayment(String password){
        JSONObject data =new JSONObject();
        try {
            data.put("user_id", SharedPreferencesUtil.getString(context,"user_id",""));
            data.put("password", password);
            data.put("ordernum", tv_scanfoods_ordernum.getText().toString());
            data.put("jfnum", tv_scanfoods_goodsprice.getText().toString().substring(0,tv_scanfoods_goodsprice.getText().length()-2));
//            Log.e("jfnum",data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_QRCodePay, data.toString(), new Callback.CacheCallback<String>() {

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
            //取消
            case R.id.btn_scanfoods_cancel:
                finish();
                break;

            //支付
            case R.id.btn_scanfoods_pay:
                if (!TextUtils.isEmpty(et_scanfoods_paymm.getText().toString())) {
                    ScanPayment(et_scanfoods_paymm.getText().toString());
                }
                else{
                    T.show(context,"请输入支付密码",1000);
                }
                break;
        }

    }
}
