package com.gather.android.model;

public class ChatMessageModel {

	private String create_time; // 创建时间（例：1997-07-01 09:00:00）
	private String content; // 内容
	private int id; // 用户私信关联id
	private int role; // 角色：1自己发送者，2自己接收者 和u_id联系起来
	private int u_id; // 自己id
	private int contact_id; // 联系人id

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

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getU_id() {
		return u_id;
	}

	public void setU_id(int u_id) {
		this.u_id = u_id;
	}

	public int getContact_id() {
		return contact_id;
	}

	public void setContact_id(int contact_id) {
		this.contact_id = contact_id;
	}

}
