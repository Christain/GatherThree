package com.gather.android.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.UserPhotoAdapter;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.UserPhotoList;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.params.GetUserPhotoParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

/**
 * 个人的相册
 */
public class UserPhoto extends SwipeBackActivity implements OnClickListener {

	private TextView tvLeft, tvTitle, tvRight;
	private ImageView ivLeft, ivRight;

	private GridView gridView;
	private ArrayList<UserPhotoModel> picList;
	private UserPhotoAdapter adapter;
	
	private LoadingDialog mLoadingDialog;

	@Override
	protected int layoutResId() {
		return R.layout.user_photo;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(UserPhoto.this, true);
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
		this.tvRight.setText("管理");
		this.ivRight.setVisibility(View.GONE);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);

		this.gridView = (GridView) findViewById(R.id.gridView);
		this.adapter = new UserPhotoAdapter(UserPhoto.this);
		this.gridView.setAdapter(adapter);
		
		this.gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (!ClickUtil.isFastClick()) {
					UserPhotoModel model = adapter.getItem(position);
					if (null != model) {
						Intent intent = new Intent(UserPhoto.this, TrendsPicGallery.class);
						intent.putExtra("USER_PHOTO_LIST", picList);
						intent.putExtra("POSITION", position);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}
			}
		});
		
		getUserPhoto();
	}
	
	/**
	 * 获取个人相册
	 */
	private void getUserPhoto() {
		if (mLoadingDialog != null) {
			mLoadingDialog.setMessage("获取相册中...");
			mLoadingDialog.show();
		}
		GetUserPhotoParam param = new GetUserPhotoParam(UserPhoto.this, AppPreference.getUserPersistentInt(UserPhoto.this, AppPreference.USER_ID), 1, 20);
		HttpStringPost task = new HttpStringPost(UserPhoto.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Gson gson = new Gson();
				UserPhotoList list = gson.fromJson(result, UserPhotoList.class);
				if (list != null && list.getPhotos() != null) {
					picList = list.getPhotos();
					adapter.setPicLis(picList);
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
				toast("获取相册失败，请重试");
				finish();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("获取相册失败，请重试");
				finish();
			}
		}, param.getParameters());
		executeRequest(task);
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
			if (!ClickUtil.isFastClick() && picList != null) {
				Intent intent = new Intent(UserPhoto.this, UserPhotoSet.class);
				intent.putExtra("LIST", picList);
				startActivityForResult(intent, 100);
			}
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 100:
				picList = null;
				picList = new ArrayList<UserPhotoModel>();
				adapter.setPicLis(picList);
				getUserPhoto();
				break;
			}
		}
	}
}
