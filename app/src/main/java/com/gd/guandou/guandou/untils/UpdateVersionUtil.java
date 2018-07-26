package com.gd.guandou.guandou.untils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by LCB on 2018/7/20.
 */

public class UpdateVersionUtil {

    private static ProgressDialog pDialog;
    private String nowVersion;
    private static ProgressDialog progressDialog;
    private static String downFilePath=Environment.getExternalStorageDirectory().getPath() + File.separator+"GDDownLoad";

    /**
     * 下载包
     *
     * @param downloadurl
     *            下载的url
     *
     */
    @SuppressLint("SdCardPath")
    protected static void setDownLoad(final Context context, String downloadurl, final String
            versionName) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams(downloadurl);
        params.setAutoRename(true);//断点下载
        SDCardUtils.isExistFilemir(Environment.getExternalStorageDirectory().getPath() + File.separator+"GDDownLoad");
        params.setSaveFilePath(downFilePath+ File.separator+"gd_"+versionName+".apk");
        x.http().get(params, new Callback.ProgressCallback<File>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                System.out.println("提示更新失败");
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(File arg0) {
                // TODO Auto-generated method stub
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(downFilePath, "gd_"+versionName+".apk")),
                        "application/vnd.android.package-archive");
                context.startActivity(intent);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                // TODO Auto-generated method stub
                progressDialog.setMax((int)total);
                progressDialog.setProgress((int)current);
            }

            @Override
            public void onStarted() {
                // TODO Auto-generated method stub
                System.out.println("开始下载");
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置为水平进行条
                progressDialog.setMessage("正在下载中...");
                progressDialog.setProgress(0);
                progressDialog.show();
            }

            @Override
            public void onWaiting() {
                // TODO Auto-generated method stub

            }
        });
    }

    public static void proDialogShow(Context context, String msg) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(msg);
        // pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void proDialogHide() {
        try {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param versionname
     *            地址中版本的名字
     * @param downloadurl
     *            下载包的地址
     * @param desc
     *            版本的描述
     */
    public static void setUpDialog(final Context context, final String versionname, final
    String downloadurl, String desc) {

        AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(false)
                .setTitle("下载" + versionname + "最新版本").setMessage(desc)
                .setNegativeButton("取消", null)
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        //删除其他安装包
                        deleteSvaeFile(downFilePath+File.separator,versionname);
                        setDownLoad(context,downloadurl,versionname);
                    }
                }).create();
        dialog.show();
    }


    /**
     * 递归删除文件夹
     */
    public static void deleteSvaeFile(String destfile,String LastFile){
        File file=new File(destfile);
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            for(File childFile:childFiles)
            {
                if (!childFile.getName().contains(LastFile)){
                    childFile.delete();
                }

            }
        }
    }
}
