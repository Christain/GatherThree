package com.gather.android.model;

import java.io.Serializable;

/**
 * Created by Christain on 2015/3/27.
 */
public class ActMoreInfoModel implements Serializable{

    private int id;//活动id
    private int enroll_limit;//是否限制报名人数：0否，1是
    private int enroll_limit_num;//报名限制人数
    private int limit_sex_num;//是否分性别限制
    private int limit_male_num;//限制男性人数
    private int limit_female_num;//限制女性人数
    private int can_with_people;//是否允许携带随行人员：0否，1是
    private int with_people_limit_num;//随行人员限制人数
    private String busi_url;//主办方介绍url
    private int album_id;//自己的相册id：-1无
    private int group_id;//自己的分组id：-1无
    private int enroll_status;//自己的报名状态：-1未报名，0未报名，1审核中，2核中， 3通过，4拒绝
    private int enroll_num;//已成功报名的人数
    private int enroll_male_num;//已成功报名的男性人数
    private int enroll_female_num;//已成功报名的女性人数
    private int is_manager;//自己是否是该活动管理员：0否，1是
    private int serial_no;//序号，报名成功后的id
    private String pass_no;//编号，分组后可能有的编号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnroll_limit() {
        return enroll_limit;
    }

    public void setEnroll_limit(int enroll_limit) {
        this.enroll_limit = enroll_limit;
    }

    public int getEnroll_limit_num() {
        return enroll_limit_num;
    }

    public void setEnroll_limit_num(int enroll_limit_num) {
        this.enroll_limit_num = enroll_limit_num;
    }

    public int getLimit_sex_num() {
        return limit_sex_num;
    }

    public void setLimit_sex_num(int limit_sex_num) {
        this.limit_sex_num = limit_sex_num;
    }

    public int getLimit_male_num() {
        return limit_male_num;
    }

    public void setLimit_male_num(int limit_male_num) {
        this.limit_male_num = limit_male_num;
    }

    public int getLimit_female_num() {
        return limit_female_num;
    }

    public void setLimit_female_num(int limit_female_num) {
        this.limit_female_num = limit_female_num;
    }

    public int getCan_with_people() {
        return can_with_people;
    }

    public void setCan_with_people(int can_with_people) {
        this.can_with_people = can_with_people;
    }

    public int getWith_people_limit_num() {
        return with_people_limit_num;
    }

    public void setWith_people_limit_num(int with_people_limit_num) {
        this.with_people_limit_num = with_people_limit_num;
    }

    public String getBusi_url() {
        if (busi_url != null) {
            return busi_url;
        } else {
            return "";
        }
    }

    public void setBusi_url(String busi_url) {
        this.busi_url = busi_url;
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

    public int getEnroll_status() {
        return enroll_status;
    }

    public void setEnroll_status(int enroll_status) {
        this.enroll_status = enroll_status;
    }

    public int getEnroll_num() {
        return enroll_num;
    }

    public void setEnroll_num(int enroll_num) {
        this.enroll_num = enroll_num;
    }

    public int getEnroll_male_num() {
        return enroll_male_num;
    }

    public void setEnroll_male_num(int enroll_male_num) {
        this.enroll_male_num = enroll_male_num;
    }

    public int getEnroll_female_num() {
        return enroll_female_num;
    }

    public void setEnroll_female_num(int enroll_female_num) {
        this.enroll_female_num = enroll_female_num;
    }

    public int getIs_manager() {
        return is_manager;
    }

    public void setIs_manager(int is_manager) {
        this.is_manager = is_manager;
    }

    public int getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(int serial_no) {
        this.serial_no = serial_no;
    }

    public String getPass_no() {
        if (pass_no != null) {
            return pass_no;
        }
        return "null";
    }

    public void setPass_no(String pass_no) {
        this.pass_no = pass_no;
    }
}
