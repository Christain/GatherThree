package com.gather.android.model;


public class ActHasEnrollModel {

	private int id;//报名Id
	private int act_id;//活动Id
	private ActModel act;//活动内容
	private String name;//姓名
	private String phone;//联系电话
	private int people_num;//人数
	private String create_time;//时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAct_id() {
		return act_id;
	}

	public void setAct_id(int act_id) {
		this.act_id = act_id;
	}

	public ActModel getAct() {
		return act;
	}

	public void setAct(ActModel act) {
		this.act = act;
	}

	public String getName() {
		if (name != null) {
			return name;
		} else {
			return "";
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		if (phone != null) {
			return phone;
		} else {
			return "";
		}
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getPeople_num() {
		return people_num;
	}

	public void setPeople_num(int people_num) {
		this.people_num = people_num;
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
