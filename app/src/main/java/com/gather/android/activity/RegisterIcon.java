package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.RegisterDataModel;
import com.gather.android.params.RegisterUploadPhotoParam;
import com.gather.android.params.RegisterUploadUserInfoParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.swipeback.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

@SuppressLint("InflateParams")
public class RegisterIcon extends SwipeBackActivity implements OnClickListener {

	private ImageView ivPhoto;
	private TextView tvNext, tvTips;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private RegisterDataModel model;
	private int type;

	private File mIconFile = null;
	private Bitmap mIconBmp = null;
	private Uri imgUri;
	private boolean hasIcon = false;

	@Override
	protected int layoutResId() {
		return R.layout.register_icon;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("MODEL") && intent.hasExtra("TYPE")) {
			model = (RegisterDataModel) intent.getSerializableExtra("MODEL");
			type = intent.getExtras().getInt("TYPE");

			this.mLoadingDialog = LoadingDialog.createDialog(RegisterIcon.this, true);
			this.tvNext = (TextView) findViewById(R.id.tvNext);
			this.ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
			this.tvTips = (TextView) findViewById(R.id.tvTips);

			this.ivPhoto.setOnClickListener(this);
			this.tvNext.setOnClickListener(this);

			this.init();
		}
	}

	private void init() {
		if (model != null && model.getPhotoPath().length() > 2) {
			hasIcon = true;
			mIconFile = new File(model.getPhotoPath());
			ivPhoto.setImageBitmap(BitmapUtils.getRoundBitmap(BitmapUtils.getBitmapFromFile(mIconFile, -1)));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvNext:
			if (!ClickUtil.isFastClick()) {
				if (hasIcon) {
					UploadPhoto();
				} else {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请上传头像").withEffect(Effectstype.Shake).show();
					}
				}
			}
			break;
		case R.id.ivPhoto:
			ChoosePicAlert.showAlert(RegisterIcon.this, "选择头像", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
				public void onDismissed() {
				}

				public void onClick(int whichButton) {
					Intent intent;
					switch (whichButton) {
					case 0:// 拍照
						mIconFile = getImageTempFile();
						intent = IntentManage.getSystemCameraIntent(mIconFile);
						startActivityForResult(intent, IntentManage.REQUEST_CODE_TAKE_PHOTO);
						break;
					case 1:// 相册
						mIconFile = getImageTempFile();
						intent = new Intent(Intent.ACTION_PICK);
						intent.setType("image/*");
						startActivityForResult(intent, IntentManage.REQUEST_CODE_ALBUM);
						break;
					}
				}
			});
			break;
		}
	}

	/**
	 * 上传头像
	 */
	private void UploadPhoto() {
		mLoadingDialog.setMessage("正在提交...");
		mLoadingDialog.show();
		RegisterUploadPhotoParam param = new RegisterUploadPhotoParam(mIconFile);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    UploadUserInfo(object.getInt("img_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	/**
	 * 完善资料（上传头像成功后，根据返回的头像ID，提交完善的资料）
	 * 
	 * @param id
	 */
	private void UploadUserInfo(int id) {
		RegisterUploadUserInfoParam param = new RegisterUploadUserInfoParam(model.getPassword(), model.getNickname(), model.getSex(), model.getBirthday(), model.getAddress(), model.getEmail(), id);
		AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(RegisterIcon.this, "api_key"));
                AppPreference.save(RegisterIcon.this, AppPreference.LOGIN_TYPE, type);
                AppPreference.save(RegisterIcon.this, AppPreference.IS_REGISTER, 1);
                Intent intent = new Intent(RegisterIcon.this, IndexHome.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                AppPreference.save(RegisterIcon.this, AppPreference.IS_REGISTER, 0);
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IntentManage.REQUEST_CODE_TAKE_PHOTO:// 拍照
				imgUri = Uri.fromFile(mIconFile);
				tailor(imgUri);
				break;
			case IntentManage.REQUEST_CODE_ALBUM:// 相册
				imgUri = Uri.fromFile(mIconFile);
				tailor(data.getData());
				break;
			case IntentManage.REQUEST_CODE_CROP_IMAGE:// 裁剪图片
				if (imgUri != null) {
					recycleBmp();
					mIconBmp = BitmapUtils.getBitmapFromUri(RegisterIcon.this, imgUri);
					new ProgressSaveImage().execute();
				} else {
					toast("图片截取失败，请重试");
				}
				break;
			}
		}
	}

	private void tailor(Uri uri) { // 截图
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			String url = IntentManage.getPath(RegisterIcon.this, uri);
			intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 500);
		intent.putExtra("outputY", 500);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mIconFile));
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, IntentManage.REQUEST_CODE_CROP_IMAGE);
	}

	private File getImageTempFile() {
		File file = new File(Constant.UPLOAD_FILES_DIR_PATH + System.currentTimeMillis() + ".jpg");
		return file;
	}

	private void recycleBmp() {
		ivPhoto.setImageResource(R.drawable.default_user_icon);
		if (mIconBmp != null && !mIconBmp.isRecycled()) {
			mIconBmp.recycle();
			System.gc();
		}
		mIconBmp = null;
	}

	private class ProgressSaveImage extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingDialog.setMessage("正在处理头像");
			mLoadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (mIconBmp != null && !mIconBmp.isRecycled()) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
			if (result) {
				ivPhoto.setImageBitmap(BitmapUtils.getRoundBitmap(mIconBmp));
				tvTips.setText("重新上传");
				hasIcon = true;
				imgUri = null;
			} else {
				Toast.makeText(getApplicationContext(), "头像处理失败，请重试", Toast.LENGTH_LONG).show();
				recycleBmp();
				hasIcon = false;
				mIconFile = null;
				imgUri = null;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasIcon) {
				model.setPhotoPath(mIconFile.getPath());
				Intent intent = new Intent();
				intent.putExtra("MODEL", model);
				setResult(RESULT_OK);
				finishActivity();
			} else {
				onBackPressed();
			}
		}
		return true;
	}

}
