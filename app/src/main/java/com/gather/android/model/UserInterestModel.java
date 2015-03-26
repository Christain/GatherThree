package com.gather.android.model;

public class UserInterestModel {

	private int id;
	private String name;
	private boolean isSelect;

	public int getId() {
		return id;
	}

	public String getName() {
		if (name != null) {
			return name;
		} else {
			return "";
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

}
