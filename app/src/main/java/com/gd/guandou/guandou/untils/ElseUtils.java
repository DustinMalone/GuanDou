package com.gd.guandou.guandou.untils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;

import com.gd.guandou.guandou.Application.AppManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/10/12.
 */

public class ElseUtils {


    private  static long lastClickTime=0;

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     *  捕捉密码输入框,禁止输入汉字和空格
     */
    public static InputFilter noChineseAndEmptyFilter(){
    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (ElseUtils.isChinese(source.charAt(i))||source.toString().equals(" ")) {
                    return "";
                }
            }
            return null;
        }
    };
    
    return filter;
    }


    public static String getInputStreamString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * bytes转换成十六进制字符串
     * @param //byte[] b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2hex(byte[] b) // 二进制转字符串
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }

        }
        return sb.toString();
    }

    /**
     * bytes字符串转换为Byte值
     * @param //String src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hex2byte(String str) { // 字符串转二进制
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer
                        .decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否有权限
     * @param context
     * @return
     */
    public static boolean hasPermission(Context context,String content) {
        int perm = context.checkCallingOrSelfPermission(content);
        return perm == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 点击2次返回键退出程序
     */
    public static void ontwiceBackPressed(Context context,long time) {
        if (lastClickTime <= 0) {
            T.bshow(context,"再按一次退出程序",1000);
            // 把当前点击back键的时间赋值给lastClickTime
            lastClickTime = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < time) {
                AppManager.getAppManager().exitApp(context);
            } else {
                T.bshow(context,"再按一次退出程序",1000);
                lastClickTime = System.currentTimeMillis();
            }
        }
    }

    /**
     *   将字符串(BASE64格式)转换成Bitmap类型
     */
    public static Bitmap stringtoBitmap(String string){

        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray=Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    /**
     *  将Bitmap转换成字符串(BASE64格式)
     */
    public static String bitmaptoString(Bitmap bitmap){

        String string=null;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string= Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }



    /**
     * 验证手机账号密码
     */
    public static Boolean isMMYes(String mobiles)  {
        String telRegex = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![!#$%^&*]+$)[\\da-zA-Z]{6,20}$";
         return  Pattern.matches(telRegex,mobiles);
    }

}
