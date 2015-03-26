package com.gather.android.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.ActRelationList;
import com.gather.android.activity.ApplyVip;
import com.gather.android.activity.Collection;
import com.gather.android.activity.FriendsList;
import com.gather.android.activity.Set;
import com.gather.android.activity.UserCenter;
import com.gather.android.activity.UserInfo;
import com.gather.android.activity.UserOrder;
import com.gather.android.activity.UserPhoto;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.GetUserInfoParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.OverScrollView;
import com.gather.android.widget.OverScrollView.OverScrollListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 切换卡动态个人中心
 */
public class UserFragment extends BaseFragment implements OnClickListener, OverScrollListener {

	private View convertView;
	private OverScrollView scrollView;
	private TextView tvUserName, tvFocusNum, tvFansNum;
	private ImageView ivPreview, ivUserIcon;
	private LinearLayout llUser, llFocusAndFans, llPhoto, llCollection, llAct, llBuy, llSet, llApplyVip;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private UserInfoModel userInfoModel;
	private GatherApplication application;
	private boolean isRequest = false;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		if (AppPreference.hasLogin(getActivity())) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			this.convertView = inflater.inflate(R.layout.fragment_user, (ViewGroup) getActivity().findViewById(R.id.tabhost), false);

			this.scrollView = (OverScrollView) convertView.findViewById(R.id.ScrollView);
			this.scrollView.setMaxOverScrollHigh(500);
			this.ivPreview = (ImageView) convertView.findViewById(R.id.ivPreview);
			this.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			this.tvFocusNum = (TextView) convertView.findViewById(R.id.tvFocusNum);
			this.tvFansNum = (TextView) convertView.findViewById(R.id.tvFansNum);
			this.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
			this.llUser = (LinearLayout) convertView.findViewById(R.id.llUser);
			this.llFocusAndFans = (LinearLayout) convertView.findViewById(R.id.llFocusAndFans);
			this.llPhoto = (LinearLayout) convertView.findViewById(R.id.llPhoto);
			this.llCollection = (LinearLayout) convertView.findViewById(R.id.llCollection);
			this.llAct = (LinearLayout) convertView.findViewById(R.id.llAct);
			this.llBuy = (LinearLayout) convertView.findViewById(R.id.llBuy);
			this.llSet = (LinearLayout) convertView.findViewById(R.id.llSet);
			this.llApplyVip = (LinearLayout) convertView.findViewById(R.id.llApplyVip);

			this.ivPreview.setOnClickListener(this);
			this.llUser.setOnClickListener(this);
			this.llFocusAndFans.setOnClickListener(this);
			this.llPhoto.setOnClickListener(this);
			this.llCollection.setOnClickListener(this);
			this.llAct.setOnClickListener(this);
			this.llBuy.setOnClickListener(this);
			this.llSet.setOnClickListener(this);
			this.llApplyVip.setOnClickListener(this);
			this.scrollView.setOverScrollListener(this);

			this.application = (GatherApplication) getActivity().getApplication();
			this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
			this.initView();
		}
	}

	private void initView() {
		userInfoModel = application.getUserInfoModel();
		if (userInfoModel == null) {
			userInfoModel = AppPreference.getUserInfo(getActivity());
			if (userInfoModel.getNick_name().equals("")) {
				getUserInfo(application.getCityId());
			}
		}
		tvUserName.setText(userInfoModel.getNick_name());
		if (userInfoModel.getIs_vip() == 0) {
			llApplyVip.setVisibility(View.VISIBLE);
		} else {
			llApplyVip.setVisibility(View.GONE);
		}
		tvFansNum.setText(userInfoModel.getFans_num() + "");
		tvFocusNum.setText(userInfoModel.getFocus_num() + "");
		imageLoader.displayImage(userInfoModel.getHead_img_url(), ivUserIcon, options);
	}

	@Override
	protected View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (convertView != null) {
			ViewGroup p = (ViewGroup) convertView.getParent();
			if (p != null) {
				p.removeAllViews();
			}
			return convertView;
		} else {
			return null;
		}
	}

	@Override
	protected void OnSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void OnActivityCreated(Bundle savedInstanceState) {
		if (AppPreference.hasLogin(getActivity())) {
			if (application.getUserInfoModel() != null) {
				userInfoModel = application.getUserInfoModel();
				if (userInfoModel.getIs_vip() == 0) {
					llApplyVip.setVisibility(View.VISIBLE);
				} else {
					llApplyVip.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ivPreview:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(getActivity(), UserCenter.class);
				intent.putExtra("UID", AppPreference.getUserPersistentInt(getActivity(), AppPreference.USER_ID));
				if (userInfoModel != null) {
					intent.putExtra("MODEL", userInfoModel);
				}
				startActivity(intent);
			}
			break;
		case R.id.llApplyVip:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				intent = new Intent(getActivity(), ApplyVip.class);
				startActivity(intent);
			}
			break;
		case R.id.llUser:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				intent = new Intent(getActivity(), UserInfo.class);
				startActivityForResult(intent, 100);
			}
			break;
		case R.id.llFocusAndFans:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				intent = new Intent(getActivity(), FriendsList.class);
				intent.putExtra("UID", userInfoModel.getUid());
				startActivity(intent);
			}
			break;
		case R.id.llPhoto:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				intent = new Intent(getActivity(), UserPhoto.class);
				startActivity(intent);
			}
			break;
		case R.id.llCollection:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				intent = new Intent(getActivity(), Collection.class);
				intent.putExtra("UID", userInfoModel.getUid());
				startActivity(intent);
			}
			break;
		case R.id.llAct:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				intent = new Intent(getActivity(), ActRelationList.class);
				intent.putExtra("UID", userInfoModel.getUid());
				startActivity(intent);
			}
			break;
		case R.id.llBuy:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				intent = new Intent(getActivity(), UserOrder.class);
				startActivity(intent);
			}
			break;
		case R.id.llSet:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(getActivity(), Set.class);
				startActivity(intent);
			}
			break;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			switch (requestCode) {
			case 100:
				initView();
				break;
			}
		}
	}

	/**
	 * 获取个人信息
	 */
	private void getUserInfo(int cityId) {
		isRequest = true;
		GetUserInfoParam param = new GetUserInfoParam(getActivity(), cityId);
		HttpStringPost task = new HttpStringPost(getActivity(), param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					Gson gson = new Gson();
					userInfoModel = gson.fromJson(object.getString("user"), UserInfoModel.class);
					if (userInfoModel != null) {
						if (application != null) {
							application.setUserInfoModel(userInfoModel);
						}
						AppPreference.saveUserInfo(getActivity(), userInfoModel);
						initView();
					} else {
						Toast.makeText(getActivity(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "个人信息解析失败", Toast.LENGTH_SHORT).show();
				}
				isRequest = false;
			}

			@Override
			public void relogin(String msg) {
				isRequest = false;
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				isRequest = false;
				Toast.makeText(getActivity(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				isRequest = false;
				Toast.makeText(getActivity(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
			}
		}, param.getParameters());
		RequestManager.addRequest(task, getActivity());
	}

	@Override
	public void headerScroll() {
		getUserInfo(application.getCityId());
	}

	@Override
	public void footerScroll() {

	}

}
