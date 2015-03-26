package com.gather.android.activity;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.MultipartRequest;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.IntentManage;
import com.gather.android.params.FeedbackParam;
import com.gather.android.params.UploadPicParam;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 问题反馈
 */
public class Feedback extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private EditText etContent;

	private ImageView ivAddPic, ivPic;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	private File mIconFile = null;
	private Bitmap mIconBmp = null;
	private Uri imgUri;
	private ArrayList<Integer> imgIds = new ArrayList<Integer>();
	private boolean hasIcon = false;

	@Override
	protected int layoutResId() {
		return R.layout.feed_back;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(Feedback.this, false);
		this.dialog = DialogTipsBuilder.getInstance(Feedback.this);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.VISIBLE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvRight.setText("确认");
		this.tvTitle.setText("问题反馈");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);

		this.etContent = (EditText) findViewById(R.id.etContent);

		this.ivAddPic = (ImageView) findViewById(R.id.ivAddPic);
		this.ivPic = (ImageView) findViewById(R.id.ivPic);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		LayoutParams params = (LayoutParams) ivPic.getLayoutParams();
		params.width = metrics.widthPixels / 2;
		params.height = params.width;
		this.ivPic.setLayoutParams(params);

		this.ivAddPic.setVisibility(View.VISIBLE);
		this.ivPic.setVisibility(View.GONE);

		this.ivAddPic.setOnClickListener(this);
		this.ivPic.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (mIconFile != null && mIconFile.exists()) {
					mIconFile.delete();
				}
				finish();
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入您的反馈意见").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (hasIcon) {
					UploadPic();
				} else {
					UploadFeedback();
				}
			}
			break;
		case R.id.ivAddPic:
		case R.id.ivPic:
			if (!ClickUtil.isFastClick()) {
				ChoosePicAlert.showAlert(Feedback.this, "图片", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
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
			}
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!ClickUtil.isFastClick()) {
				if (mIconFile != null && mIconFile.exists()) {
					mIconFile.delete();
				}
				finish();
			}
		}
		return true;
	}

	/**
	 * 上传图片
	 */
	private void UploadPic() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("提交中...");
			mLoadingDialog.show();
		}
		UploadPicParam param = new UploadPicParam(Feedback.this, mIconFile);
		MultipartRequest task = new MultipartRequest(Feedback.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					imgIds.add(object.getInt("img_id"));
					UploadFeedback();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}
	
	/**
	 * 意见反馈
	 */
	private void UploadFeedback() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("提交中...");
			mLoadingDialog.show();
		}
		FeedbackParam param = new FeedbackParam(Feedback.this, etContent.getText().toString().trim(), imgIds);
		HttpStringPost task = new HttpStringPost(Feedback.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("反馈成功，谢谢您的宝贵意见");
				finish();
			}
			
			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
			
			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
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
					mIconBmp = BitmapUtils.getBitmapFromUri(Feedback.this, imgUri);
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
			String url = IntentManage.getPath(Feedback.this, uri);
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
		ivAddPic.setVisibility(View.VISIBLE);
		ivPic.setVisibility(View.GONE);
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
			mLoadingDialog.setMessage("正在处理图片");
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
				if (ivAddPic.isShown()) {
					ivAddPic.setVisibility(View.GONE);
				}
				if (!ivPic.isShown()) {
					ivPic.setVisibility(View.VISIBLE);
				}
				ivPic.setImageBitmap(mIconBmp);
				hasIcon = true;
				imgUri = null;
			} else {
				Toast.makeText(getApplicationContext(), "图片处理失败，请重试", Toast.LENGTH_LONG).show();
				recycleBmp();
				hasIcon = false;
				mIconFile = null;
				imgUri = null;
			}
		}
	}

}
