package com.gather.android.model;

import android.graphics.Bitmap;

import com.gather.android.utils.BitmapUtils;

import java.io.File;
import java.io.Serializable;

public class UserPhotoModel implements Serializable {

	private int id;
	private String img_url;
	private Bitmap bmp;
	private File file;
	private String path;

    public UserPhotoModel() {

    }

	public UserPhotoModel(File file) {
		this.file = file;
	}

	public UserPhotoModel(File file, String path) {
		this.file = file;
		this.path = path;
	}

	public void destroy() {
		if (bmp != null && bmp.isRecycled()) {
			bmp.recycle();
		}
		bmp = null;
	}

	public void destroyAll() {
		if (bmp != null && bmp.isRecycled()) {
			bmp.recycle();
		}
		bmp = null;
		file.delete();
	}

	public Bitmap getImageBmp() {
		if (bmp == null) {
			this.bmp = BitmapUtils.getBitmapFromFile(file, 400 * 400);
		}
		return bmp;
	}

	public File getImageFile() {
		return file;
	}

	public String getPath() {
		return path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImg_url() {
		if (img_url != null) {
			return img_url;
		} else {
			return "";
		}
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

}
