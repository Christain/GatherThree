package com.gather.android.model;

import java.io.Serializable;

public class MessageUserListModel implements Serializable {

	private int id;// 用户Id
	private String nick_name;// 用户昵称
	private String birth;// 用户生日
	private int sex;// 用户性别
	private String hobby;// 用户爱好
	private String head_img_url;// 用户头像
	private String intro;// 用户个性签名
	private int is_focus;// 是否关注，0没有关注，1关注
	private int is_vip;// 是否达人，0不是，1是
	private int new_msg_num;// 新消息的条数
	private String content; // 最后一条消息内容
	private String last_contact_time;// 最后一条消息时间
	private int status;// 0正常，1已屏蔽提醒
	private int last_login_platform; // 最后登录平台
	private long baidu_channel_id;
	private String baidu_user_id;

	public int getId() {
		return id;
	}

	public String getNick_name() {
		if (nick_name != null) {
			return nick_name;
		} else {
			return "匿名用户";
		}
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getBirth() {
		if (birth != null) {
			return birth;
		} else {
			return "";
		}
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getHobby() {
		if (hobby != null) {
			return hobby;
		} else {
			return "";
		}
	}

	public String getHead_img_url() {
		if (head_img_url != null) {
			return head_img_url;
		} else {
			return "";
		}
	}

	public String getIntro() {
		if (intro != null) {
			return intro;
		} else {
			return "";
		}
	}

	public int getIs_focus() {
		return is_focus;
	}

	public void setIs_focus(int is_focus) {
		this.is_focus = is_focus;
	}

	public int getIs_vip() {
		return is_vip;
	}

	public void setIs_vip(int is_vip) {
		this.is_vip = is_vip;
	}

	public int getNew_msg_num() {
		return new_msg_num;
	}

	public void setNew_msg_num(int new_msg_num) {
		this.new_msg_num = new_msg_num;
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

	public String getLast_contact_time() {
		if (last_contact_time != null) {
			return last_contact_time;
		} else {
			return "";
		}
	}

	public void setLast_contact_time(String last_contact_time) {
		this.last_contact_time = last_contact_time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getLast_login_platform() {
		return last_login_platform;
	}

	public void setLast_login_platform(int last_login_platform) {
		this.last_login_platform = last_login_platform;
	}

	public long getBaidu_channel_id() {
		return baidu_channel_id;
	}

	public void setBaidu_channel_id(long baidu_channel_id) {
		this.baidu_channel_id = baidu_channel_id;
	}

	public String getBaidu_user_id() {
		if (baidu_user_id != null) {
			return baidu_user_id;
		} else {
			return "";
		}
	}

	public void setBaidu_user_id(String baidu_user_id) {
		this.baidu_user_id = baidu_user_id;
	}

}
