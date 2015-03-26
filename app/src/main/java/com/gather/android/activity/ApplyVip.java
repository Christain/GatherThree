package com.gather.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.ApplyVipCityAdapter;
import com.gather.android.adapter.ApplyVipInterestAdapter;
import com.gather.android.adapter.PickedImagesAdapter;
import com.gather.android.adapter.PickedImagesAdapter.OnPickedItemClickListener;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.MultipartRequest;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.CityList;
import com.gather.android.model.CityListModel;
import com.gather.android.model.PickedImageModel;
import com.gather.android.model.UserInterestList;
import com.gather.android.model.UserInterestModel;
import com.gather.android.params.ApplyVipParam;
import com.gather.android.params.GetCityListParam;
import com.gather.android.params.GetUserTagsParam;
import com.gather.android.params.UploadPicParam;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.MobileUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

/**
 * 申请成为达人
 */
public class ApplyVip extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private EditText etUserName, etUserPhone, etUserEmail, etUserBrief;
	private NoScrollGridView actGridView, userGridView, cityGridView, userIconGridView;;
	private ApplyVipInterestAdapter actAdapter, userAdapter;
	private PickedImagesAdapter userIconAdapter;
	private ApplyVipCityAdapter cityAdapter;
	private ArrayList<UserInterestModel> actList, userList;
	private ArrayList<CityListModel> cityList;
	private ArrayList<Integer> imgList;
	private ScrollView scrollView;

	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;
	private int actIndex, userIndex, cityIndex;

	/**
	 * 多图
	 */
	private int actionPosition;
	private File mPicFile;
	private static final int REQEUST_CODE_MULTIPICS_ALBUM = 103;
	private boolean isShowDialog = false;

	@Override
	protected int layoutResId() {
		return R.layout.apply_vip;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.cityList = new ArrayList<CityListModel>();
		this.imgList = new ArrayList<Integer>();
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.VISIBLE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("申请达人");
		this.tvRight.setText("确定");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);

		this.mLoadingDialog = LoadingDialog.createDialog(ApplyVip.this, true);
		this.dialog = DialogTipsBuilder.getInstance(ApplyVip.this);
		this.scrollView = (ScrollView) findViewById(R.id.ScrollView);
		if (Build.VERSION.SDK_INT >= 9) {
			scrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		}
		this.etUserName = (EditText) findViewById(R.id.etUserName);
		this.etUserPhone = (EditText) findViewById(R.id.etUserPhone);
		this.etUserEmail = (EditText) findViewById(R.id.etUserEmail);
		this.etUserBrief = (EditText) findViewById(R.id.etUserBrief);

		this.cityGridView = (NoScrollGridView) findViewById(R.id.cityGridView);
		this.actGridView = (NoScrollGridView) findViewById(R.id.actGridView);
		this.userGridView = (NoScrollGridView) findViewById(R.id.userGridView);
		this.userIconGridView = (NoScrollGridView) findViewById(R.id.gridView);
		this.actAdapter = new ApplyVipInterestAdapter(ApplyVip.this);
		this.userAdapter = new ApplyVipInterestAdapter(ApplyVip.this);
		this.cityAdapter = new ApplyVipCityAdapter(ApplyVip.this);
		this.actGridView.setAdapter(actAdapter);
		this.userGridView.setAdapter(userAdapter);
		this.cityGridView.setAdapter(cityAdapter);

		this.actGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				UserInterestModel model = actAdapter.getItem(position);
				if (null != model) {
					if (model.isSelect()) {
						actIndex--;
						model.setSelect(false);
					} else {
						if (actIndex >= 2) {
							toast("最多选择两个活动标签");
							return;
						} else {
							model.setSelect(true);
							actIndex++;
						}
					}
					actAdapter.notifyDataSetChanged();
				}
			}
		});
		this.userGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				UserInterestModel model = userAdapter.getItem(position);
				if (null != model) {
					if (model.isSelect()) {
						userIndex--;
						model.setSelect(false);
					} else {
						if (userIndex >= 3) {
							toast("最多选择三个用户标签");
							return;
						} else {
							model.setSelect(true);
							userIndex++;
						}
					}
					userAdapter.notifyDataSetChanged();
				}
			}
		});
		this.cityGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				CityListModel model = cityAdapter.getItem(position);
				if (null != model) {
					if (model.isSelect()) {
						cityIndex--;
						model.setSelect(false);
					} else {
						if (cityIndex >= 3) {
							toast("最多选择三个城市");
							return;
						} else {
							model.setSelect(true);
							cityIndex++;
						}
					}
					cityAdapter.notifyDataSetChanged();
				}
			}
		});
		this.userIconAdapter = new PickedImagesAdapter(ApplyVip.this, 3);
		this.userIconGridView.setAdapter(userIconAdapter);
		this.userIconAdapter.setListener(mPickedItemClickListener);

		this.initView();

		this.getActTags();
		this.getUserTags();
	}

	private void initView() {
		this.cityList = new ArrayList<CityListModel>();
		etUserName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
		etUserPhone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
		etUserEmail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
		etUserBrief.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });

		SharedPreferences cityPreferences = ApplyVip.this.getSharedPreferences("CITY_LIST", Context.MODE_PRIVATE);
		String city = cityPreferences.getString("CITY", "");
		if (!city.equals("")) {
			Gson gson = new Gson();
			CityList list = gson.fromJson(city, CityList.class);
			cityList = list.getCities();
			cityAdapter.setNotifyChanged(cityList);
		}
		getCityList();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (userIconAdapter.getList().size() > 0) {
					for (int i = 0; i < userIconAdapter.getList().size(); i++) {
						File file = new File(userIconAdapter.getList().get(i).getPath());
						if (file != null && file.exists()) {
							file.delete();
							file = null;
						}
					}
				}
				finish();
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (TextUtils.isEmpty(etUserName.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("姓名不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (!checkNameChese(etUserName.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("姓名只能输入中文").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (TextUtils.isEmpty(etUserPhone.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("电话不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (MobileUtil.execute(etUserPhone.getText().toString().trim().replace(" ", "")).equals("未知")) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (TextUtils.isEmpty(etUserEmail.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("邮箱不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (!isEmail(etUserEmail.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入正确的邮箱").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (cityIndex == 0) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请至少选择一个城市").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (TextUtils.isEmpty(etUserBrief.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("申请理由不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (userIconAdapter.getList().size() == 0) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("头像不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (actIndex == 0) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请至少选择一个活动标签").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				imgList = null;
				imgList = new ArrayList<Integer>();
				UploadPhoto(userIconAdapter.getList().get(0).getImageFile(), 0);
			}
			break;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (userIconAdapter.getList().size() > 0) {
				for (int i = 0; i < userIconAdapter.getList().size(); i++) {
					File file = new File(userIconAdapter.getList().get(i).getPath());
					if (file != null && file.exists()) {
						file.delete();
						file = null;
					}
				}
			}
			finish();
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
			MMAlert.showAlert(ApplyVip.this, "图片操作", new String[] { "查看图片", "删除图片" }, null, new MMAlert.OnAlertSelectId() {
				public void onDismissed() {
					isShowDialog = false;
				}

				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						isShowDialog = false;
						ArrayList<String> imgList = new ArrayList<String>();
						for (int i = 0; i < userIconAdapter.getList().size(); i++) {
							imgList.add(userIconAdapter.getList().get(i).getPath());
						}
						Intent intent = new Intent(ApplyVip.this, PublishTrendsPicGallery.class);
						intent.putExtra("LIST", imgList);
						intent.putExtra("POSITION", actionPosition);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					case 1:
						isShowDialog = false;
						File file = new File(userIconAdapter.getList().get(actionPosition).getPath());
						if (file != null && file.exists()) {
							file.delete();
						}
						userIconAdapter.del(actionPosition);
						break;
					}
				}
			});
			break;
		case 1:
			ChoosePicAlert.showAlert(ApplyVip.this, "选择图片", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
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
						Intent intent2 = new Intent(ApplyVip.this, SelectPicture.class);
						intent2.putExtra(SelectPicture.MAX_PICS_NUM, (3 - userIconAdapter.getImagesNum()));
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
				userIconAdapter.add(model);
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
			userIconAdapter.add(result);
			if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
		}
	}

	/**
	 * 上传头像
	 */
	private void UploadPhoto(File file, final int index) {
		if (index == 0) {
			mLoadingDialog.setMessage("正在提交申请...");
			mLoadingDialog.show();
		}
		UploadPicParam param = new UploadPicParam(ApplyVip.this, file);
		MultipartRequest task = new MultipartRequest(ApplyVip.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					imgList.add(object.getInt("img_id"));
					if (index < userIconAdapter.getList().size() - 1) {
						UploadPhoto(userIconAdapter.getList().get(index + 1).getImageFile(), index + 1);
					} else {
						UploadVipData();
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
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 申请达人（提交资料）
	 */
	private void UploadVipData() {
		ApplyVipParam param = new ApplyVipParam(ApplyVip.this);
		param.setRealName(etUserName.getText().toString().trim());
		param.setContactPhone(etUserPhone.getText().toString().trim());
		param.setEmail(etUserEmail.getText().toString().trim());
		param.setIntro(etUserBrief.getText().toString().trim());
		ArrayList<CityListModel> city = new ArrayList<CityListModel>();
		for (int i = 0; i < cityList.size(); i++) {
			if (cityList.get(i).isSelect()) {
				city.add(cityList.get(i));
			}
		}
		param.setCity(city);
		ArrayList<UserInterestModel> act = new ArrayList<UserInterestModel>();
		for (int i = 0; i < actList.size(); i++) {
			if (actList.get(i).isSelect()) {
				act.add(actList.get(i));
			}
		}
		param.setActTags(act);
		ArrayList<UserInterestModel> user = new ArrayList<UserInterestModel>();
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).isSelect()) {
				user.add(userList.get(i));
			}
		}
		param.setUserTags(user);
		param.setImgList(imgList);
		HttpStringPost task = new HttpStringPost(ApplyVip.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (userIconAdapter.getList().size() > 0) {
					for (int i = 0; i < userIconAdapter.getList().size(); i++) {
						File file = new File(userIconAdapter.getList().get(i).getPath());
						if (file != null && file.exists()) {
							file.delete();
							file = null;
						}
					}
				}
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("申请成功，请等待审核通知");
				finish();
			}

			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("提交申请失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("提交申请失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 获取类别标签
	 */
	private void getActTags() {
//		GetActTagsParam param = new GetActTagsParam(ApplyVip.this);
		GetUserTagsParam param = new GetUserTagsParam(ApplyVip.this, 1);
		HttpStringPost task = new HttpStringPost(ApplyVip.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				Gson gson = new Gson();
				UserInterestList list = gson.fromJson(result, UserInterestList.class);
				if (list != null) {
					actList = list.getTags();
					actAdapter.setNotifyChanged(actList);
				}
			}

			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				toast("获取活动标签失败，请重试");
				finish();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toast("获取活动标签失败，请重试");
				finish();
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 获取个性标签
	 */
	private void getUserTags() {
		GetUserTagsParam param = new GetUserTagsParam(ApplyVip.this, 2);
		HttpStringPost task = new HttpStringPost(ApplyVip.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				Gson gson = new Gson();
				UserInterestList list = gson.fromJson(result, UserInterestList.class);
				if (list != null) {
					userList = list.getTags();
					userAdapter.setNotifyChanged(userList);
				}
			}

			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				toast("获取用户标签失败，请重试");
				finish();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toast("获取用户标签失败，请重试");
				finish();
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 获取城市列表
	 */
	private void getCityList() {
		GetCityListParam param = new GetCityListParam(ApplyVip.this);
		HttpStringPost task = new HttpStringPost(ApplyVip.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				SharedPreferences cityPreferences = ApplyVip.this.getSharedPreferences("CITY_LIST", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = cityPreferences.edit();
				editor.putString("CITY", result);
				editor.commit();
				Gson gson = new Gson();
				CityList list = gson.fromJson(result, CityList.class);
				cityList = list.getCities();
				cityAdapter.setNotifyChanged(cityList);
			}

			@Override
			public void relogin(String msg) {

			}

			@Override
			public void error(int code, String msg) {
				Toast.makeText(ApplyVip.this, "获取城市失败", Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(ApplyVip.this, "获取城市失败", Toast.LENGTH_SHORT).show();
			}
		}, param.getParameters());
		RequestManager.addRequest(task, ApplyVip.this);
	}

	/**
	 * 判定输入汉字
	 * 
	 * @param c
	 * @return
	 */
	public boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 检测String是否全是中文
	 * 
	 * @param name
	 * @return
	 */
	public boolean checkNameChese(String name) {
		boolean res = true;
		char[] cTemp = name.toCharArray();
		for (int i = 0; i < name.length(); i++) {
			if (!isChinese(cTemp[i])) {
				res = false;
				break;
			}
		}
		return res;
	}

	/**
	 * 判断是否是邮箱
	 */
	public static boolean isEmail(String email) {
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		return doRegex(regex, email);
	}

	private static boolean doRegex(String regex, String content) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		return m.find();
	}

}
