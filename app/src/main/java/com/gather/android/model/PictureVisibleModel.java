package com.gather.android.model;

public class PictureVisibleModel {

	private String path;
	private boolean isVisible;

	public String getPath() {
		if (path != null) {
			return path;
		} else {
			return "";
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

}
