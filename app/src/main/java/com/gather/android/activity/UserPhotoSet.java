package com.gather.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.UserPhotoSetAdapter;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.MultipartRequest;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.params.UpdateUserPhotoParam;
import com.gather.android.params.UploadPicParam;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.DragGridView;
import com.gather.android.widget.DragGridView.OnChanageListener;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 个人相册设置
 */
public class UserPhotoSet extends SwipeBackActivity implements OnClickListener {

	private TextView tvLeft, tvTitle, tvRight;
	private ImageView ivLeft, ivRight;

	private DragGridView gridView;
	private UserPhotoSetAdapter adapter;
	private ArrayList<UserPhotoModel> picList;
	private LoadingDialog mLoadingDialog;
	private ArrayList<Integer> imgIdList;
	private int startIndex = 0;

	/**
	 * 多图
	 */
	private File mPicFile;
	private static final int REQEUST_CODE_MULTIPICS_ALBUM = 103;
	private boolean isShowDialog = false, isPicChanged = false;

	@Override
	protected int layoutResId() {
		return R.layout.user_photo_set;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("LIST")) {
			picList = (ArrayList<UserPhotoModel>) intent.getSerializableExtra("LIST");
			this.mLoadingDialog = LoadingDialog.createDialog(UserPhotoSet.this, false);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.VISIBLE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.tvTitle.setText("我的相册");
			this.tvRight.setText("完成");
			this.ivRight.setVisibility(View.GONE);
			this.ivLeft.setOnClickListener(this);
			this.tvRight.setOnClickListener(this);

			this.gridView = (DragGridView) findViewById(R.id.gridView);
			this.adapter = new UserPhotoSetAdapter(UserPhotoSet.this);
			this.gridView.setAdapter(adapter);

			this.gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					if (!adapter.isFull() && position == adapter.getCount() - 1) {
						if (!isShowDialog) {
							isShowDialog = true;
							showMenuDialog(1, position);
						}
					} else {
						if (!isShowDialog) {
							isShowDialog = true;
							showMenuDialog(0, position);
						}
					}
				}
			});
			this.gridView.setOnChangeListener(new OnChanageListener() {
				@Override
				public void onChange(int from, int to) {
					UserPhotoModel temp = picList.get(from);
					// 直接交互item
					if (from < to) {
						for (int i = from; i < to; i++) {
							Collections.swap(picList, i, i + 1);
						}
					} else if (from > to) {
						for (int i = from; i > to; i--) {
							Collections.swap(picList, i, i - 1);
						}
					}
					picList.set(to, temp);
					isPicChanged = true;
					adapter.notifyDataSetChanged();
				}
			});

			this.initView();
		} else {
			toast("相册数据有误...");
			finish();
		}
	}

	private void initView() {
		if (imgIdList == null) {
			imgIdList = new ArrayList<Integer>();
		}
		startIndex = 0;
		if (picList != null) {
			adapter.setUserPhotoList(picList);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (!isPicChanged) {
					toast("相册未发生任何修改");
					return;
				}
				if (startIndex == 0) {
					if (picList.size() != 0) {
						for (int i = 0; i < picList.size(); i++) {
							if (picList.get(i).getId() == 0) {
								uploadNewPic(i);
								break;
							} else {
								imgIdList.add(picList.get(i).getId());
								if (i == picList.size() - 1) {
									UpdateUserPhoto();
								}
							}
						}
					} else {
						UpdateUserPhoto();
					}
				} else if (startIndex == 100) {
					UpdateUserPhoto();
				} else {
					for (int i = startIndex; i < picList.size(); i++) {
						if (picList.get(i).getId() == 0) {
							uploadNewPic(i);
							break;
						} else {
							imgIdList.add(picList.get(i).getId());
							if (i == picList.size() - 1) {
								UpdateUserPhoto();
							}
						}
					}
				}
			}
			break;
		}
	}
	
	/**
	 * 上传新增图片
	 */
	private void uploadNewPic(final int index) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("上传中...");
			mLoadingDialog.show();
		}
		UploadPicParam param = new UploadPicParam(UserPhotoSet.this, new File(picList.get(index).getPath()));
		MultipartRequest task = new MultipartRequest(UserPhotoSet.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					if (imgIdList == null) {
						imgIdList = new ArrayList<Integer>();
					}
					imgIdList.add(object.getInt("img_id"));
					if (index + 1 < picList.size()) {
						for (int i = index + 1; i < picList.size(); i++) {
							if (picList.get(i).getId() == 0) {
								uploadNewPic(i);
								break;
							}  else {
								imgIdList.add(picList.get(i).getId());
								if (i == picList.size() - 1) {
									UpdateUserPhoto();
								}
							}
						}
					} else {
						UpdateUserPhoto();
					}
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
				startIndex = index;
				toast("上传图片失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				startIndex = index;
				toast("上传图片失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}
	
	/**
	 * 更新相册图片Id
	 */
	private void UpdateUserPhoto() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("更新中...");
			mLoadingDialog.show();
		}
		UpdateUserPhotoParam param = new UpdateUserPhotoParam(UserPhotoSet.this, imgIdList);
		HttpStringPost task = new HttpStringPost(UserPhotoSet.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				setResult(RESULT_OK);
				toast("更新相册成功");
				finish();
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
				startIndex = 100;
				toast("更新相册失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				startIndex = 100;
				toast("更新相册失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	private void showMenuDialog(int type, final int actionPosition) {
		switch (type) {
		case 0:
			MMAlert.showAlert(UserPhotoSet.this, "图片操作", new String[] { "查看图片", "删除图片" }, null, new MMAlert.OnAlertSelectId() {
				public void onDismissed() {
					isShowDialog = false;
				}

				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						isShowDialog = false;
						ArrayList<String> imgList = new ArrayList<String>();
						for (int i = 0; i < adapter.getPicList().size(); i++) {
							if (adapter.getPicList().get(i).getId() == 0) {
								imgList.add(adapter.getPicList().get(i).getPath());
							} else {
								imgList.add(adapter.getPicList().get(i).getImg_url());
							}
						}
						Intent intent = new Intent(UserPhotoSet.this, PublishTrendsPicGallery.class);
						intent.putExtra("LIST", imgList);
						intent.putExtra("POSITION", actionPosition);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					case 1:
						isShowDialog = false;
						if (adapter.getPicList().get(actionPosition).getId() == 0) {
							File file = new File(adapter.getPicList().get(actionPosition).getPath());
							if (file != null && file.exists()) {
								file.delete();
							}
						}
						isPicChanged = true;
						adapter.del(actionPosition);
						break;
					}
				}
			});
			break;
		case 1:
			ChoosePicAlert.showAlert(UserPhotoSet.this, "选择图片", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
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
						Intent intent2 = new Intent(UserPhotoSet.this, SelectPicture.class);
						intent2.putExtra(SelectPicture.MAX_PICS_NUM, (SelectPicture.MAX_NUM - adapter.getPicList().size()));
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
				if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
					mLoadingDialog.setMessage("正在处理");
					mLoadingDialog.show();
				}
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
				UserPhotoModel model = new UserPhotoModel(result, result.getAbsolutePath());
				isPicChanged = true;
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
	private class MultiImagesProgress extends AsyncTask<String, Void, List<UserPhotoModel>> {

		@Override
		protected List<UserPhotoModel> doInBackground(String... params) {
			ArrayList<UserPhotoModel> list = new ArrayList<UserPhotoModel>();
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
							UserPhotoModel model = new UserPhotoModel(file, file.getAbsolutePath());
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
		protected void onPostExecute(List<UserPhotoModel> result) {
			isPicChanged = true;
			adapter.add(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
		}
	}
}
