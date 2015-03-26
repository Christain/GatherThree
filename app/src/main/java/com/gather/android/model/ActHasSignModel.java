package com.gather.android.model;

public class ActHasSignModel {

	private int id;//签到ID
	private int act_id;
	private String create_time;//签到时间
	private ActModel act;

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

	public ActModel getAct() {
		return act;
	}

	public void setAct(ActModel act) {
		this.act = act;
	}

}
