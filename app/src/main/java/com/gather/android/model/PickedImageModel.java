package com.gather.android.model;

import java.io.File;
import java.io.Serializable;

import android.graphics.Bitmap;

import com.gather.android.utils.BitmapUtils;


public class PickedImageModel {
	private Bitmap bmp;
	private File file;
	private String path;

	public PickedImageModel(File file) {
		this.file = file;
	}

	public PickedImageModel(File file, String path) {
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
}
