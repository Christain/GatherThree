package com.gather.android.imgcache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.gather.android.constant.Constant;
import com.gather.android.manage.PhoneManage;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.MD5;

/*
 * 图片本地缓存
 */
public class DiskLruCache {
	private static final int INITIAL_CAPACITY = 32;
	private static final float LOAD_FACTOR = 0.75f;
	private final Map<String, String> mLinkedHashMap = Collections.synchronizedMap(new LinkedHashMap<String, String>(
			INITIAL_CAPACITY, LOAD_FACTOR, true));
	private File cacheDir;

	// /**
	// * A filename filter to use to identify the cache filenames which have
	// * CACHE_FILENAME_PREFIX prepended.
	// */
	// private static final FilenameFilter cacheFileFilter = new
	// FilenameFilter() {
	// @Override
	// public boolean accept(File dir, String filename) {
	// return filename.startsWith(CACHE_FILENAME_PREFIX);
	// }
	// };

	/**
	 * Used to fetch an instance of DiskLruCache.
	 * 
	 * @param context
	 * @param cacheDir
	 * @param maxByteSize
	 * @return
	 */
	public static DiskLruCache openCache(Context context) {
		return new DiskLruCache(context);
	}

	private DiskLruCache(Context context) {
		cacheDir = getDiskImageCacheDir();
		if (PhoneManage.isSdCardExit() && !cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	/**
	 * 添加本地缓存
	 * 
	 * @param key
	 * @param data
	 */
	public void addDiskCache(String key, String filePath) {
		synchronized (mLinkedHashMap) {
			if (mLinkedHashMap.get(key) == null) {
				mLinkedHashMap.put(key, filePath);
			}
		}
	}

	public File getDiskCacheFile(String key) {
		if (key != null && !key.equals("")) {
			return new File(cacheDir, MD5.md5Encode(key));
		}
		return null;
	}

	/**
	 * 添加本地缓存
	 * 
	 * @param key
	 * @param data
	 */
	public void addDiskCache(String key, Bitmap data) {
		synchronized (mLinkedHashMap) {
			if (mLinkedHashMap.get(key) == null) {
				try {
					final File file = getDiskCacheFile(cacheDir, key);
					if (file != null && writeBitmapToFile(data, file)) {
						mLinkedHashMap.put(key, file.getAbsolutePath());
					}
				} catch (final FileNotFoundException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将bitmap写为指定的图片文件
	 * 
	 * @param bmp
	 * @param file
	 * @return
	 * @throws java.io.IOException
	 * @throws java.io.FileNotFoundException
	 */
	private boolean writeBitmapToFile(Bitmap bmp, File file) throws IOException, FileNotFoundException {
		boolean b = false;
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file), 4 * 1024);
			b = bmp.compress(CompressFormat.JPEG, 90, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return b;
	}

	/**
	 * 获取本地缓存
	 * 
	 * @param cacheDir
	 * @param key
	 * @return
	 */
	public Bitmap getDiskCache(String key) {
		synchronized (mLinkedHashMap) {
			try {
				if (cacheExist(key)) {
					final String path = mLinkedHashMap.get(key);
					return getDiskCacheFileBitmap(path);
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * 检查缓存文件中是否存在该缓存
	 * 
	 * @param key
	 * @return
	 */
	public boolean cacheExist(String key) {
		if (mLinkedHashMap.containsKey(key)) {
			final String path = mLinkedHashMap.get(key);
			if (new File(path).exists()) {
				return true;
			} else {
				mLinkedHashMap.remove(key);
			}
		}
		File existingFile = getDiskCacheFile(cacheDir, key);
		if (existingFile != null && existingFile.exists()) {
			mLinkedHashMap.put(key, existingFile.getAbsolutePath());
			return true;
		}
		return false;
	}

	/**
	 * 获取图片缓存文件夹
	 * 
	 * @return
	 */
	public static File getDiskImageCacheDir() {
		return new File(Constant.IMAGE_CACHE_DIR_PATH);
	}

	/**
	 * 获取系统图片缓存文件夹
	 * 
	 * @return
	 */
	public static File getDiskAppImageCacheDir() {
		return new File(Constant.IMAGE_CACHE_DIR_PATH);
	}

	/**
	 * 获取缓存文件
	 * 
	 * @param dir
	 * @param url
	 * @return
	 */
	public static File getDiskCacheFile(File dir, String key) {
		if (dir != null && key != null && !key.equals("")) {
			return new File(dir, MD5.md5Encode(key));
		}
		return null;
	}

	/**
	 * 获取缓存文件的bitmap
	 * 
	 * @param filepath
	 * @return
	 */
	public static Bitmap getDiskCacheFileBitmap(String filepath) {
		if (filepath != null && !filepath.equals("")) {
			Bitmap bitmap = BitmapUtils.getBitmapFromFile(new File(filepath), -1);
			return bitmap;
		}
		return null;
	}
}
