package com.gather.android.model;

/**
 * Created by Christain on 2015/3/31.
 */
public class ActNotifyModel {

    private int id;
    private String subject;
    private String descri;
    private String create_time;

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

    public String getDescri() {
        if (descri != null) {
            return descri;
        }
        return "";
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getCreate_time() {
        if (create_time != null) {
            return create_time;
        }
        return "";
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
