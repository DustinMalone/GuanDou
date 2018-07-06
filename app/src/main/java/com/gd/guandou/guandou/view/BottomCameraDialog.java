package com.gd.guandou.guandou.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gd.guandou.guandou.R;

/**
 * Created by Administrator on 2017/11/1.
 */

public class BottomCameraDialog {

    private Dialog dialog;

//初始化
    public void initDialog(Context context, View.OnClickListener clickListener){
            dialog = new Dialog(context, R.style.ActionCameraDialogStyle);
            //填充对话框的布局
            View inflate = LayoutInflater.from(context).inflate(R.layout.cramera_dialog, null);
            //初始化控件
            TextView choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
            TextView takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
            choosePhoto.setOnClickListener(clickListener);
            takePhoto.setOnClickListener(clickListener);
            //将布局设置给Dialog
            dialog.setContentView(inflate);
            //获取当前Activity所在的窗体
            Window dialogWindow = dialog.getWindow();
            //设置Dialog从窗体底部弹出
            dialogWindow.setGravity(Gravity.BOTTOM);
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
            dialogWindow.setAttributes(lp);
            dialog.show();//显示对话框

    }

    public void dismiss(){
        if (dialog!=null){
        dialog.dismiss();
        }
        dialog=null;
    }
}
