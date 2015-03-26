package com.gather.android.model;

public class VersionModel {

	private int code;
	private String name;
	private String descri;
	private String ver_url;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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

	public String getDescri() {
		if (descri != null) {
			return descri;
		} else {
			return "";
		}
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public String getVer_url() {
		if (ver_url != null) {
			return ver_url;
		} else {
			return "";
		}
	}

	public void setVer_url(String ver_url) {
		this.ver_url = ver_url;
	}

}
