package com.gather.android.model;

import java.io.Serializable;

/**
 * Created by Christain on 2015/3/17.
 */
public class ActModulesStatusModel implements Serializable{

    private int id;
    private int show_process;//流程状态：-1无，0未设置，1已设置
    private int show_menu;//菜单：-1无，0未设置，1已设置
    private int show_attention;//注意事项：-1无，0未设置，1已设置
    private int show_busi;//主办方介绍：-1无，0未设置，1已设置
    private int show_navi;//导航：-1无，0未设置，1已设置
    private int show_place_img;//场地平面图：-1无，0未设置，1已设置
    private int show_route_map;//路线图：-1无，0未设置，1已设置
    private int show_location_share;//位置共享：-1无，1未设置，1已设置
    private int show_album;//相册：-1无，0未设置，1已设置
    private int show_message;//留言：-1无，0未设置，1已设置
    private int show_group;//分组：-1无，0未设置，1已设置
    private int show_notice;//最新通知：-1无，0未设置，1已设置
    private int show_more_addr;//更多地点：-1无，0未设置，1已设置
    private int show_prize;//奖品模块：-1无，0未设置，1已设置
    private int show_checkin;//签到模块：-1无，0未设置，1已设置

    private int show_enroll;//	报名模块：-1无，0未设置，1已设置
    private int show_enroll_custom;//	自定义报名信息模块：-1无，0未设置，1已设置
    private int show_manager;//	管理员模块：-1无，0未设置，1已设置
    private int show_video;//	视频：-1无，0未设置，1已设置
    private int show_order;//	订购模块：-1无，0未设置，1已设置


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShow_process() {
        return show_process;
    }

    public void setShow_process(int show_process) {
        this.show_process = show_process;
    }

    public int getShow_menu() {
        return show_menu;
    }

    public void setShow_menu(int show_menu) {
        this.show_menu = show_menu;
    }

    public int getShow_attention() {
        return show_attention;
    }

    public void setShow_attention(int show_attention) {
        this.show_attention = show_attention;
    }

    public int getShow_busi() {
        return show_busi;
    }

    public void setShow_busi(int show_busi) {
        this.show_busi = show_busi;
    }

    public int getShow_navi() {
        return show_navi;
    }

    public void setShow_navi(int show_navi) {
        this.show_navi = show_navi;
    }

    public int getShow_place_img() {
        return show_place_img;
    }

    public void setShow_place_img(int show_place_img) {
        this.show_place_img = show_place_img;
    }

    public int getShow_route_map() {
        return show_route_map;
    }

    public void setShow_route_map(int show_route_map) {
        this.show_route_map = show_route_map;
    }

    public int getShow_location_share() {
        return show_location_share;
    }

    public void setShow_location_share(int show_location_share) {
        this.show_location_share = show_location_share;
    }

    public int getShow_album() {
        return show_album;
    }

    public void setShow_album(int show_album) {
        this.show_album = show_album;
    }

    public int getShow_message() {
        return show_message;
    }

    public void setShow_message(int show_message) {
        this.show_message = show_message;
    }

    public int getShow_group() {
        return show_group;
    }

    public void setShow_group(int show_group) {
        this.show_group = show_group;
    }

    public int getShow_notice() {
        return show_notice;
    }

    public void setShow_notice(int show_notice) {
        this.show_notice = show_notice;
    }

    public int getShow_more_addr() {
        return show_more_addr;
    }

    public void setShow_more_addr(int show_more_addr) {
        this.show_more_addr = show_more_addr;
    }

    public int getShow_prize() {
        return show_prize;
    }

    public void setShow_prize(int show_prize) {
        this.show_prize = show_prize;
    }

    public int getShow_checkin() {
        return show_checkin;
    }

    public void setShow_checkin(int show_checkin) {
        this.show_checkin = show_checkin;
    }

    public int getShow_enroll() {
        return show_enroll;
    }

    public void setShow_enroll(int show_enroll) {
        this.show_enroll = show_enroll;
    }

    public int getShow_enroll_custom() {
        return show_enroll_custom;
    }

    public void setShow_enroll_custom(int show_enroll_custom) {
        this.show_enroll_custom = show_enroll_custom;
    }

    public int getShow_manager() {
        return show_manager;
    }

    public void setShow_manager(int show_manager) {
        this.show_manager = show_manager;
    }

    public int getShow_video() {
        return show_video;
    }

    public void setShow_video(int show_video) {
        this.show_video = show_video;
    }

    public int getShow_order() {
        return show_order;
    }

    public void setShow_order(int show_order) {
        this.show_order = show_order;
    }
}
