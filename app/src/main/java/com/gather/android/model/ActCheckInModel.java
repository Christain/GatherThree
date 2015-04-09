package com.gather.android.model;

import java.io.Serializable;

/**
 * Created by Christain on 2015/4/7.
 */
public class ActCheckInModel implements Serializable {

    private int id;//签到id
    private String subject;//名称
    private String rgb_hex;//8位十六进制颜色rgb值
    private int need_sure;//	需要确认：0否，1是
    private int order_limit;//顺序限制：0否，1是
    private int has_prize;//是否有随机奖品：0否，1是
    private int status;//状态：0正常，1已确认

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        if (subject != null) {
            return subject;
        }
        return "";
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRgb_hex() {
        if (rgb_hex != null) {
            return rgb_hex;
        }
        return "FFFF9933";
    }

    public void setRgb_hex(String rgb_hex) {
        this.rgb_hex = rgb_hex;
    }

    public int getNeed_sure() {
        return need_sure;
    }

    public void setNeed_sure(int need_sure) {
        this.need_sure = need_sure;
    }

    public int getOrder_limit() {
        return order_limit;
    }

    public void setOrder_limit(int order_limit) {
        this.order_limit = order_limit;
    }

    public int getHas_prize() {
        return has_prize;
    }

    public void setHas_prize(int has_prize) {
        this.has_prize = has_prize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
