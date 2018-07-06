package com.gd.guandou.guandou.bean;

/**
 * Created by Administrator on 2017/11/14.
 * 二维码扫描结果类
 */

public class QRCodeResult {

    private String goodname;
    private String goodprice;
    private String ordernum;//订单号
    private String date;//订单日期

    public String getGoodname() {
        return goodname;
    }

    public void setGoodname(String goodname) {
        this.goodname = goodname;
    }

    public String getGoodprice() {
        return goodprice;
    }

    public void setGoodprice(String goodprice) {
        this.goodprice = goodprice;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
