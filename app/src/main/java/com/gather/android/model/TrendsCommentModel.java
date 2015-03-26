package com.gather.android.model;

public class TrendsCommentModel {

	private String create_time;
	private String content;
	private int id; // 评论Id
	private UserInfoModel author_user;
	private int author_id;// 发送者id
	private int at_id; // 被回复用户id
	private UserInfoModel at_user;

	public String getCreate_time() {
		if (create_time != null) {
			return create_time;
		} else {
			return "";
		}
	}

	public void setAuthor_id(int author_id) {
		this.author_id = author_id;
	}

	public void setAt_id(int at_id) {
		this.at_id = at_id;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
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

	public int getId() {
		return id;
	}

	public UserInfoModel getAuthor_user() {
		if (author_user != null) {
			return author_user;
		} else {
			UserInfoModel model = new UserInfoModel();
			return model;
		}
	}

	public void setAuthor_user(UserInfoModel author_user) {
		this.author_user = author_user;
	}

	public int getAuthor_id() {
		return author_id;
	}

	public int getAt_id() {
		return at_id;
	}

	public UserInfoModel getAt_user() {
		if (at_user != null) {
			return at_user;
		} else {
			UserInfoModel model = new UserInfoModel();
			return model;
		}
	}

	public void setAt_user(UserInfoModel at_user) {
		this.at_user = at_user;
	}

}
