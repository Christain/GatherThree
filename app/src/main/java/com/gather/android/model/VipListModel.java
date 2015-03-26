package com.gather.android.model;

import java.util.ArrayList;

public class VipListModel {

	private ArrayList<UserInfoModel> queue;
	private ArrayList<UserInfoModel> users;

	public ArrayList<UserInfoModel> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<UserInfoModel> queue) {
		this.queue = queue;
	}

	public ArrayList<UserInfoModel> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<UserInfoModel> users) {
		this.users = users;
	}

}
