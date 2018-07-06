package com.gd.guandou.guandou.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2017/10/18.
 */

public class ProgressDialogView {
    private  static ProgressDialog progressDialog;


    /**
     * 加载框
     */
    public static void buildProgressDialog(Context context,String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setOwnerActivity((Activity)context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        //防止Activity被杀死后又重新创建
        if (null!=progressDialog.getOwnerActivity()&&progressDialog.getOwnerActivity().isFinishing()){
          //  Log.e("progressDialog","ddddddddddddddddddddddddddddddddddddddddddddddd");
            progressDialog = new ProgressDialog(context);
            progressDialog.setOwnerActivity((Activity)context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /**
     * @Description: TODO 取消加载框
     */
    public static void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }
}
