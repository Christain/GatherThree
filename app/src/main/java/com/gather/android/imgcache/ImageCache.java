package com.gather.android.imgcache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.gather.android.constant.Constant;
import com.gather.android.manage.PhoneManage;
import com.gather.android.utils.BitmapUtils;

/*
 * 图片加载\缓存
 */

public class ImageCache {

	private static ImageCache cacheManager;
	public LruCache<String, Bitmap> mMemoryCache;

	public DiskLruCache mDiskCache;

	/** 图片加载队列，后进先出 */
	private Stack<ImageRef> mImageQueue = new Stack<ImageRef>();

	/** 图片请求队列，先进先出，用于存放已发送的请求。 */
	private Queue<ImageRef> mRequestQueue = new LinkedList<ImageRef>();

	/** 图片加载线程消息处理器 */
	private Handler mImageLoaderHandler;

	/** 图片加载线程是否就绪 */
	private boolean mImageLoaderIdle = true;

	/** 请求图片 */
	private static final int MSG_REQUEST_LOAD_IMAGE = 10;
	private static final int MSG_REQUEST_LOAD_ROUND_ICON = 11;
	private static final int MSG_REQUEST_LOAD_ROUND_CORNER_ICON = 12;
	/** 图片加载完成 */
	private static final int MSG_REQUEST_REPLY = 20;
	/** 中止图片加载线程 */
	/** 如果图片是从SD卡加载，则应用渐显动画，如果从缓存读出则不应用动画 */
	private boolean isFromNet = true;
	private int status;

