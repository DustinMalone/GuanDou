package com.gd.guandou.guandou.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gd.guandou.guandou.Application.AppManager;
import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.content.Content;
import com.gd.guandou.guandou.untils.BitmapUtils;
import com.gd.guandou.guandou.untils.ElseUtils;
import com.gd.guandou.guandou.untils.IntentUtil;
import com.gd.guandou.guandou.untils.SDCardUtils;
import com.gd.guandou.guandou.untils.SharedPreferencesUtil;
import com.gd.guandou.guandou.untils.T;
import com.gd.guandou.guandou.view.CircleImageView;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

@ContentView(R.layout.activity_head_picture)
public class HeadPictureActivity extends AppCompatActivity implements View.OnClickListener{

    private Context context;

    private  final int takePhoto=1;//拍照
    private  final int choosePhoto=2;//选择相册图片
    private  final int cropPhoto=3;//截取图片

    private  final String WRITE_EXTERNAL_STORAGE="android.permission.WRITE_EXTERNAL_STORAGE";//写权限
    private  final String READ_EXTERNAL_STORAGE="android.permission.READ_EXTERNAL_STORAGE";//截取图片
    private  final String CAMERA="android.permission.CAMERA";//截取图片


    @ViewInject(R.id.iv_head_picture_pic)
    private CircleImageView iv_head_picture_pic;

    //文件路径
    private Uri filePath;

    private String filenames="";



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
            byte[] headimg=bundle.getByteArray("img");
            if (headimg!=null){
            iv_head_picture_pic.setImageBitmap(BitmapFactory.decodeByteArray(headimg,0,headimg.length));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //拍照
            case R.id.tv_head_picture_takePhoto:
                if (!ElseUtils.hasPermission(context,CAMERA)||!ElseUtils.hasPermission(context,WRITE_EXTERNAL_STORAGE)||!ElseUtils.hasPermission(context,READ_EXTERNAL_STORAGE))
                {
                    Log.e("WRITE",ElseUtils.hasPermission(context,CAMERA)+"");
                    Log.e("WRITE",ElseUtils.hasPermission(context,WRITE_EXTERNAL_STORAGE)+"");
                    Log.e("READ",ElseUtils.hasPermission(context,READ_EXTERNAL_STORAGE)+"");
                    T.show(context,"请设置相机权限",1000);
                    return;
                }
                filePath=IntentUtil.takePhoto((Activity)context,null,takePhoto);
//                Log.e("filePath",filePath.getPath().toString());
                break;
            //从相册中选取图片
            case R.id.tv_head_picture_choosePhoto:

                if (!ElseUtils.hasPermission(context,WRITE_EXTERNAL_STORAGE)||!ElseUtils.hasPermission(context,READ_EXTERNAL_STORAGE))
                {
                    Log.e("WRITE",ElseUtils.hasPermission(context,WRITE_EXTERNAL_STORAGE)+"");
                    Log.e("READ",ElseUtils.hasPermission(context,READ_EXTERNAL_STORAGE)+"");
                    T.show(context,"请设置存储权限",1000);
                    return;
                }
                IntentUtil.choosePhoto((Activity) context,choosePhoto);
                break;
        }
    }


    public void backFinish(View v) {
        finish();
    }

    /**
     * 上传图片
     */
    public void uploadImage(String fileP){
        final ProgressDialog dia = new ProgressDialog(context);
        dia.setMessage("加载中....");
        dia.setCanceledOnTouchOutside(true);
        dia.show();
        String upUrl = fileP;//指定要上传的文件
        RequestParams params = new RequestParams(Content.GDAPI+Content.gd_user_UploadHeadPhoto);
        params.setAsJsonContent(true);
        params.addHeader("charset","utf-8");
        params.addHeader("Content-Type","application/json");
//        File file = new File(upUrl);
//        FileInputStream inputFile = null;
//        try {
//            inputFile = new FileInputStream(file);
//            byte[] buffer = new byte[(int)file.length()];
//            inputFile.read(buffer);
//            inputFile.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        params.addBodyParameter("image", ElseUtils.byte2hex(BitmapUtils.getBitmapByte(fileP)));
        params.addBodyParameter("accessToken", SharedPreferencesUtil.getString(context,"accessToken",""));
            Log.e("image", ElseUtils.byte2hex(BitmapUtils.getBitmapByte(fileP)));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //加载成功回调，返回获取到的数据
                Log.e("onSuccess", "onSuccess: " + result);
                iv_head_picture_pic.setImageBitmap(BitmapFactory.decodeFile(filenames));
            }

            @Override
            public void onFinished() {
                filenames="";
                dia.dismiss();//加载完成
            }

            @Override
            public void onCancelled(CancelledException cex) {

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
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==takePhoto){
            if (resultCode==RESULT_OK){
//                iv_head_picture_pic.setImageURI(filePath);
                SDCardUtils.isExistFilemir(Content.filename);
                filenames= Content.filename+ File.separator+String.valueOf(System.currentTimeMillis())+".jpg";
                SDCardUtils.isExistFile(filenames);

                IntentUtil.cropImage((Activity)context,filePath,500,500,cropPhoto,filenames);

            }
        }
        else if (requestCode==choosePhoto){
            if (resultCode==RESULT_OK){

                SDCardUtils.isExistFilemir(Content.filename);
                  filenames= Content.filename+ File.separator+String.valueOf(System.currentTimeMillis())+".jpg";
                SDCardUtils.isExistFile(filenames);
                IntentUtil.cropImage((Activity)context,data.getData(),500,500,cropPhoto,filenames);
//                IntentUtil.doPhoto((Activity) context,data,requestCode,choosePhoto
//                        ,null,iv_head_picture_pic,null);
            }

        }
        else if (requestCode==cropPhoto) {
            if (resultCode==RESULT_OK){
           if(null==BitmapUtils.saveBitmap
                   (BitmapFactory.decodeFile(filenames),Content.filename,filenames)){
               T.show(context,"图片过大.",1000);
               return;
           }
           uploadImage(filenames);

            }
        }
    }



}
