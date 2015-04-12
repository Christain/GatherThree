package com.gather.android.model;

/**
 * Created by Christain on 15/4/11.
 */
public class WXPayModel {


  //  appid mch_id nonce_str prepay_id result_code return_code return_msg sign trade_type
    private String appid;
    private String mch_id;
    private String nonce_str;
    private String prepay_id;
    private int result_code;
    private int return_code;
    private String return_msg;
    private String sign;
    private int trade_type;

    public String getAppid() {
        return (appid != null) ? appid : "";
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return (mch_id != null) ? mch_id : "";
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return (nonce_str != null) ? nonce_str : "";
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public int getReturn_code() {
        return return_code;
    }

    public void setReturn_code(int return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return (return_msg != null) ? return_msg : "";
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getSign() {
        return (sign != null) ? sign : "";
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(int trade_type) {
        this.trade_type = trade_type;
    }
}
