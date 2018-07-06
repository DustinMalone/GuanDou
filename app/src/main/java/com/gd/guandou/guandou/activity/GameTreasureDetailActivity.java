package com.gd.guandou.guandou.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.adapter.GameTreasureDetailAdapter;
import com.gd.guandou.guandou.bean.GameTreasure;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.HttpUtils;
import com.gd.guandou.guandou.untils.IntentUtil;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;
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
import java.util.Collection;
import java.util.List;

@ContentView(R.layout.activity_game_treasure_detail)
public class GameTreasureDetailActivity extends AppCompatActivity implements View.OnClickListener{

    @ViewInject(R.id.xlv_game_treasure_detail_xlist)
    private XListView xlv_game_treasure_detail_xlist;

    private GameTreasureDetailAdapter gameTreasureDetailAdapter;

    private List<GameTreasure> datalist;

    private Context context;

    //每页的大小
    private int pageSize=20;

    //上一页的序号
    private int preIndex=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        context=this;
//        setContentView(R.layout.activity_game_treasure_detail);

        init();
    }

    private void init() {
        datalist=new ArrayList<>();
        getGDGainCBrecoreds(1);

        gameTreasureDetailAdapter=new GameTreasureDetailAdapter(context,datalist);
        xlv_game_treasure_detail_xlist.setAdapter(gameTreasureDetailAdapter);

        xlv_game_treasure_detail_xlist.setPullRefreshEnable(false);
        xlv_game_treasure_detail_xlist.setPullLoadEnable(true);
        xlv_game_treasure_detail_xlist.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getGDGainCBrecoreds((int)Math.ceil(datalist.size()/pageSize)+1);
                        xlv_game_treasure_detail_xlist.stopLoadMore();
                    }
                }, 2000);



            }
        });
    }

    /**
     * 获取分红记录
     * @param
     */
    public void getGDGainCBrecoreds(final int index){
        Log.e("index",""+index);
        final JSONObject data =new JSONObject();
        try {
            data.put("pageIndex",index);
            data.put("pageSize",pageSize);
            data.put("userId", SharedPreferencesUtil.getString(context,"user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.xPostJson(context, Content.GDAPI+Content.gd_user_FindCBRecords, data.toString(), new Callback.CacheCallback<String>() {

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

                        if (datalist.size()%pageSize>0){
                            T.show(context,"已加载最后到一条记录",1000);
                            return;
                        }
                        if (jobject.getJSONArray("data").length() != 0) {
                                JSONArray jsonoArray = jobject.getJSONArray("data");
                                Gson gson = new Gson();
                                datalist.addAll((Collection<? extends GameTreasure>) gson.fromJson(jsonoArray.toString(), new TypeToken<List<GameTreasure>>() {}.getType()));
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
                gameTreasureDetailAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回键
            case R.id.iv_game_treasure_detail_back:
                IntentUtil.goIntent(GameTreasureDetailActivity.this,GameTreasureActivity.class,null,true);
                break;
        }

    }
}
