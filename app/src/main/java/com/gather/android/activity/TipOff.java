package com.gather.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.PickedImagesAdapter;
import com.gather.android.adapter.PickedImagesAdapter.OnPickedItemClickListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.PickedImageModel;
import com.gather.android.params.TipOffDataParam;
import com.gather.android.params.UploadPicParam;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.MobileUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.swipeback.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 爆料
 */
public class TipOff extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private EditText etPhone, etAddress, etOther;
	private NoScrollGridView picGridView;
	private PickedImagesAdapter picAdapter;
	private ArrayList<Integer> imgIdsList;

	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;

	/**
	 * 多图
	 */
	private int actionPosition;
	private File mPicFile;
	private static final int REQEUST_CODE_MULTIPICS_ALBUM = 103;
	private boolean isShowDialog = false;

	@Override
	protected int layoutResId() {
		return R.layout.tip_off;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(TipOff.this, true);
		this.dialog = DialogTipsBuilder.getInstance(TipOff.this);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.VISIBLE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("爆料");
		this.tvRight.setText("提交");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);

		this.etPhone = (EditText) findViewById(R.id.etPhone);
		this.etAddress = (EditText) findViewById(R.id.etAddress);
		this.etOther = (EditText) findViewById(R.id.etOther);
		this.picGridView = (NoScrollGridView) findViewById(R.id.gridView);
		this.picAdapter = new PickedImagesAdapter(TipOff.this, 3);
		this.picGridView.setAdapter(picAdapter);
		this.picAdapter.setListener(mPickedItemClickListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (!TextUtils.isEmpty(etPhone.getText().toString().trim()) || !TextUtils.isEmpty(etAddress.getText().toString().trim()) || !TextUtils.isEmpty(etOther.getText().toString().trim()) || picAdapter.getList().size() > 0) {
					DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(TipOff.this);
					dialog.setMessage("还未提交爆料，是否确定退出？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (picAdapter.getList().size() > 0) {
								for (int i = 0; i < picAdapter.getList().size(); i++) {
									File file = new File(picAdapter.getList().get(i).getPath());
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
				if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("联系电话不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (MobileUtil.execute(etPhone.getText().toString().trim().replace(" ", "")).equals("未知")) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("详细地址不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (picAdapter.getList().size() == 0) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请至少上传一张活动图片").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				imgIdsList = null;
				imgIdsList = new ArrayList<Integer>();
				UploadPic(picAdapter.getList().get(0).getImageFile(), 0);
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!ClickUtil.isFastClick()) {
				if (!TextUtils.isEmpty(etPhone.getText().toString().trim()) || !TextUtils.isEmpty(etAddress.getText().toString().trim()) || !TextUtils.isEmpty(etOther.getText().toString().trim()) || picAdapter.getList().size() > 0) {
					DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(TipOff.this);
					dialog.setMessage("还未提交爆料，是否确定退出？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (picAdapter.getList().size() > 0) {
								for (int i = 0; i < picAdapter.getList().size(); i++) {
									File file = new File(picAdapter.getList().get(i).getPath());
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
		}
		return true;
	}

	/**
	 * 上传活动线索图片
	 */
	private void UploadPic(File file, final int index) {
		if (index == 0) {
			mLoadingDialog.setMessage("正在提交...");
			mLoadingDialog.show();
		}
		UploadPicParam param = new UploadPicParam(file);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    imgIdsList.add(object.getInt("img_id"));
                    if (index < picAdapter.getList().size() - 1) {
                        UploadPic(picAdapter.getList().get(index + 1).getImageFile(), index + 1);
                    } else {
                        UploadActData();
                    }
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
                    dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	/**
	 * 上传爆料线索
	 */
	private void UploadActData() {
		String other = "";
		if (etOther.getText().toString().trim().length() > 0) {
			other = etOther.getText().toString().trim();
		}
		GatherApplication application = (GatherApplication) getApplication();
		TipOffDataParam param = new TipOffDataParam(application.getCityId(), imgIdsList, etPhone.getText().toString().trim(), etAddress.getText().toString().trim(), other);
		AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                setResult(RESULT_OK);
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
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
                }
            }
        });
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
			MMAlert.showAlert(TipOff.this, "图片操作", new String[] { "查看图片", "删除图片" }, null, new MMAlert.OnAlertSelectId() {
				public void onDismissed() {
					isShowDialog = false;
				}

				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						isShowDialog = false;
						ArrayList<String> imgList = new ArrayList<String>();
						for (int i = 0; i < picAdapter.getList().size(); i++) {
							imgList.add(picAdapter.getList().get(i).getPath());
						}
						Intent intent = new Intent(TipOff.this, PublishTrendsPicGallery.class);
						intent.putExtra("LIST", imgList);
						intent.putExtra("POSITION", actionPosition);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					case 1:
						isShowDialog = false;
						File file = new File(picAdapter.getList().get(actionPosition).getPath());
						if (file != null && file.exists()) {
							file.delete();
						}
						picAdapter.del(actionPosition);
						break;
					}
				}
			});
			break;
		case 1:
			ChoosePicAlert.showAlert(TipOff.this, "选择图片", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
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
						Intent intent2 = new Intent(TipOff.this, SelectPicture.class);
						intent2.putExtra(SelectPicture.MAX_PICS_NUM, (3 - picAdapter.getImagesNum()));
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
				picAdapter.add(model);
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
			picAdapter.add(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
		}
	}

}
