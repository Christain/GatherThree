package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogDateSelect;
import com.gather.android.dialog.DialogDateSelect.OnDateClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.GetUserInfoParam;
import com.gather.android.params.RegisterUploadPhotoParam;
import com.gather.android.params.UploadUserInfoParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.OverScrollView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 自己的个人资料
 */
public class UserInfo extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	/**
	 * 个人资料
	 */
	private OverScrollView scrollView;
	private ImageView ivUserIcon;
	private LinearLayout llUserIcon, llNickName, llUserSex, llUserAge, llUserInterest, llUserBrief, llUserName, llUserPhone, llUserAddress;
	private TextView tvNickName, tvUserSex, tvUserAge, tvUserInterest, tvUserBrief, tvUserName, tvUserPhone, tvUserAddress;

	/**
	 * 头像选择
	 */
	private File mIconFile = null;
	private Bitmap mIconBmp = null;
	private Uri imgUri;
	private boolean hasIcon = false;

	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private GatherApplication application;
	private int sex, age;
	private String nickName = "", userName = "", user_phone = "", user_address = "", user_brief = "", user_love = "";
	private boolean hasChange = false;
	private DisplayImageOptions options;
	private UserInfoModel userInfoModel = null;

	/**
	 * 不同资料修改的Type
	 */
	public static final int REQUEST_NICK_NAME = 0x21;
	public static final int REQUEST_USER_NAME = 0x22;
	public static final int REQUEST_CONTACT_PHONE = 0x23;
	public static final int REQUEST_CONTACT_ADDRESS = 0x24;
	public static final int REQUEST_USER_BRIEF = 0x25;
	public static final int REQUEST_USER_LOVE = 0x26;

	@Override
	protected int layoutResId() {
		return R.layout.user_info;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.GONE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("个人资料");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);

		this.application = (GatherApplication) getApplication();
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.mLoadingDialog = LoadingDialog.createDialog(UserInfo.this, true);
		this.dialog = DialogTipsBuilder.getInstance(UserInfo.this);
		this.scrollView = (OverScrollView) findViewById(R.id.ScrollView);
		this.ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);
		this.tvNickName = (TextView) findViewById(R.id.tvNickName);
		this.tvUserSex = (TextView) findViewById(R.id.tvUserSex);
		this.tvUserAge = (TextView) findViewById(R.id.tvUserAge);
		this.tvUserInterest = (TextView) findViewById(R.id.tvUserInterest);
		this.tvUserBrief = (TextView) findViewById(R.id.tvUserBrief);
		this.tvUserName = (TextView) findViewById(R.id.tvUserName);
		this.tvUserPhone = (TextView) findViewById(R.id.tvUserPhone);
		this.tvUserAddress = (TextView) findViewById(R.id.tvUserAddress);
		this.llUserIcon = (LinearLayout) findViewById(R.id.llUserIcon);
		this.llNickName = (LinearLayout) findViewById(R.id.llNickName);
		this.llUserSex = (LinearLayout) findViewById(R.id.llUserSex);
		this.llUserAge = (LinearLayout) findViewById(R.id.llUserAge);
		this.llUserInterest = (LinearLayout) findViewById(R.id.llUserInterest);
		this.llUserBrief = (LinearLayout) findViewById(R.id.llUserBrief);
		this.llUserName = (LinearLayout) findViewById(R.id.llUserName);
		this.llUserPhone = (LinearLayout) findViewById(R.id.llUserPhone);
		this.llUserAddress = (LinearLayout) findViewById(R.id.llUserAddress);

		this.llUserIcon.setOnClickListener(this);
		this.llNickName.setOnClickListener(this);
		this.llUserAge.setOnClickListener(this);
		this.llUserInterest.setOnClickListener(this);
		this.llUserBrief.setOnClickListener(this);
		this.llUserName.setOnClickListener(this);
		this.llUserPhone.setOnClickListener(this);
		this.llUserAddress.setOnClickListener(this);

		this.initView();
	}

	private void initView() {
		userInfoModel = application.getUserInfoModel();
		if (userInfoModel == null) {
			userInfoModel = AppPreference.getUserInfo(UserInfo.this);
			getUserInfo(application.getCityId());
		}
		setUserInfo();
	}

	private void setUserInfo() {
		nickName = userInfoModel.getNick_name();
		if (nickName.equals("")) {
			nickName = "未填写";
		}
		userName = userInfoModel.getReal_name();
		if (userName.equals("")) {
			userName = "未填写";
		}
		user_phone = userInfoModel.getPho_num();
		if (user_phone.equals("")) {
			user_phone = "未填写";
		}
		user_address = userInfoModel.getAddress();
		if (user_address.equals("")) {
			user_address = "未填写";
		}
		user_brief = userInfoModel.getIntro();
		if (user_brief.equals("")) {
			user_brief = "未填写";
		}
		user_love = userInfoModel.getHobby();
		if (user_love.equals("")) {
			user_love = "未填写";
		}
		sex = userInfoModel.getSex();
		age = TimeUtil.getUserAge(userInfoModel.getBirth());
		imageLoader.displayImage(userInfoModel.getHead_img_url(), ivUserIcon, options);
		tvNickName.setText(nickName);
		if (sex == 1) {
			tvUserSex.setText("男");
		} else {
			tvUserSex.setText("女");
		}
		if (age == -1) {
			tvUserAge.setText("0");
		} else {
			tvUserAge.setText(age + "");
		}
		tvUserBrief.setText(user_brief);
		tvUserBrief.post(new Runnable() {
			@Override
			public void run() {
				if (tvUserBrief.getLineCount() > 1) {
					tvUserBrief.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				} else {
					tvUserBrief.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				}
			}
		});
		tvUserName.setText(userName);
		tvUserPhone.setText(user_phone);
		tvUserAddress.setText(user_address);
		tvUserAddress.post(new Runnable() {
			@Override
			public void run() {
				if (tvUserAddress.getLineCount() > 1) {
					tvUserAddress.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				} else {
					tvUserAddress.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				}
			}
		});
		tvUserInterest.setText(user_love);
		tvUserInterest.post(new Runnable() {
			@Override
			public void run() {
				if (tvUserInterest.getLineCount() > 1) {
					tvUserInterest.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				} else {
					tvUserInterest.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				}
			}
		});
	}

	/**
	 * 获取个人信息
	 */
	private void getUserInfo(int cityId) {
		GetUserInfoParam param = new GetUserInfoParam(cityId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    userInfoModel = gson.fromJson(object.getString("user"), UserInfoModel.class);
                    if (userInfoModel != null) {
                        if (application != null) {
                            application.setUserInfoModel(userInfoModel);
                        }
                        AppPreference.saveUserInfo(UserInfo.this, userInfoModel);
                        setUserInfo();
                    } else {
                        Toast.makeText(UserInfo.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserInfo.this, "个人信息解析失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (hasChange) {
					setResult(RESULT_OK);
				}
				finish();
			}
			break;
		case R.id.llUserIcon:
			ChoosePicAlert.showAlert(UserInfo.this, "选择头像", new String[] { "相机拍照", "相册选取" }, null, new ChoosePicAlert.OnAlertSelectId() {
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
		case R.id.llNickName:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(UserInfo.this, EditData.class);
				intent.putExtra("CONTENT", nickName);
				intent.putExtra("TYPE", REQUEST_NICK_NAME);
				startActivityForResult(intent, REQUEST_NICK_NAME);
			}
			break;
		case R.id.llUserAge:
			DialogDateSelect dialogDate = new DialogDateSelect(UserInfo.this, R.style.dialog_date);
			dialogDate.withDuration(300).withEffect(Effectstype.Fadein).setOnSureClick(new OnDateClickListener() {
				@Override
				public void onDateListener(final String date) {
					if (TimeUtil.getUserAge(date) == -1) {
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("年龄选择错误").withEffect(Effectstype.Shake).show();
						}
					} else {
						age = TimeUtil.getUserAge(date);
						tvUserAge.setText(age + "");
						UploadUserInfoParam param = new UploadUserInfoParam();
						param.SaveAge(date);
                        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                            @Override
                            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                                hasChange = true;
                                userInfoModel.setBirth(date);
                                application.setUserInfoModel(userInfoModel);
                                AppPreference.save(UserInfo.this, AppPreference.USER_BIRTHDAY, date);
                            }

                            @Override
                            public void onNeedLogin(String msg) {
                                needLogin(msg);
                            }

                            @Override
                            public void onResponseFailed(int returnCode, String errorMsg) {
                                toast("年龄保存失败，请重试");
                            }
                        });
					}
				}
			}).show();
			break;
		case R.id.llUserInterest:
			intent = new Intent(UserInfo.this, EditData.class);
			intent.putExtra("CONTENT", user_love);
			intent.putExtra("TYPE", REQUEST_USER_LOVE);
			startActivityForResult(intent, REQUEST_USER_LOVE);
			break;
		case R.id.llUserBrief:
			intent = new Intent(UserInfo.this, EditData.class);
			intent.putExtra("CONTENT", user_brief);
			intent.putExtra("TYPE", REQUEST_USER_BRIEF);
			startActivityForResult(intent, REQUEST_USER_BRIEF);
			break;
		case R.id.llUserName:
			intent = new Intent(UserInfo.this, EditData.class);
			intent.putExtra("CONTENT", userName);
			intent.putExtra("TYPE", REQUEST_USER_NAME);
			startActivityForResult(intent, REQUEST_USER_NAME);
			break;
		case R.id.llUserPhone:
			intent = new Intent(UserInfo.this, EditData.class);
			intent.putExtra("CONTENT", user_phone);
			intent.putExtra("TYPE", REQUEST_CONTACT_PHONE);
			startActivityForResult(intent, REQUEST_CONTACT_PHONE);
			break;
		case R.id.llUserAddress:
			intent = new Intent(UserInfo.this, EditData.class);
			intent.putExtra("CONTENT", user_address);
			intent.putExtra("TYPE", REQUEST_CONTACT_ADDRESS);
			startActivityForResult(intent, REQUEST_CONTACT_ADDRESS);
			break;
		}
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
					mIconBmp = BitmapUtils.getBitmapFromUri(UserInfo.this, imgUri);
					new ProgressSaveImage().execute();
				} else {
					toast("图片截取失败，请重试");
				}
				break;
			case REQUEST_NICK_NAME:
				if (data != null && data.hasExtra("CONTENT")) {
					nickName = data.getStringExtra("CONTENT");
					if (nickName.equals("")) {
						nickName = "未填写";
					}
					tvNickName.setText(nickName);
					UploadUserInfoParam param = new UploadUserInfoParam();
					param.SaveNickName(nickName);
					AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                        @Override
                        public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                            hasChange = true;
                            userInfoModel.setNick_name(nickName);
                            application.setUserInfoModel(userInfoModel);
                            AppPreference.save(UserInfo.this, AppPreference.NICK_NAME, nickName);
                        }

                        @Override
                        public void onNeedLogin(String msg) {
                            needLogin(msg);
                        }

                        @Override
                        public void onResponseFailed(int returnCode, String errorMsg) {
                            toast("昵称保存失败，请重试");
                        }
                    });
				}
				break;
			case REQUEST_USER_NAME:
				if (data != null && data.hasExtra("CONTENT")) {
					userName = data.getStringExtra("CONTENT");
					if (userName.equals("")) {
						userName = "未填写";
					}
					tvUserName.setText(userName);
					UploadUserInfoParam param = new UploadUserInfoParam();
					param.SaveUserName(userName);
                    AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                        @Override
                        public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                            hasChange = true;
                            userInfoModel.setReal_name(userName);
                            application.setUserInfoModel(userInfoModel);
                            AppPreference.save(UserInfo.this, AppPreference.REAL_NAME, userName);
                        }

                        @Override
                        public void onNeedLogin(String msg) {
                            needLogin(msg);
                        }

                        @Override
                        public void onResponseFailed(int returnCode, String errorMsg) {
                            toast("姓名保存失败，请重试");
                        }
                    });
				}
				break;
			case REQUEST_USER_BRIEF:
				if (data != null && data.hasExtra("CONTENT")) {
					user_brief = data.getStringExtra("CONTENT");
					if (user_brief.equals("")) {
						user_brief = "未填写";
					}
					tvUserBrief.setText(user_brief);
					if (tvUserBrief.getLineCount() > 1) {
						tvUserBrief.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					} else {
						tvUserBrief.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
					}
					UploadUserInfoParam param = new UploadUserInfoParam();
					param.SaveBrief(user_brief);
                    AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                        @Override
                        public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                            hasChange = true;
                            userInfoModel.setIntro(user_brief);
                            application.setUserInfoModel(userInfoModel);
                            AppPreference.save(UserInfo.this, AppPreference.INTRO, user_brief);
                        }

                        @Override
                        public void onNeedLogin(String msg) {
                            needLogin(msg);
                        }

                        @Override
                        public void onResponseFailed(int returnCode, String errorMsg) {
                            toast("个性签名保存失败，请重试");
                        }
                    });
				}
				break;
			case REQUEST_CONTACT_PHONE:
				if (data != null && data.hasExtra("CONTENT")) {
					user_phone = data.getStringExtra("CONTENT");
					if (user_phone.equals("")) {
						user_phone = "未填写";
					}
					tvUserPhone.setText(user_phone);
					UploadUserInfoParam param = new UploadUserInfoParam();
					param.SavePhone(user_phone);
                    AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                        @Override
                        public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                            hasChange = true;
                            userInfoModel.setPho_num(user_phone);
                            application.setUserInfoModel(userInfoModel);
                            AppPreference.save(UserInfo.this, AppPreference.CONTACT_PHONE, user_phone);
                        }

                        @Override
                        public void onNeedLogin(String msg) {
                            needLogin(msg);
                        }

                        @Override
                        public void onResponseFailed(int returnCode, String errorMsg) {
                            toast("联系电话保存失败，请重试");
                        }
                    });
				}
				break;
			case REQUEST_CONTACT_ADDRESS:
				if (data != null && data.hasExtra("CONTENT")) {
					user_address = data.getStringExtra("CONTENT");
					if (user_address.equals("")) {
						user_address = "未填写";
					}
					tvUserAddress.setText(user_address);
					if (tvUserAddress.getLineCount() > 1) {
						tvUserAddress.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					} else {
						tvUserAddress.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
					}
					UploadUserInfoParam param = new UploadUserInfoParam();
					param.SaveAddress(user_address);
                    AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                        @Override
                        public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                            hasChange = true;
                            userInfoModel.setAddress(user_address);
                            application.setUserInfoModel(userInfoModel);
                            AppPreference.save(UserInfo.this, AppPreference.ADDRESS, user_address);
                        }

                        @Override
                        public void onNeedLogin(String msg) {
                            needLogin(msg);
                        }

                        @Override
                        public void onResponseFailed(int returnCode, String errorMsg) {
                            toast("通讯地址保存失败，请重试");
                        }
                    });
				}
				break;
			case REQUEST_USER_LOVE:
				if (data != null && data.hasExtra("CONTENT")) {
					user_love = data.getStringExtra("CONTENT");
					if (user_love.equals("")) {
						user_love = "未填写";
					}
					tvUserInterest.setText(user_love);
					if (tvUserInterest.getLineCount() > 1) {
						tvUserInterest.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					} else {
						tvUserInterest.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
					}
					UploadUserInfoParam param = new UploadUserInfoParam();
					param.SaveHobby(user_love);
                    AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                        @Override
                        public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                            hasChange = true;
                            userInfoModel.setHobby(user_love);
                            application.setUserInfoModel(userInfoModel);
                            AppPreference.save(UserInfo.this, AppPreference.HOBBY, user_love);
                        }

                        @Override
                        public void onNeedLogin(String msg) {
                            needLogin(msg);
                        }

                        @Override
                        public void onResponseFailed(int returnCode, String errorMsg) {
                            toast("爱好保存失败，请重试");
                        }
                    });
				}
				break;
			}
		}
	}

	private void tailor(Uri uri) { // 截图
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String url = IntentManage.getPath(UserInfo.this, uri);
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
		ivUserIcon.setImageResource(R.drawable.default_user_icon);
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
				ivUserIcon.setImageBitmap(BitmapUtils.getRoundBitmap(mIconBmp));
				hasIcon = true;
				imgUri = null;
				uploadPhoto();
			} else {
				Toast.makeText(getApplicationContext(), "头像处理失败，请重试", Toast.LENGTH_LONG).show();
				recycleBmp();
				hasIcon = false;
				mIconFile = null;
				imgUri = null;
			}
		}
	}

	/**
	 * 上传头像
	 */
	private void uploadPhoto() {
		mLoadingDialog.setMessage("正在提交...");
		mLoadingDialog.show();
		RegisterUploadPhotoParam param = new RegisterUploadPhotoParam(mIconFile);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    JSONObject object = new JSONObject(result);
                    UploadUserInfoParam param = new UploadUserInfoParam();
                    param.SaveUserIcon(object.getInt("img_id"));
                    AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                        @Override
                        public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                            hasChange = true;
                            userInfoModel.setHead_img_url("file:///mnt/sdcard/Gather/upload/" + mIconFile.getName());
                            application.setUserInfoModel(userInfoModel);
                            AppPreference.save(UserInfo.this, AppPreference.USER_PHOTO, "file:///mnt/sdcard/Gather/upload/" + mIconFile.getName());
                        }

                        @Override
                        public void onNeedLogin(String msg) {
                            needLogin(msg);
                        }

                        @Override
                        public void onResponseFailed(int returnCode, String errorMsg) {
                            toast("头像保存失败，请重试");
                        }
                    });
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasChange) {
				setResult(RESULT_OK);
			}
			finish();
		}
		return true;
	}

}
