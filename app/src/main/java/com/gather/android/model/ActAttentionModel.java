package com.gather.android.model;

/**
 * Created by Administrator on 2015/3/28.
 */
public class ActAttentionModel {

    private int id;//注意事项ID
    private String subject;//内容

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
}
