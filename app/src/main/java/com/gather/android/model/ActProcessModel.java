package com.gather.android.model;

/**
 * Created by Christain on 2015/3/18.
 */
public class ActProcessModel {

    private int id;
    private String b_time;//开始时间（例：11:11:11）
    private String e_time;//结束时间
    private String subject;//项目
    private int status;//状态：-1已删除，0未设置，1即将开始，2正在进行，3已完成

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
