package com.gather.android.model;

import java.io.Serializable;

import com.gather.android.utils.TimeUtil;

@SuppressWarnings("serial")
public class TrendsModel implements Serializable {

	private String create_time;
	private String content;
	private int id;
	private int comment_num;
	private int author_id;
	private TrendsPicObjectModel imgs;
	private UserInfoModel user;
	private boolean local = false;// 是不是本地数据库未发布的动态

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

	public void setId(int id) {
		this.id = id;
	}

	public int getComment_num() {
		return comment_num;
	}

	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}

	public int getAuthor_id() {
		return author_id;
	}

	public void setAuthor_id(int author_id) {
		this.author_id = author_id;
	}

	public TrendsPicObjectModel getImgs() {
		return imgs;
	}

	public int getType() {
		if (imgs.getTotal_num() == 0) {
			return 0;
		} else if (imgs.getTotal_num() == 1) {
			return 1;
		} else {
			return 2;
		}
	}

	public int getUserTrendsType() {
		if (imgs.getTotal_num() == 0) {
			return 0;
		} else if (imgs.getTotal_num() == 1) {
			return 1;
		} else if (imgs.getTotal_num() == 2) {
			return 2;
		} else {
			return 3;
		}
	}

	public UserInfoModel getUser() {
		if (user != null) {
			return user;
		} else {
			UserInfoModel model = new UserInfoModel();
			return model;
		}
	}

	public void setUser(UserInfoModel user) {
		this.user = user;
	}

	public String getUserTrendsTime() {
		if (create_time != null && !create_time.equals("")) {
			return TimeUtil.getUserTrendsTime(TimeUtil.getStringtoLong(create_time));
		} else {
			return "未知";
		}
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public void setImgs(TrendsPicObjectModel imgs) {
		this.imgs = imgs;
	}

}
