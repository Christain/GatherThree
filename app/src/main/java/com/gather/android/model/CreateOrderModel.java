package com.gather.android.model;

/**
 * Created by Christain on 15/4/11.
 */
public class CreateOrderModel {

    private int id;//订单ID
    private int trade_no;//订单号
    private WXPayModel wx;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(int trade_no) {
        this.trade_no = trade_no;
    }

    public WXPayModel getWx() {
        return wx;
    }

    public void setWx(WXPayModel wx) {
        this.wx = wx;
    }
}
