package com.gather.android.model;

import java.io.Serializable;

/**
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumContentModel implements Serializable{

    private int id;
    private String subject;
    private String cover_url;
    private String create_time;
    private int sum;//图片总数
    private UserInfoModel user;
    private boolean isOwner = false;//是不是主办方
    private int type;//1是主办方相册，2是主办方视频

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        if (subject != null) {
            return subject;
        } else {
            return "";
        }
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCover_url() {
        if (cover_url != null) {
            return cover_url;
        } else {
            return "";
        }
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getCreate_time() {
        if (create_time != null) {
            return create_time;
        } else {
            return "";
        }
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public UserInfoModel getUser() {
        if (user != null) {
            return user;
        } else {
            return new UserInfoModel();
        }
    }

    public void setUser(UserInfoModel user) {
        this.user = user;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
