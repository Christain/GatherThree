package com.gather.android.model;

public class CityListModel {

	private int id; // 城市Id
	private String name; // 城市名字
	private String create_time; // 创建时间
	private int status; // 状态：-1已删除，0正常
	private boolean isSelect;// 选择城市时判断是否被选择

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

}
