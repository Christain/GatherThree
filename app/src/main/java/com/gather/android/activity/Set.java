package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogShareAct;
import com.gather.android.dialog.DialogShareAct.ShareClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.MultipartRequest;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.ShareToSinaParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用设置
 */
@SuppressLint("InflateParams")
public class Set extends SwipeBackActivity implements OnClickListener {

	private RelativeLayout rlTitle;
	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private LinearLayout llRecommend, llBind, llClean, llExit;
	private TextView tvCacheSize;

	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;

	/********** 第三方绑定 *************/
	private File mFile = null;
	private Tencent mTencent;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
	private IWXAPI wxApi;
	private String Share_Message = "集合啦！脱宅利器必备神器-你想要的活动，这里都有！";
	private String Share_Title = "各类活动哪家强？中国成都找集合。";

	@Override
	protected int layoutResId() {
		return R.layout.set;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.GONE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("设置");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
		this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);
		this.dialog = DialogTipsBuilder.getInstance(Set.this);
		this.mLoadingDialog = LoadingDialog.createDialog(Set.this, true);
		this.llRecommend = (LinearLayout) findViewById(R.id.llRecommend);
		this.llBind = (LinearLayout) findViewById(R.id.llBind);
		this.llClean = (LinearLayout) findViewById(R.id.llClean);
		this.llExit = (LinearLayout) findViewById(R.id.llExit);
		this.tvCacheSize = (TextView) findViewById(R.id.tvCacheSize);

		this.llRecommend.setOnClickListener(this);
		this.llBind.setOnClickListener(this);
		this.llClean.setOnClickListener(this);
		this.llExit.setOnClickListener(this);

