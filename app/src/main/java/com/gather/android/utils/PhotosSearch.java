package com.gather.android.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.gather.android.constant.Constant;
import com.gather.android.model.SelectPictureModel;

public class PhotosSearch {
	private Context context;

	public PhotosSearch(Context context) {
		this.context = context;
	}

	public List<SelectPictureModel> getImages() {
		// 指定要查询的uri资源
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		// 获取ContentResolver
		ContentResolver contentResolver = context.getContentResolver();
		// 查询的字段
		String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
		// // 条件
		// String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
		// // 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)
		// String[] selectionArgs = { "image/jpeg" };
		// 排序
		String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
		// 查询sd卡上的图片
		Cursor cursor = contentResolver.query(uri, projection, null, null, sortOrder);
		List<SelectPictureModel> imageList = new ArrayList<SelectPictureModel>();
		if (cursor != null) {
			// PicModel model = null;
			// String imageId, picName, picParentPath, path;
			cursor.moveToFirst();
			while (cursor.moveToNext()) {
				boolean isContain = false;
				String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
				String picName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
				String picParentPath = getParentPath(path);
				// 获得图片的id
				// imageMap.put("imageID",
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
				// model.setImageID(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
				// 获得图片显示的名称
				// imageMap.put("imageName",
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				// model.setPicName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				// 获得图片的信息
				// imageMap.put("imageInfo",
				// "" +
				// cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
				// / 1024) + "kb");
				// 获得图片所在的路径(可以使用路径构建URI)
				// imageMap.put("data",
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
				// model.setPicPath(getParentPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
				// Log.e("url",
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
				boolean isPic = path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".PNG") || path.endsWith(".JPG") || path.endsWith("JPEG") || path.endsWith("jpeg");
				if (isPic && size > 0) {
					for (int i = 0; i < imageList.size(); i++) {
						SelectPictureModel model = imageList.get(i);
						if (model.getPicParentPath().equals(picParentPath)) {
							model.setPicNum(model.getPicNum() + 1);
							ArrayList<String> list = model.getPicPath();
							list.add(path);
							model.setPicPath(list);
							isContain = true;
							imageList.remove(i);
							imageList.add(model);
							// break;
						}
					}
					if (!isContain) {
						SelectPictureModel model = new SelectPictureModel();
						model.setImageID(imageId);
						model.setPicParentPath(picParentPath);
						ArrayList<String> list = new ArrayList<String>();
						list.add(path);
						model.setPicPath(list);
						model.setPicName(getShowTitle(picParentPath));
						imageList.add(model);
					}
				} else {
					if (Constant.SHOW_LOG) {
						Log.e("picpath", path);
					}
				}
			}
			cursor.close();
		}
		return imageList;
	}

	private String getParentPath(String path) {
		String parentPath = "";
		if (null != path && !path.equals("")) {
			String[] paths = path.split("/");
			parentPath = path.replace(paths[paths.length - 1], ""); 
		}
		return parentPath;
	}

	private String getShowTitle(String parentPath) {
		String title = "";
		if (null != parentPath && !parentPath.equals("")) {
			String[] paths = parentPath.split("/");
			title = paths[paths.length - 1];
		}
		return title;
	}
}