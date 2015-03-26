package com.gather.android.model;

public class ActCommentModel {

	private int id;//评论Id
	private int author_id;//发送者的用户id
	private UserInfoModel user;
	private String content;//评论内容
	private String create_time;//评论时间

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

	public UserInfoModel getUser() {
		return user;
	}

	public void setUser(UserInfoModel user) {
		this.user = user;
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

}
