package com.gd.guandou.guandou.content;

import com.gd.guandou.guandou.untils.SDCardUtils;

/**
 * Created by Administrator on 2017/9/30.
 */

public class Content {
    //常用变量
    public static String accessToken="accessToken";

    public static String QRCodeRusult="QRCodeRusult";//二维码扫描结果标识

    //贯豆测试接口域名
//    public static String GDAPI="http://gzpandie.mynetgear.com:81/";

    //贯豆正式接口域名
//    public static String GDAPI="http://139.224.229.110:8088/";

    //贯豆正式接口域名
    public static String GDAPI="http://120.79.55.141/";


    //相机压缩图片的文件夹名
    public static String photoFileName="GUANDOU";

    public static String filename= SDCardUtils.getSDCardPath()+ Content.photoFileName;


    /**
     * 贯豆接口地址
     */
    //验证TOKEN
//    public static String checkToken="http://120.77.174.176/web_test/public/index.ptophp/api?";
//    public static String checkToken="http://test.88106.org/index.php/toapi?";
    public static String checkToken="GuanDou/app/applogincheck";

    //商城测试注册页面
//    public static String regPage="http://120.77.174.176/web_test/public/index.php/wap/passport-goregister.html";
    public static String regPage="http://www.dao333.com/wap/passport-goregister.html";

    //商城测试忘记密码页面
//    public static String foggetMMPage="http://120.77.174.176/web_test/public/index.php/wap/passport-gofindpwd.html";
    public static String foggetMMPage="http://www.dao333.com/wap/passport-gofindpwd.html";

    //贯豆登录
    public static String gd_login="GuanDou/app/applogin";

    //贯豆获取用户信息
    public static String gd_user_info="GuanDou/app/getUserInfo.do";

    //贯豆获取用户贝母
    public static String gd_user_BM="GuanDou/app/getPointsInfo.do";

    //贯豆获取用户贯豆信息
    public static String gd_user_GD="GuanDou/user/getbeansInfo.do";

    //贯豆获取用户分红信息
    public static String gd_user_FHRecords="GuanDou/userOpeService/queryFHRecords.do";

    //贯豆查询用户藏豆数量
    public static String gd_user_HideGDCounts="GuanDou/user/selectById.do";

    //贯豆查询用户累计收益和昨日收益
    public static String gd_user_FindInComeInfo="GuanDou/xbService/queryXBInfo.do";

    //贯豆转入转出贯豆
    public static String gd_user_ChanceOrTurn="GuanDou/user/changeOrTurn.do";

    //贯豆用户验证支付密码
    public static String gd_user_VerifyPwd="GuanDou/user/VerifyPwd.do";

    //贯豆藏宝游戏明细记录
    public static String gd_user_FindCBRecords="GuanDou/userOpeService/queryCBRecords.do";


    //贯豆获取贯豆指数
    public static String gd_user_GetGDValueIndex="GuanDou/bean/queryAll.do";

    //贯豆兑换贝母
    public static String gd_user_GDExchangeBM="GuanDou/bean/Exchange.do";

    //贯豆获取贯豆指数线图
    public static String gd_user_GetGBValueIndexLine="GuanDou/bean/selectByTime.do";

    //贯豆获取消费记录
    public static String gd_user_FindConsumeRecords="GuanDou/userOpeService/queryAll.do";

    //贯豆获取用户头像
    public static String gd_user_GetHeadPhoto="GuanDou/user/selectPhoto.do";

    //贯豆查询用户总资产
    public static String gd_user_FindTotalAssetsInfo="GuanDou/bean/selectByTotal.do";

    //贯豆验证用户是否设置支付密码
    public static String gd_user_IsSetPayMM="GuanDou/user/check.do";

    //贯豆用户设置支付密码
    public static String gd_user_SetPayMM="GuanDou/user/save.do";

    //贯豆用户修改支付密码
    public static String gd_user_UpdatePayMM="GuanDou/user/updateUser.do";

    //贯豆用户上传头像
    public static String gd_user_UploadHeadPhoto="GuanDou/user/upload.do";

    //扫描支付
    public static String gd_user_QRCodePay="GuanDou/user/queryByCode.do";

    //获取验证码
    public static String gd_reg_vcode="GuanDou/api/user/vcode.do";

    //发送信息
    public static String gd_reg_sendSms="GuanDou/api/user/sendInfo.do";

    //效验验证码
    public static String verifyAccount="GuanDou/api/user/verifyAccount.do";

    //效验短信验证码
    public static String verifySms="GuanDou/api/user/verifySms.do";

    //账号注册
    public static String signup="GuanDou/api/user/testAccount.do";

    //注册支付
    public static String pay="GuanDou/payment/payment/pay.do";

    //重置密码
    public static String resetpassword="GuanDou/api/user/forgot/gdresetpassword.do";



}
