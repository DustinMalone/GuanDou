package com.gd.guandou.guandou.bean;

/**
 * Created by Administrator on 2017/10/17.
 */

public class PurchashHistory {

    private String title;
    private String nDate;//日期格式 yyyy-MM-dd
    private String date;//日期格式 MM-dd(显示日期)
    private String time;
    private String img;
    private String goodsName;
    private String goodsPrices;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsPrices() {
        return goodsPrices;
    }

    public void setGoodsPrices(String goodsPrices) {
        this.goodsPrices = goodsPrices;
    }


    public String getnDate() {
        return nDate;
    }

    public void setnDate(String nDate) {
        this.nDate = nDate;
    }
}
