package com.gather.android.model;

import java.io.Serializable;

public class NewsModel implements Serializable {

	private int id;// 活动id
	private String title;
	private String intro;//
	private String detail_url;// 详情url
	private String h_img_url;// 资讯首图url
	private int is_loved;// 是否已添加感兴趣：-1不可再添加，0未添加，1已添加
	private int loved_num;
	private String lov_time;// 收藏时间
	private String publish_time;// 发布时间（攻略，回忆，票务）
	private int type_id; // 类型id：1攻略，2回忆，3票务，4专访
	private double price;// 订票价格

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		if (title != null) {
			return title;
		} else {
			return "";
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntro() {
		if (intro != null) {
			return intro;
		} else {
			return "";
		}
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getDetail_url() {
		if (detail_url != null) {
			return detail_url;
		} else {
			return "";
		}
	}

	public void setDetail_url(String detail_url) {
		this.detail_url = detail_url;
	}

	public String getH_img_url() {
		if (h_img_url != null) {
			return h_img_url;
		} else {
			return "";
		}
	}

	public void setH_img_url(String h_img_url) {
		this.h_img_url = h_img_url;
	}

	public int getIs_loved() {
		return is_loved;
	}

	public void setIs_loved(int is_loved) {
		this.is_loved = is_loved;
	}

	public int getLoved_num() {
		return loved_num;
	}

	public void setLoved_num(int loved_num) {
		this.loved_num = loved_num;
	}

	public String getLov_time() {
		if (lov_time != null) {
			return lov_time;
		} else {
			return "";
		}
	}

	public void setLov_time(String lov_time) {
		this.lov_time = lov_time;
	}

	public String getPublish_time() {
		if (publish_time != null) {
			return publish_time;
		} else {
			return "";
		}
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public int getType_id() {
		return type_id;
	}

	public void setType_id(int type_id) {
		this.type_id = type_id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
