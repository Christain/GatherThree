package com.gather.android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SelectPictureModel implements Serializable {
	private String picParentPath; // 图片父类路径
	private int picNum = 1; // 图片数量
	private String imageID; // 图片id
	private String picName; // 图片名字
	private ArrayList<String> picPathList; // 图片路径

	public String getPicParentPath() {
		return picParentPath;
	}

	public void setPicParentPath(String picParentPath) {
		this.picParentPath = picParentPath;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}

	public String getImageID() {
		return imageID;
	}

	public void setImageID(String imageID) {
		this.imageID = imageID;
	}

	public ArrayList<String> getPicPath() {
		return picPathList;
	}

	public void setPicPath(ArrayList<String> picPathList) {
		this.picPathList = picPathList;
	}

	public int getPicNum() {
		return picNum;
	}

	public void setPicNum(int picNum) {
		this.picNum = picNum;
	}

}