	/**
	 * 获取单例，只能在UI线程中使用。
	 * 
	 * @param context
	 * @return
	 */
	public static ImageCache from(Context context) {
		// 如果不在ui线程中，则抛出异常
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}
		if (cacheManager == null) {
			cacheManager = new ImageCache(context);
		}
		return cacheManager;
	}

	/**
	 * 私有构造函数，保证单例模式
	 * 
	 * @param context
	 */
	private ImageCache(Context context) {
		int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		memClass = memClass > 32 ? 32 : memClass;
		// 使用可用内存的1/8作为图片缓存
		final int cacheSize = 1024 * 1024 * memClass / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}

		};
		mDiskCache = DiskLruCache.openCache(context);
	}

	/**
	 * 存放图片信息
	 */
	class ImageRef {
		ImageView imageView;
		String url;
		String key;
		int loadType;
		int width = 0;
		int height = 0;

		ImageRef(ImageView imageView, String key, String url, int width, int height, int type) {
			this.imageView = imageView;
			this.url = url;
			this.key = key;
			this.width = width;
			this.height = height;
			this.loadType = type;
		}
	}

	public Bitmap getBimtapFromCache(String key) {
		final Bitmap bitmap = mMemoryCache.get(key);
		if (bitmap == null) {
			final Bitmap diskBitmap = mDiskCache.getDiskCache(key);
			return diskBitmap;
		} else {
			return bitmap;
		}
	}

	public Bitmap getBitmapFromMemory(String key) {
		final Bitmap bmp = mMemoryCache.get(key);
		return bmp;
	}

	/**
	 * 加载图片缓存
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 *            默认图的资源Id（小于0时不适用默认图）
	 */
	public void displayImage(ImageView imageView, String url, int resId) {
		String key = url;
		isFromNet = true;
		display(imageView, url, key, resId, 0, 0, MSG_REQUEST_LOAD_IMAGE);
	}

	/**
	 * 加载图片缓存
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 *            默认图的资源Id（小于0时不适用默认图）
	 * @param width
	 *            图的最大宽度
	 * @param height
	 *            图的最大高度
	 */
	public void displayImage(ImageView imageView, String url, int resId, int width, int height) {
		String key = width + url + height;
		display(imageView, url, key, resId, width, height, MSG_REQUEST_LOAD_IMAGE);
	}

	public void displayImageSD(ImageView imageView, String url, int resId) {
		String key = url;
		isFromNet = false;//
		display(imageView, url, key, resId, 0, 0, MSG_REQUEST_LOAD_IMAGE);
	}

	public void displayImageSD(ImageView imageView, String url, int resId, int width, int height) {
		String key = width + url + height;
		isFromNet = false;//
		display(imageView, url, key, resId, width, height, MSG_REQUEST_LOAD_IMAGE);
	}

	public void displayImageSDIcon(ImageView imageView, String filePath, int resId, int width, int height) {
		String key = width + filePath + height;
		isFromNet = false;
		display(imageView, filePath, key, resId, width, height, MSG_REQUEST_LOAD_ROUND_ICON);
	}

	public void displayRoundIcon(ImageView imageView, String url, int resId) {
		String key = url + "_RI";
		display(imageView, url, key, resId, 0, 0, MSG_REQUEST_LOAD_ROUND_ICON);
	}

	public void disPlayRoundIcon(ImageView imageView, String url, int resId, int width, int height) {
		String key = width + url + height + "_RI";
		display(imageView, url, key, resId, width, height, MSG_REQUEST_LOAD_ROUND_ICON);
	}

	public void displayRoundCornerIcon(ImageView imageView, String url, int resId) {
		String key = url + "_RCI";
		display(imageView, url, key, resId, 0, 0, MSG_REQUEST_LOAD_ROUND_CORNER_ICON);
	}

	public void disPlayRoundCornerIcon(ImageView imageView, String url, int resId, int width, int height) {
		String key = width + url + "_RCI" + height;
		display(imageView, url, key, resId, width, height, MSG_REQUEST_LOAD_ROUND_CORNER_ICON);
	}

	private void display(ImageView imageView, String url, String key, int resId, int width, int height, int loadType) {
		if (imageView == null) {
			return;
		}
		if (url == null || url.equals("")) {
			if (resId != -1) {
				imageView.setImageResource(resId);
			} else {
				imageView.setImageBitmap(null);
			}
			return;
		}
		imageView.setTag(key);
		Bitmap bitmap = mMemoryCache.get(key);
		if (bitmap != null) {
			setImageBitmap(imageView, bitmap, false);
		} else {
			if (resId != -1) {
				imageView.setImageResource(resId);
			} else {
				imageView.setImageBitmap(null);
			}
			queueImage(new ImageRef(imageView, key, url, width, height, loadType));
		}
	}

	/**
	 * 添加图片显示渐现动画
	 * 
	 */
	private void setImageBitmap(ImageView imageView, Bitmap bitmap, boolean isTran) {
		isTran = false;
		if (isTran) {
			final TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(android.R.color.transparent), new BitmapDrawable(bitmap) });
			td.setCrossFadeEnabled(true);
			imageView.setImageDrawable(td);
			td.startTransition(50);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * 入队，后进先出
	 * 
	 * @param imageRef
	 */
	public void queueImage(ImageRef imageRef) {
		// 删除已有ImageView
		Iterator<ImageRef> iterator = mImageQueue.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().imageView == imageRef.imageView) {
				iterator.remove();
			}
		}
		// 添加请求
		mImageQueue.push(imageRef);
		sendRequest();
	}

	/**
	 * 发送请求
	 */
	private void sendRequest() {
		// 开启图片加载线程
		if (mImageLoaderHandler == null) {
			HandlerThread imageLoader = new HandlerThread("image_loader");
			imageLoader.start();
			mImageLoaderHandler = new ImageLoaderHandler(imageLoader.getLooper());
		}

		// 发送请求
		if (mImageLoaderIdle && mImageQueue.size() > 0) {
			ImageRef imageRef = mImageQueue.pop();
			Message message = mImageLoaderHandler.obtainMessage(imageRef.loadType, imageRef);
			mImageLoaderHandler.sendMessage(message);
			mImageLoaderIdle = false;
			mRequestQueue.add(imageRef);
		}
	}

	/**
	 * 图片加载线程
	 */
	class ImageLoaderHandler extends Handler {

		public ImageLoaderHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			if (msg == null) {
				return;
			}
			if (msg.obj != null && msg.obj instanceof ImageRef) {
				ImageRef imageRef = (ImageRef) msg.obj;
				Bitmap bitmap;
				try {
					bitmap = loadImage(imageRef);
				} catch (Exception e) {
					e.printStackTrace();
					bitmap = null;
				}
				if (bitmap != null) {
					switch (msg.what) {
					case MSG_REQUEST_LOAD_IMAGE: // 收到请求
						mMemoryCache.put(imageRef.key, bitmap);
						break;
					case MSG_REQUEST_LOAD_ROUND_ICON:
						final Bitmap roundIcon = BitmapUtils.getRoundBitmap(bitmap);
						mMemoryCache.put(imageRef.key, roundIcon);
						bitmap.recycle();
						bitmap = null;
						break;
					case MSG_REQUEST_LOAD_ROUND_CORNER_ICON:
						final Bitmap roundCornerIcon = BitmapUtils.getRoundCornerBitmap(bitmap, 20.0f);
						mMemoryCache.put(imageRef.key, roundCornerIcon);
						bitmap.recycle();
						bitmap = null;
						break;
					}
				}
				if (mImageLoaderHandler != null) {
					Message message = mImageManagerHandler.obtainMessage(MSG_REQUEST_REPLY, mMemoryCache.get(imageRef.key));
					mImageManagerHandler.sendMessage(message);
				}
			}
			// switch (msg.what) {
			// case MSG_REQUEST_LOAD_IMAGE: // 收到请求
			// break;
			// case MSG_REQUEST_LOAD_ROUND_ICON:
			//
			// break;
			// case MSG_REQUEST_LOAD_ROUND_CORNER_ICON:
			//
			// break;
			// case MSG_REQUEST_STOP: // 收到终止指令
			// Looper.myLooper().quit();
			// break;
			//
			// }
		}
	}

	/** UI线程消息处理器 */
	private Handler mImageManagerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
				case MSG_REQUEST_REPLY: // 收到应答
					do {
						ImageRef imageRef = mRequestQueue.remove();
						if (imageRef == null) {
							break;
						}
						if (imageRef.imageView == null || imageRef.imageView.getTag() == null || imageRef.key == null) {
							break;
						}
						if (!(imageRef.key).equals((String) imageRef.imageView.getTag())) {
							break;
						}
						switch (status) {
						case 0:
							Bitmap bitmap = (Bitmap) msg.obj;
							setImageBitmap(imageRef.imageView, bitmap, isFromNet);
							break;
						// case 1:
						// imageRef.imageView.setImageResource(R.drawable.defoult_image_no_disk);
						// break;
						// default:
						// imageRef.imageView.setImageResource(R.drawable.defoult_image_load_fail);
						// break;
						}
					} while (false);
					break;
				}
			}
			// 设置闲置标志
			mImageLoaderIdle = true;
			// 若服务未关闭，则发送下一个请求。
			if (mImageLoaderHandler != null) {
				sendRequest();
			}
		}
	};

	private String createZoomImageUrl(String orgUrl, int width, int height) {
		StringBuilder sb = new StringBuilder();
		int position = orgUrl.lastIndexOf(".");
		String suffix = orgUrl.substring(position);
		String shortUrl = orgUrl.substring(0, position);
		sb.append(shortUrl);
		sb.append("_");
		sb.append(width);
		sb.append("x");
		sb.append(height);
		sb.append(suffix);
		return sb.toString();
	}

	private Bitmap loadImage(ImageRef imageRef) throws Exception {
		status = -1;
		if (!PhoneManage.isSdCardExit()) {
			status = 1;
			return null;
		}
		Bitmap bitmap = null;
		if (imageRef.url != null) {
			String url;
			String key = imageRef.key;
			if (imageRef.width > 0 && imageRef.height > 0) {
				url = createZoomImageUrl(imageRef.url, imageRef.width, imageRef.height);
			} else {
				url = imageRef.url;
			}
			bitmap = mDiskCache.getDiskCache(key);
			if (bitmap == null) {
				if (isFromNet) {
					File cacheFile = mDiskCache.getDiskCacheFile(key);
					File file = loadImageFromUrl(url, cacheFile);
					if (file != null) {
						bitmap = BitmapUtils.getBitmapFromFile(file, -1);
						if (bitmap != null) {
							mDiskCache.addDiskCache(imageRef.key, file.getAbsolutePath());
							status = 0;
						}
					}
				} else {
					bitmap = loadImageFromSD(imageRef);
				}
			} else {
				status = 0;
			}
		}
		return bitmap;
	}

	@SuppressWarnings("NewApi")
	private Bitmap loadImageFromSD(ImageRef imageRef) { // 获取SD卡中的图片
		Bitmap tBitmap = null;
		Bitmap bitmap = null;
		String url = imageRef.url;
		try {
			byte[] data = loadByteArrayFromSD(url);
			if (data != null) {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inSampleSize = 1;
				opt.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(data, 0, data.length, opt);
				int bitmapSize = opt.outHeight * opt.outWidth * 4;// pixels*3
				if (bitmapSize > 1000 * 1200)
					opt.inSampleSize = 2;
				opt.inJustDecodeBounds = false;
				tBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
				if (imageRef.width != 0 && imageRef.height != 0) {
					bitmap = ThumbnailUtils.extractThumbnail(tBitmap, imageRef.width, imageRef.height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				} else {
					bitmap = tBitmap;
					tBitmap = null;
				}
				if (bitmap != null && url != null) {
					if (imageRef.width != 0 && imageRef.height != 0) {
						// mDiskCache.put(url + imageRef.width +
						// imageRef.height, bitmap);
						mMemoryCache.put(url + imageRef.width + imageRef.height, bitmap);
					} else {
						// mDiskCache.put(url, bitmap);
						mMemoryCache.put(url, bitmap);
					}
					status = 0;
				}
			}
			return bitmap;
		} catch (OutOfMemoryError e) {
			tBitmap = null;
			bitmap = null;
		}
		return null;
	}

	private File loadImageFromUrl(String imgurl, File saveFile) { // 从网络下载图片
		try {
			HttpGet httpRequest = new HttpGet(imgurl);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
			InputStream inStream = bufferedHttpEntity.getContent();
//			bitmap = BitmapFactory.decodeStream(is);
			
//			URL url = new URL(imgurl);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setConnectTimeout(6 * 1000);
//			InputStream inStream = conn.getInputStream();
			FileOutputStream outStream = new FileOutputStream(saveFile);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
//			conn.getContentType();
			outStream.flush();
			outStream.close();
			inStream.close();
//			conn.disconnect();
			saveFile.setLastModified(System.currentTimeMillis());
			return saveFile;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			if (saveFile.exists()) {
				saveFile.delete();
				saveFile = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (saveFile.exists()) {
				saveFile.delete();
				saveFile = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (saveFile.exists()) {
				saveFile.delete();
				saveFile = null;
			}
		}
		return null;
	}

	/**
	 * 从SD卡获取图片字节数组
	 * 
	 * @param url
	 * @return
	 */
	private byte[] loadByteArrayFromSD(String url) {
		InputStream is = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			is = new FileInputStream(url);// pathStr 文件路径
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				out.write(b, 0, n);
			}// end while
		} catch (Exception e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return out.toByteArray();

	}

	/**
	 * Activity#onStop后，ListView不会有残余请求。
	 */
	public void stop() {
		// 清空请求队列
		mImageQueue.clear();
	}

}
