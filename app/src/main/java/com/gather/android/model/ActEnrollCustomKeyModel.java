package com.gather.android.model;

/**
 * Created by Christain on 2015/3/31.
 */
public class ActEnrollCustomKeyModel {

    private int id;//id
    private String subject;//key名称
    private String hint;//输入提示
    private String descri;//描述

    private String content;//输入的内容

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

    public String getHint() {
        if (hint != null) {
            return hint;
        }
        return "";
    }

    public void setHint(String hint) {
        this.hint = hint;
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

    public String getContent() {
        if (content != null) {
            return content;
        }
        return "";
    }

    public void setContent(String content) {
        this.content = content;
    }
}
