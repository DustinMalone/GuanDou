package com.gd.guandou.guandou.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gd.guandou.guandou.R;
import com.gd.guandou.guandou.untils.ScreenUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/11/1.
 */

public class CenterPassWordDialog {

    private Dialog dialog;
    public  interface ConvertViewListener
    {
        public void ViewListener(String pass);
    }

    private  ConvertViewListener convertViewListener;

    public void setConvertViewOnClickListener(ConvertViewListener convertViewListener) {
        this.convertViewListener = convertViewListener;
    }
//初始化
    public void initDialog(Context context, View.OnClickListener clickListener){
            dialog = new Dialog(context, R.style.ActionCameraDialogStyle);
            //填充对话框的布局
            View inflate = LayoutInflater.from(context).inflate(R.layout.password_dialog, null);
            //初始化控件
            ImageView cancel = (ImageView) inflate.findViewById(R.id.img_cancel);
            final PasswordView password = (PasswordView) inflate.findViewById(R.id.pv_password_dialog);
            cancel.setOnClickListener(clickListener);

            password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length()== password.PASSWORD_LENGTH) {
                    convertViewListener.ViewListener(s.toString());
                    password.reset();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


            //将布局设置给Dialog
            dialog.setContentView(inflate);
            //获取当前Activity所在的窗体
            Window dialogWindow = dialog.getWindow();
            //设置Dialog从窗体底部弹出
            dialogWindow.setGravity(Gravity.BOTTOM);
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//            lp.y = 20;//设置Dialog距离底部的距离

        /**
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (ScreenUtils.getScreenHeight(context) * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (ScreenUtils.getScreenWidth(context) * 1); // 宽度设置为屏幕的0.65
//       将属性设置给窗体
            dialogWindow.setAttributes(p);
            dialog.show();//显示对话框

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                password.showKeyboard();
            }
        }, 500);

    }

    public void dismiss(){
        if (dialog!=null){
        dialog.dismiss();
        }
        dialog=null;
    }
}
