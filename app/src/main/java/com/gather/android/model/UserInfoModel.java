package com.gather.android.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserInfoModel implements Serializable {

	private int id; // 用户ID
	private String birth; // 生日1999-09-09 00:00:00
	private int sex; // 性别1男2女
	private String nick_name; // 昵称
	private String real_name; // 真实姓名
	private String head_img_url; // 头像
	private String pho_num; // 电话号码
	private String hobby; // 爱好
	private String intro; // 个性签名
	private int is_vip; // 在该城市是否成为达人：0否，1是
	private int focus_num;// 关注数
	private int fans_num;// 粉丝数
	private int is_focus;// 是否已关注：0否，1是
	private String email; // 邮箱
	private String address; // 地址
	private int is_regist; // 是否已完成注册：0未完成，1已完成
	private String contact_phone; // 联系电话
	private long sina_expires_in; // 新浪过期时间
	private String sina_token; // 新浪token
	private String sina_openid; // 新浪openId
	private long qq_expires_in; // 腾讯过期时间
	private String qq_openid; // 腾讯openId
	private String qq_token; // 腾讯token
	private String contact_qq; // 联系QQ
	private String last_login_time; // 最后登录时间
	private int is_shield;// 是否已屏蔽此联系人：0否，1是
	private String baidu_user_id;// 聊天消息推送
	private long baidu_channel_id;// 聊天消息推送
	private int last_login_platform;// 最后登录平台

	public int getUid() {
		return id;
	}

	public void setUid(int uid) {
		this.id = uid;
	}

	public String getBirth() {
		if (birth != null) {
			return birth;
		} else {
			return "";
		}
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
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

	public String getReal_name() {
		if (real_name != null) {
			return real_name;
		} else {
			return "";
		}
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getHead_img_url() {
		if (head_img_url != null) {
			return head_img_url;
		} else {
			return "";
		}
	}

	public void setHead_img_url(String head_img_url) {
		this.head_img_url = head_img_url;
	}

	public String getPho_num() {
		if (pho_num != null) {
			return pho_num;
		} else {
			return "";
		}
	}

	public void setPho_num(String pho_num) {
		this.pho_num = pho_num;
	}

	public String getHobby() {
		if (hobby != null) {
			return hobby;
		} else {
			return "";
		}
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getEmail() {
		if (email != null) {
			return email;
		} else {
			return "";
		}
	}

	public void setEmail(String email) {
		this.email = email;
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

	public int getIs_vip() {
		return is_vip;
	}

	public void setIs_vip(int is_vip) {
		this.is_vip = is_vip;
	}

	public int getFocus_num() {
		return focus_num;
	}

	public void setFocus_num(int focus_num) {
		this.focus_num = focus_num;
	}

	public int getFans_num() {
		return fans_num;
	}

	public void setFans_num(int fans_num) {
		this.fans_num = fans_num;
	}

	public int getIs_focus() {
		return is_focus;
	}

	public void setIs_focus(int is_focus) {
		this.is_focus = is_focus;
	}

	public String getAddress() {
		if (address != null) {
			return address;
		} else {
			return "";
		}
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getIs_regist() {
		return is_regist;
	}

	public void setIs_regist(int is_regist) {
		this.is_regist = is_regist;
	}

	public String getContact_phone() {
		if (contact_phone != null) {
			return contact_phone;
		} else {
			return "";
		}
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public long getSina_expires_in() {
		return sina_expires_in;
	}

	public void setSina_expires_in(long sina_expires_in) {
		this.sina_expires_in = sina_expires_in;
	}

	public String getSina_token() {
		if (sina_token != null) {
			return sina_token;
		} else {
			return "";
		}
	}

	public void setSina_token(String sina_token) {
		this.sina_token = sina_token;
	}

	public String getSina_openid() {
		if (sina_openid != null) {
			return sina_openid;
		} else {
			return "";
		}
	}

	public void setSina_openid(String sina_openid) {
		this.sina_openid = sina_openid;
	}

	public long getQq_expires_in() {
		return qq_expires_in;
	}

	public void setQq_expires_in(long qq_expires_in) {
		this.qq_expires_in = qq_expires_in;
	}

	public String getQq_openid() {
		if (qq_openid != null) {
			return qq_openid;
		} else {
			return "";
		}
	}

	public void setQq_openid(String qq_openid) {
		this.qq_openid = qq_openid;
	}

	public String getQq_token() {
		if (qq_token != null) {
			return qq_token;
		} else {
			return "";
		}
	}

	public void setQq_token(String qq_token) {
		this.qq_token = qq_token;
	}

	public String getContact_qq() {
		if (contact_qq != null) {
			return contact_qq;
		} else {
			return "";
		}
	}

	public void setContact_qq(String contact_qq) {
		this.contact_qq = contact_qq;
	}

	public String getLast_login_time() {
		if (last_login_time != null) {
			return last_login_time;
		} else {
			return "";
		}
	}

	public void setLast_login_time(String last_login_time) {
		this.last_login_time = last_login_time;
	}

	public int getIs_shield() {
		return is_shield;
	}

	public void setIs_shield(int is_shield) {
		this.is_shield = is_shield;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public long getBaidu_channel_id() {
		return baidu_channel_id;
	}

	public void setBaidu_channel_id(long baidu_channel_id) {
		this.baidu_channel_id = baidu_channel_id;
	}

	public int getLast_login_platform() {
		return last_login_platform;
	}

	public void setLast_login_platform(int last_login_platform) {
		this.last_login_platform = last_login_platform;
	}

}