		// this.setCacheSize();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.llRecommend:
			if (!ClickUtil.isFastClick()) {
				DialogShareAct shareDialog = new DialogShareAct(Set.this, R.style.ShareDialog).setOnShareClickListener(new ShareClickListener() {
					@Override
					public void OnShareClickListener(int position) {
						switch (position) {
						case DialogShareAct.WECHAT:
							ShareToWeChat();
							break;
						case DialogShareAct.SQUARE:
							ShareToSquare();
							break;
						case DialogShareAct.ZOON:
							ShareToZone();
							break;
						case DialogShareAct.SINA:
							if (!AppPreference.getUserPersistent(Set.this, AppPreference.SINA_ID).equals("")) {
								Oauth2AccessToken token = new Oauth2AccessToken();
								token.setUid(AppPreference.getUserPersistent(Set.this, AppPreference.SINA_ID));
								token.setToken(AppPreference.getUserPersistent(Set.this, AppPreference.SINA_TOKEN));
								token.setExpiresTime(AppPreference.getUserPersistentLong(Set.this, AppPreference.SINA_EXPIRES));
								if (!token.isSessionValid()) {
									if (checkSinaPackage()) {
										mSsoHandler = new SsoHandler(Set.this, mWeiboAuth);
										mSsoHandler.authorize(new AuthListener());
									} else {
										mWeiboAuth.anthorize(new AuthListener());
									}
								} else {
									ShareToSina();
								}
							} else {
								if (checkSinaPackage()) {
									mSsoHandler = new SsoHandler(Set.this, mWeiboAuth);
									mSsoHandler.authorize(new AuthListener());
								} else {
									mWeiboAuth.anthorize(new AuthListener());
								}
							}
							break;
						}
					}
				});
				shareDialog.show();
			}
			break;
		case R.id.llBind:
			if (!ClickUtil.isFastClick()) {
				GatherApplication application = (GatherApplication) getApplication();
				if (application.getUserInfoModel() != null) {
					Intent intent = new Intent(Set.this, BindThird.class);
					intent.putExtra("MODEL", application.getUserInfoModel());
					startActivity(intent);
				} else {
					toast("帐号信息有误");
				}
			}
			break;
		case R.id.llClean:
			if (!ClickUtil.isFastClick()) {
				final DialogChoiceBuilder clear = DialogChoiceBuilder.getInstance(Set.this);
				clear.setMessage("您确定要清理缓存？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						new ProgressClear().execute();
						clear.dismiss();
					}
				}).show();
			}
			break;
		case R.id.llExit:
			if (!ClickUtil.isFastClick()) {
				DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(Set.this);
				dialog.setMessage("您确定要退出登录？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (PushManager.isPushEnabled(getApplicationContext())) {
							PushManager.stopWork(getApplicationContext());
						}
						GatherApplication application = (GatherApplication) getApplication();
						application.setUserInfoModel(null);
						AppPreference.clearInfo(Set.this);
						Intent intent = new Intent(Set.this, LoginIndex.class);
						startActivity(intent);
					}
				}).show();
			}
			break;
		}
	}

	/**
	 * 清理缓存
	 */
	private class ProgressClear extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingDialog.setMessage("正在清理缓存");
			mLoadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.clearMemoryCache();
			imageLoader.clearDiskCache();
			// File file = new File(Constant.UPLOAD_FILES_DIR_PATH);
			// if (file != null && file.exists()) {
			// File files[] = file.listFiles();
			// if (files != null) {
			// for (int i = 0; i < files.length; i++) {
			// if (files[i].isDirectory()) {
			// File childs[] = files[i].listFiles();
			// if (childs != null) {
			// for (int j = 0; j < childs.length; j++) {
			// childs[j].delete();
			// }
			// }
			// }
			// files[i].delete();
			// }
			// return true;
			// }
			// } else {
			// if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			// mLoadingDialog.dismiss();
			// }
			// toast("缓存文件不存在");
			// return false;
			// }
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
			if (result) {
				toast("清理完成");
				setCacheSize();
			} else {
				toast("清理缓存失败，请重试");
			}
		}
	}

	/**
	 * 计算缓存大小
	 */
	private void setCacheSize() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = new File(Constant.IMAGE_CACHE_DIR_PATH);
				try {
					String size = FormetFileSize(getFileSizes(file));
					Message msg = new Message();
					msg.obj = size;
					msg.what = 3;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	/**
	 * 获取指定文件大小
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
			Log.e("获取文件大小", "文件不存在!");
		}
		return size;
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 绑定新浪微博
	 */
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			AppPreference.save(Set.this, AppPreference.SINA_ID, mAccessToken.getUid());
			AppPreference.save(Set.this, AppPreference.SINA_TOKEN, mAccessToken.getToken());
			AppPreference.save(Set.this, AppPreference.SINA_EXPIRES, mAccessToken.getExpiresTime());
			ShareToSina();
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onWeiboException(WeiboException e) {

		}
	}

	/**
	 * 分享到微信好友
	 */
	private void ShareToWeChat() {
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID);
		wxApi.registerApp(Constant.WE_CHAT_APPID);
		if (wxApi.isWXAppInstalled() && wxApi.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = Constant.OFFICIAL_WEB;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = Share_Title;
			msg.description = Share_Message;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.setThumbImage(thumb);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			wxApi.sendReq(req);
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本微信客户端").withEffect(Effectstype.Shake).show();
			}
		}
	}

	/**
	 * 分享到微信朋友圈
	 */
	private void ShareToSquare() {
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID);
		wxApi.registerApp(Constant.WE_CHAT_APPID);
		if (wxApi.isWXAppInstalled() && wxApi.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = Constant.OFFICIAL_WEB;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = Share_Title;
			msg.description = Share_Message;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.setThumbImage(thumb);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			wxApi.sendReq(req);
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本微信客户端").withEffect(Effectstype.Shake).show();
			}
		}
	}

	/**
	 * 分享到QQ空间
	 */
	private void ShareToZone() {
		if (checkQQPackage()) {
			mLoadingDialog.setMessage("分享到QQ空间...");
			mLoadingDialog.show();
		}
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, Share_Message);// 必填
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, Constant.OFFICIAL_WEB);// 必填
		// params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, Share_Title);// 选填
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(Constant.SHARE_QQZONE_IMAGE_URL);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		new Thread(new Runnable() {
			@Override
			public void run() {
				mTencent.shareToQzone(Set.this, params, new IUiListener() {
					@Override
					public void onError(UiError arg0) {
						if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
							mLoadingDialog.dismiss();
						}
						if (mFile != null && mFile.exists()) {
							mFile.delete();
						}
						handler.sendEmptyMessage(0);
					}

					@Override
					public void onComplete(Object arg0) {
						if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
							mLoadingDialog.dismiss();
						}
						if (mFile != null && mFile.exists()) {
							mFile.delete();
						}
						handler.sendEmptyMessage(1);
					}

					@Override
					public void onCancel() {
						if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
							mLoadingDialog.dismiss();
						}
						if (mFile != null && mFile.exists()) {
							mFile.delete();
						}
					}
				});
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				toast("分享失败，请重试");
				break;
			case 1:
				toast("分享成功");
				break;
			case 3:
				tvCacheSize.setText((String) msg.obj);
				break;
			}
		}
	};

	/**
	 * 分享到新浪微博
	 */
	private void ShareToSina() {
		mLoadingDialog.setMessage("分享到新浪微博...");
		mLoadingDialog.show();
		mFile = getImageTempFile();
		ShareToSinaParam param = null;
		if (BitmapUtils.saveBitmapToImageFile(Set.this, Bitmap.CompressFormat.JPEG, convertDrawable2BitmapSimple(getResources().getDrawable(R.drawable.ic_launcher)), mFile, 100)) {
			param = new ShareToSinaParam(Set.this, AppPreference.getUserPersistent(Set.this, AppPreference.SINA_TOKEN), Share_Message + "下载链接：" + Constant.OFFICIAL_WEB, mFile);
			MultipartRequest task = new MultipartRequest(Set.this, param.getUrl(), new ResponseListener() {

				@Override
				public void success(int code, String msg, String result) {
					if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
						mLoadingDialog.dismiss();
					}
				}

				@Override
				public void relogin(String msg) {
					if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
						mLoadingDialog.dismiss();
					}
				}

				@Override
				public void error(int code, String msg) {
					if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
						mLoadingDialog.dismiss();
					}
					toast("分享成功");
					if (mFile != null && mFile.exists()) {
						mFile.delete();
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
						mLoadingDialog.dismiss();
					}
					toast(error.getMessage());
				}
			}, param.getParameters());
			executeRequest(task);
		} else {
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
			toast("分享失败，请重试");
		}
	}

	public static Bitmap convertDrawable2BitmapSimple(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	private File getImageTempFile() {
		File file = new File(Constant.UPLOAD_FILES_DIR_PATH + "SHARE" + ".jpg");
		return file;
	}

	/**
	 * 检测新浪微博是否安装
	 * 
	 */
	private boolean checkSinaPackage() {
		PackageManager manager = getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.sina.weibo")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测QQ是否安装
	 * 
	 */
	private boolean checkQQPackage() {
		PackageManager manager = getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.tencent.mobileqq")) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

}
