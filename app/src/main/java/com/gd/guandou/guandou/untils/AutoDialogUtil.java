package com.gd.guandou.guandou.untils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gd.guandou.guandou.R;

/**
 * Created by Administrator on 2017/10/11.
 */

public class AutoDialogUtil {
    private static AlertDialog dialog;
    public static EditText et_dialog_guandou_num;
    private static final int DECIMAL_DIGITS = 2;//小数的位数
    /**
     *
     * @param context
     *            上下文
     * @param text
     *            自定义显示的文字
     * @param
     *
     */
    public static void showScanNumberDialog(final Context context, String text, final View.OnClickListener listener) {
        // SysApplication.getInstance().exit();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 创建对话框
        dialog = builder.create();
        // 没有下面这句代码会导致自定义对话框还存在原有的背景

        // 弹出对话框
        dialog.show();
        //取消外部点击
        dialog.setCanceledOnTouchOutside(false);

        // 以下两行代码是对话框的EditText点击后不能显示输入法的
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        Window window = dialog.getWindow();
        window.setContentView(R.layout.auto_dialog);
        et_dialog_guandou_num = (EditText) window
                .findViewById(R.id.et_dialog_guandou_num);

        WindowManager.LayoutParams params =window.getAttributes();
        params.gravity= Gravity.CENTER;
        params.width= LinearLayout.LayoutParams.WRAP_CONTENT;
        params.height= LinearLayout.LayoutParams.WRAP_CONTENT;


        //获取焦点
        et_dialog_guandou_num.setFocusable(true);
        et_dialog_guandou_num.setFocusableInTouchMode(true);
        et_dialog_guandou_num.requestFocus();

        TextView tv_dialog_title = (TextView) window
                .findViewById(R.id.tv_dialog_title);

        // 实例化确定按钮
        final Button btn_hint_yes = (Button) window.findViewById(R.id.btn_hint_yes);
        // 实例化取消按钮
        Button btn_hint_no = (Button) window.findViewById(R.id.btn_hint_no);

        et_dialog_guandou_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS+1);
                        et_dialog_guandou_num.setText(s);
                        et_dialog_guandou_num.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    et_dialog_guandou_num.setText(s);
                    et_dialog_guandou_num.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et_dialog_guandou_num.setText(s.subSequence(0, 1));
                        et_dialog_guandou_num.setSelection(1);
                        return;
                    }
                }

                if (s.toString().isEmpty()){
                    btn_hint_yes.setEnabled(false);
                    btn_hint_yes.setTextColor(context.getResources().getColor(R.color.colorGrey));
                }else {
                    btn_hint_yes.setEnabled(true);
                    btn_hint_yes.setTextColor(context.getResources().getColor(R.color.colorblue));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });




        //设置标题
        if (!text.isEmpty())
        {
        tv_dialog_title.setText(text);
        }




        btn_hint_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Toast.makeText(context, "确定", Toast.LENGTH_SHORT).show();
                listener.onClick(v);

            }
        });
        btn_hint_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
//                v.setTag(et_dialog_guandou_num);
//                listener.onClick(v);
                dismissScanNumberDialog();
            }
        });
    }

    public static void setEditTextHintValue(String hint) {

        et_dialog_guandou_num.setHint(hint);
    }

    public static String getEditTextValue() {
       return  et_dialog_guandou_num.getText().toString().trim();
    }

    public static void addTextChangedListener(TextWatcher textWatcher) {
        et_dialog_guandou_num.addTextChangedListener(textWatcher);
    }

    public static void dismissScanNumberDialog() {
        if (dialog.isShowing())
        dialog.dismiss();
    }

}

