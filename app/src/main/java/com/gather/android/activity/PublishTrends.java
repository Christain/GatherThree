package com.gather.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.PickedImagesAdapter;
import com.gather.android.adapter.PickedImagesAdapter.OnPickedItemClickListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.database.PublishTrendsInfo;
import com.gather.android.database.PublishTrendsService;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.PickedImageModel;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 发布动态
 */
public class PublishTrends extends SwipeBackActivity implements OnClickListener {

	private TextView tvLeft, tvRight, tvTitle;
	private ImageView ivLeft, ivRight;

	private EditText etContent;
	private TextView tvTextNum;
	private int MAX_NUM = 240;
	private NoScrollGridView gridView;
	private PickedImagesAdapter adapter;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private ScrollView scrollView;
	private GatherApplication application;

	/**
	 * 多图
	 */
	private int actionPosition;
	private File mPicFile;
	private static final int REQEUST_CODE_MULTIPICS_ALBUM = 103;
	private boolean isShowDialog = false;

	/**
	 * 数据库
	 */
	private PublishTrendsService service;

	@Override
	protected int layoutResId() {
		return R.layout.publish_trends;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(PublishTrends.this, true);
		this.dialog = DialogTipsBuilder.getInstance(PublishTrends.this);
		this.service = new PublishTrendsService(PublishTrends.this);
		this.application = (GatherApplication) getApplication();
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.VISIBLE);
		this.tvTitle.setText("动态发布");
		this.tvRight.setText("确认");
		this.ivLeft.setVisibility(View.VISIBLE);
		this.ivRight.setVisibility(View.GONE);
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);

		this.scrollView = (ScrollView) findViewById(R.id.ScrollView);
		if (Build.VERSION.SDK_INT >= 9) {
			scrollView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
		}
		this.etContent = (EditText) findViewById(R.id.etContent);
		this.etContent.addTextChangedListener(mTextWatcher);
		this.tvTextNum = (TextView) findViewById(R.id.tvTextNum);
		this.tvTextNum.setText(MAX_NUM + "/" + MAX_NUM);

		this.gridView = (NoScrollGridView) findViewById(R.id.gridView);
		if (Build.VERSION.SDK_INT >= 9) {
			gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		this.adapter = new PickedImagesAdapter(PublishTrends.this, 9);
		this.gridView.setAdapter(adapter);
		this.adapter.setListener(mPickedItemClickListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (!TextUtils.isEmpty(etContent.getText().toString().trim()) || adapter.getList().size() > 0) {
					DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(PublishTrends.this);
					dialog.setMessage("还未发布动态，是否确定退出？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (adapter.getList().size() > 0) {
								for (int i = 0; i < adapter.getList().size(); i++) {
									File file = new File(adapter.getList().get(i).getPath());
									if (file != null && file.exists()) {
										file.delete();
										file = null;
									}
								}
							}
							finish();
						}
					}).show();
				} else {
					finish();
				}
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入动态内容").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (application.getNetWorkStatus()) {
					if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
						mLoadingDialog.setMessage("正在处理数据中...");
						mLoadingDialog.show();
					}
					StringBuffer sb = new StringBuffer();
					sb.append("");
					int size = adapter.getList().size();
					if (size != 0) {
						for (int i = 0; i < size; i++) {
							sb.append(adapter.getList().get(i).getPath());
							if (i != size - 1) {
								sb.append(",");
							}
						}
					}
					PublishTrendsInfo info = new PublishTrendsInfo(etContent.getText().toString().trim(), sb.toString(), System.currentTimeMillis(), AppPreference.getUserPersistentInt(PublishTrends.this, AppPreference.USER_ID));
					if (service != null && service.insertTrends(info)) {
						if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
							mLoadingDialog.dismiss();
						}
						setResult(RESULT_OK);
						finish();
					} else {
						if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
							mLoadingDialog.dismiss();
						}
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("数据处理失败，请重新发送").withEffect(Effectstype.Shake).show();
						}
					}
				} else {
					NetWorkDialog();
				}
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!TextUtils.isEmpty(etContent.getText().toString().trim()) || adapter.getList().size() > 0) {
				DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(PublishTrends.this);
				dialog.setMessage("还未发布动态，是否确定退出？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (adapter.getList().size() > 0) {
							for (int i = 0; i < adapter.getList().size(); i++) {
								File file = new File(adapter.getList().get(i).getPath());
								if (file != null && file.exists()) {
									file.delete();
									file = null;
								}
							}
						}
						finish();
					}
				}).show();
			} else {
				finish();
			}
		}
		return true;
	}

	private OnPickedItemClickListener mPickedItemClickListener = new OnPickedItemClickListener() {

		@Override
		public void onImageItemClicked(int position) {
			if (!isShowDialog) {
				isShowDialog = true;
				actionPosition = position;
				showMenuDialog(0);
			}
		}

		@Override
		public void addImage() {
			if (!isShowDialog) {
				isShowDialog = true;
				showMenuDialog(1);
			}
		}
	};

	private void showMenuDialog(int type) {
		switch (type) {
		case 0:
			MMAlert.showAlert(PublishTrends.this, "图片操作", new String[] { "查看图片", "删除图片" }, null, new MMAlert.OnAlertSelectId() {
				public void onDismissed() {
					isShowDialog = false;
				}

				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						isShowDialog = false;
						ArrayList<String> imgList = new ArrayList<String>();
						for (int i = 0; i < adapter.getList().size(); i++) {
							imgList.add(adapter.getList().get(i).getPath());
						}
						Intent intent = new Intent(PublishTrends.this, PublishTrendsPicGallery.class);
						intent.putExtra("LIST", imgList);
						intent.putExtra("POSITION", actionPosition);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					case 1:
						isShowDialog = false;
						File file = new File(adapter.getList().get(actionPosition).getPath());
						if (file != null && file.exists()) {
							file.delete();
						}
						adapter.del(actionPosition);
						break;
					}
				}
			});
			break;
		case 1:
			ChoosePicAlert.showAlert(PublishTrends.this, "选择图片", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
				public void onDismissed() {
					isShowDialog = false;
				}

				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						isShowDialog = false;
						mPicFile = getImageTempFile();
						Intent intent = IntentManage.getSystemCameraIntent(mPicFile);
						startActivityForResult(intent, IntentManage.REQUEST_CODE_TAKE_PHOTO);
						break;
					case 1:
						isShowDialog = false;
						Intent intent2 = new Intent(PublishTrends.this, SelectPicture.class);
						intent2.putExtra(SelectPicture.MAX_PICS_NUM, (SelectPicture.MAX_NUM - adapter.getImagesNum()));
						startActivityForResult(intent2, REQEUST_CODE_MULTIPICS_ALBUM);
						break;
					}
				}
			});
			break;
		}
	}

	private File getImageTempFile() {
		File file = new File(Constant.UPLOAD_FILES_DIR_PATH + System.currentTimeMillis() + ".jpg");
		return file;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IntentManage.REQUEST_CODE_TAKE_PHOTO:// 拍照
				if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
					mLoadingDialog.setMessage("正在处理");
					mLoadingDialog.show();
				}
				new CameraImageProgress().execute();
				break;
			case REQEUST_CODE_MULTIPICS_ALBUM:
				mLoadingDialog.setMessage("正在处理");
				mLoadingDialog.show();
				String picsPath = data.getStringExtra("chosedPic");
				new MultiImagesProgress().execute(picsPath.split(","));
				break;
			}
		}
	}

	/**
	 * 处理拍照返回图片
	 */
	private class CameraImageProgress extends AsyncTask<Uri, Void, File> {

		@Override
		protected File doInBackground(Uri... params) {
			File file = null;
			try {
				Bitmap bmp = BitmapUtils.getBitmapFromFile(mPicFile, -1);
				if (bmp != null) {
					file = getImageTempFile();
					FileOutputStream outputStream = new FileOutputStream(file);
					int size = (int) bmp.getRowBytes() * bmp.getHeight() / (1024 * 8);
					boolean b = false;
					if (size > 120) {
						b = bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
					} else {
						b = bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
					}
					outputStream.flush();
					outputStream.close();
					if (!b && file != null && file.exists()) {
						file.delete();
						file = null;
					}
					bmp.recycle();
					bmp = null;
				}
				if (mPicFile != null && mPicFile.exists()) {
					mPicFile.delete();
				}
				mPicFile = null;
			} catch (Exception e) {
				e.printStackTrace();
				if (file != null && file.exists()) {
					file.delete();
				}
				file = null;
			}
			return file;
		}

		@Override
		protected void onPostExecute(File result) {
			if (result != null) {
				PickedImageModel model = new PickedImageModel(result, result.getAbsolutePath());
				adapter.add(model);
			} else {
				toast("图片处理出错啦！");
			}
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
		}
	}

	/**
	 * 相册图片返回处理
	 */
	private class MultiImagesProgress extends AsyncTask<String, Void, List<PickedImageModel>> {

		@Override
		protected List<PickedImageModel> doInBackground(String... params) {
			ArrayList<PickedImageModel> list = new ArrayList<PickedImageModel>();
			File file = null;
			for (int i = 0; i < params.length; i++) {
				try {
					file = new File(params[i]);
					Bitmap bmp = BitmapUtils.getBitmapFromFile(file, -1);
					if (bmp != null) {
						file = getImageTempFile();
						FileOutputStream outputStream = new FileOutputStream(file);
						int size = (int) bmp.getRowBytes() * bmp.getHeight() / (1024 * 8);
						boolean b = false;
						if (size > 120) {
							b = bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
						} else {
							b = bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
						}
						outputStream.flush();
						outputStream.close();
						if (!b && file != null && file.exists()) {
							file.delete();
							file = null;
						} else {
							PickedImageModel model = new PickedImageModel(file, file.getAbsolutePath());
							list.add(model);
						}
						bmp.recycle();
						bmp = null;
					}
					file = null;
				} catch (Exception e) {
					e.printStackTrace();
					if (file != null && file.exists()) {
						file.delete();
					}
				}

			}
			return list;
		}

		@Override
		protected void onPostExecute(List<PickedImageModel> result) {
			adapter.add(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
		}
	}

	/**
	 * 字数监听
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			int num = MAX_NUM - s.length();
			tvTextNum.setText(num + "/" + MAX_NUM);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}
	};

}
