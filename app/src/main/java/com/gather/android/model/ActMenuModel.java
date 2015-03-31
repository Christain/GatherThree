package com.gather.android.model;

/**
 * Created by Christain on 2015/3/30.
 */
public class ActMenuModel {

    private int id;//菜单Id
    private int type;//类型：1午餐，2晚餐
    private String subject;//菜单内容

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}
