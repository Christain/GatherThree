package com.gather.android.model;

import java.io.Serializable;

/**
 * Created by Christain on 2015/3/18.
 */
public class ActMoreInfoModel implements Serializable{

    private int id;//活动id
    private String breakfast_menu;//早餐菜单
    private String lunch_menu;//午餐菜单
    private String supper_menu;//晚餐菜单
    private String attention;//注意事项
    private String busi_url;//主办方介绍url
    private String place_img_url;//场地平面图url
    private String prize_descri;//奖品描述
    private int album_id;//自己的相册id：-1无
    private int group_id;//自己的分组id：-1无

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBreakfast_menu() {
        if (breakfast_menu != null) {
            return breakfast_menu;
        }
        return "";
    }

    public void setBreakfast_menu(String breakfast_menu) {
        this.breakfast_menu = breakfast_menu;
    }

    public String getLunch_menu() {
        if (lunch_menu != null) {
            return lunch_menu;
        }
        return "";
    }

    public void setLunch_menu(String lunch_menu) {
        this.lunch_menu = lunch_menu;
    }

    public String getSupper_menu() {
        if (supper_menu != null) {
            return supper_menu;
        }
        return "";
    }

    public void setSupper_menu(String supper_menu) {
        this.supper_menu = supper_menu;
    }

    public String getAttention() {
        if (attention != null) {
            return attention;
        }
        return "";
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getBusi_url() {
        if (busi_url != null) {
            return busi_url;
        }
        return "";
    }

    public void setBusi_url(String busi_url) {
        this.busi_url = busi_url;
    }

    public String getPlace_img_url() {
        if (place_img_url != null) {
            return place_img_url;
        }
        return "";
    }

    public void setPlace_img_url(String place_img_url) {
        this.place_img_url = place_img_url;
    }

    public String getPrize_descri() {
        if (prize_descri != null) {
            return prize_descri;
        }
        return "";
    }

    public void setPrize_descri(String prize_descri) {
        this.prize_descri = prize_descri;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
}
