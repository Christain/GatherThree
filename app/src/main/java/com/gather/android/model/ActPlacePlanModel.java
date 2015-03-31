package com.gather.android.model;

/**
 * Created by Christain on 2015/3/30.
 */
public class ActPlacePlanModel {

    private int id;
    private String subject;
    private String img_url;

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

    public String getImg_url() {
        if (img_url != null) {
            return img_url;
        }
        return "";
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
