package com.gather.android.model;

/**
 * Created by Christain on 2015/3/18.
 */
public class ActEnrollStatusCommentModel {

    private int id;//评论id
    private int author_id;//发送者的用户id
    private int is_admin;//是否为管理员消息：0否，1是
    private String content;
    private String create_time;
    private UserInfoModel user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public int getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(int is_admin) {
        this.is_admin = is_admin;
    }

    public String getContent() {
        if (content != null) {
            return content;
        } else {
            return "";
        }
    }

    public void setContent(String content) {
        this.content = content;
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
}
