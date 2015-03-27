package com.gather.android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ActModel implements Serializable {

    private int id;// 活动id
    private String title;// 标题
    private String intro;// 标题简介
    private int cost;// 价格
    private double lon;// 经度
    private double lat;// 纬度
    private String addr_city;// 地址（城市）
    private String addr_area;// 地址（区）
    private String addr_road;// 地址（路）
    private String addr_num;// 地址（号）
    private String addr_name;//
    private String addr_route;// 地址（路线）
    private String contact_way;// 联系方式
    private String b_time;// 开始时间（例：1997-07-01 09:00:00）
    private String e_time;// 结束时间（例：1997-07-01 09:00:00）
    //    private String organizer;// 组织者
    private int t_status;// 时间状态：1即将开始，2进行中，3筹备中，4已结束
    private String detail;// 详情
    private String share_url;// 分享url
    //    private int can_enroll;// 是否可以报名：0否，1是
    private String head_img_url;// 活动首图url
    private int is_loved;// 是否已添加感兴趣：-1不可再添加，0未添加，1已添加
    private int loved_num;// 感兴趣的用户数
    private int shared_num;// 分享数
    private ArrayList<TrendsPicModel> act_imgs;
    private String lov_time;// 收藏时间
//    private int enroll_status;//报名状态：-1未开放报名，0未报名，1已报名，2已通过，3已拒绝
//    private int admin;//管理员：-1未设置管理员，0不是管理员，1是管理员

//    public int getEnroll_status() {
//        return enroll_status;
//    }
//
//    public void setEnroll_status(int enroll_status) {
//        this.enroll_status = enroll_status;
//    }
//
//    public int getAdmin() {
//        return admin;
//    }
//
//    public void setAdmin(int admin) {
//        this.admin = admin;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        if (title != null) {
            return title;
        } else {
            return "";
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        if (intro != null) {
            return intro;
        } else {
            return "";
        }
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddr_city() {
        if (addr_city != null) {
            return addr_city;
        } else {
            return "";
        }
    }

    public void setAddr_city(String addr_city) {
        this.addr_city = addr_city;
    }

    public String getAddr_area() {
        if (addr_area != null) {
            return addr_area;
        } else {
            return "";
        }
    }

    public void setAddr_area(String addr_area) {
        this.addr_area = addr_area;
    }

    public String getAddr_road() {
        if (addr_road != null) {
            return addr_road;
        } else {
            return "";
        }
    }

    public void setAddr_road(String addr_road) {
        this.addr_road = addr_road;
    }

    public String getAddr_num() {
        if (addr_num != null) {
            return addr_num;
        } else {
            return "";
        }
    }

    public void setAddr_num(String addr_num) {
        this.addr_num = addr_num;
    }

    public String getAddr_name() {
        if (addr_name != null) {
            return addr_name;
        } else {
            return "";
        }
    }

    public void setAddr_name(String addr_name) {
        this.addr_name = addr_name;
    }

    public String getAddr_route() {
        if (addr_route != null) {
            return addr_route;
        } else {
            return "";
        }
    }

    public void setAddr_route(String addr_route) {
        this.addr_route = addr_route;
    }

    public String getContact_way() {
        if (contact_way != null) {
            return contact_way;
        } else {
            return "";
        }
    }

    public void setContact_way(String contact_way) {
        this.contact_way = contact_way;
    }

    public String getB_time() {
        if (b_time != null) {
            return b_time;
        } else {
            return "";
        }
    }

    public void setB_time(String b_time) {
        this.b_time = b_time;
    }

    public String getE_time() {
        if (e_time != null) {
            return e_time;
        } else {
            return "";
        }
    }

    public void setE_time(String e_time) {
        this.e_time = e_time;
    }

//    public String getOrganizer() {
//        if (organizer != null) {
//            return organizer;
//        } else {
//            return "";
//        }
//    }
//
//    public void setOrganizer(String organizer) {
//        this.organizer = organizer;
//    }
//
    public int getT_status() {
        return t_status;
    }

    public void setT_status(int t_status) {
        this.t_status = t_status;
    }

    public String getDetail() {
        if (detail != null) {
            return detail;
        } else {
            return "";
        }
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getShare_url() {
        if (share_url != null) {
            return share_url;
        } else {
            return "";
        }
    }

    public void setShare_url(String detail_url) {
        this.share_url = detail_url;
    }

//    public int getCan_enroll() {
//        return can_enroll;
//    }
//
//    public void setCan_enroll(int can_enroll) {
//        this.can_enroll = can_enroll;
//    }

    public String getHead_img_url() {
        if (head_img_url != null) {
            return head_img_url;
        } else {
            return "";
        }
    }

    public void setHead_img_url(String head_img_url) {
        this.head_img_url = head_img_url;
    }

    public int getIs_loved() {
        return is_loved;
    }

    public void setIs_loved(int is_loved) {
        this.is_loved = is_loved;
    }

    public int getLoved_num() {
        return loved_num;
    }

    public void setLoved_num(int loved_num) {
        this.loved_num = loved_num;
    }

    public int getShared_num() {
        return shared_num;
    }

    public void setShared_num(int shared_num) {
        this.shared_num = shared_num;
    }

    public ArrayList<TrendsPicModel> getAct_imgs() {
        return act_imgs;
    }

    public void setAct_imgs(ArrayList<TrendsPicModel> act_imgs) {
        this.act_imgs = act_imgs;
    }

    public String getLov_time() {
        if (lov_time != null) {
            return lov_time;
        } else {
            return "";
        }
    }

    public void setLov_time(String lov_time) {
        this.lov_time = lov_time;
    }

}
