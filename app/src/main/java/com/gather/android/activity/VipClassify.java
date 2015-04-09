package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.VipClassifyGridviewAdapter;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.UserInterestList;
import com.gather.android.model.UserInterestModel;
import com.gather.android.params.GetUserTagsParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * 达人分类筛选
 */
public class VipClassify extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;
	private TextView tvSexMale, tvSexFemale;
	private NoScrollGridView tagIdGridView, userGridView;
	private VipClassifyGridviewAdapter tagIdAdapter, userAdapter;
	private ArrayList<UserInterestModel> tagIdList, userList;

	private LoadingDialog mLoadingDialog;

	private int tagId, sex, userTagId;

	@Override
	protected int layoutResId() {
		return R.layout.vip_classify;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("tagId") && intent.hasExtra("sex") && intent.hasExtra("userTagId")) {
			tagId = intent.getExtras().getInt("tagId");
			sex = intent.getExtras().getInt("sex");
			userTagId = intent.getExtras().getInt("userTagId");
			this.mLoadingDialog = LoadingDialog.createDialog(VipClassify.this, true);
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.VISIBLE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("分类");
			this.tvRight.setText("确认");
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			this.tvRight.setOnClickListener(this);

			this.tvSexMale = (TextView) findViewById(R.id.tvSexMale);
			this.tvSexFemale = (TextView) findViewById(R.id.tvSexFemale);
			this.tvSexMale.setOnClickListener(this);
			this.tvSexFemale.setOnClickListener(this);

			this.tagIdGridView = (NoScrollGridView) findViewById(R.id.tagIdGridView);
			this.userGridView = (NoScrollGridView) findViewById(R.id.userGridView);
			this.tagIdAdapter = new VipClassifyGridviewAdapter(VipClassify.this);
			this.userAdapter = new VipClassifyGridviewAdapter(VipClassify.this);
			this.tagIdGridView.setAdapter(tagIdAdapter);
			this.userGridView.setAdapter(userAdapter);

			this.tagIdGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					UserInterestModel model = tagIdAdapter.getItem(position);
					if (null != model) {
						if (!tagIdList.get(position).isSelect()) {
							for (int i = 0; i < tagIdList.size(); i++) {
								if (i != position) {
									tagIdList.get(i).setSelect(false);
								} else {
									tagIdList.get(i).setSelect(true);
									tagId = tagIdList.get(i).getId();
								}
							}
							tagIdAdapter.notifyDataSetChanged();
						}
					}
				}
			});
			this.userGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					UserInterestModel model = userAdapter.getItem(position);
					if (null != model) {
						if (!userList.get(position).isSelect()) {
							for (int i = 0; i < userList.size(); i++) {
								if (i != position) {
									userList.get(i).setSelect(false);
								} else {
									userList.get(i).setSelect(true);
									userTagId = userList.get(i).getId();
								}
							}
							userAdapter.notifyDataSetChanged();
						}
					}
				}
			});

			this.initView();
			this.getUserTags();
			this.getActTags();
		} else {
			toast("分类信息错误，请重试");
			finish();
		}
	}

	private void initView() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		LayoutParams params = (LayoutParams) tvSexMale.getLayoutParams();
		params.width = (int) ((metrics.widthPixels - getResources().getDimensionPixelOffset(R.dimen.vip_classify_gridview_padding) * 2 - getResources().getDimensionPixelOffset(R.dimen.vip_classify_margin_left_right) * 2) / 3);
		params.height = params.width * 2 / 6;
		tvSexMale.setLayoutParams(params);
		tvSexFemale.setLayoutParams(params);

		if (sex == 1) {
			tvSexMale.setSelected(true);
			tvSexFemale.setSelected(false);
		} else if (sex == 2) {
			tvSexMale.setSelected(false);
			tvSexFemale.setSelected(true);
		}

		tvSexMale.setVisibility(View.GONE);
		tvSexFemale.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.tvSexMale:
			if (!ClickUtil.isFastClick()) {
				if (sex != 1) {
					sex = 1;
					tvSexMale.setSelected(true);
					tvSexFemale.setSelected(false);
				}
			}
			break;
		case R.id.tvSexFemale:
			if (!ClickUtil.isFastClick()) {
				if (sex != 2) {
					sex = 2;
					tvSexMale.setSelected(false);
					tvSexFemale.setSelected(true);
				}
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent();
				intent.putExtra("tagId", tagId);
				intent.putExtra("sex", sex);
				intent.putExtra("userTagId", userTagId);
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		}
	}

	/**
	 * 获取类别标签
	 */
	private void getActTags() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("获取分类中...");
			mLoadingDialog.show();
		}
		GetUserTagsParam param = new GetUserTagsParam(1);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                UserInterestList list = gson.fromJson(result, UserInterestList.class);
                if (list != null) {
                    tagIdList = list.getTags();
                    if (tagId != 0) {
                        for (int i = 0; i < tagIdList.size(); i++) {
                            if (tagId == tagIdList.get(i).getId()) {
                                tagIdList.get(i).setSelect(true);
                                break;
                            }
                        }
                    }
                    tagIdAdapter.setNotifyChanged(tagIdList);
                    tvSexMale.setVisibility(View.VISIBLE);
                    tvSexFemale.setVisibility(View.VISIBLE);
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
                toast("获取活动标签失败，请重试");
                finish();
            }
        });
	}

	/**
	 * 获取个性标签
	 */
	private void getUserTags() {
		GetUserTagsParam param = new GetUserTagsParam(2);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                UserInterestList list = gson.fromJson(result, UserInterestList.class);
                if (list != null) {
                    userList = list.getTags();
                    if (userTagId != 0) {
                        for (int i = 0; i < userList.size(); i++) {
                            if (userTagId == userList.get(i).getId()) {
                                userList.get(i).setSelect(true);
                                break;
                            }
                        }
                    }
                    userAdapter.setNotifyChanged(userList);
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("获取用户标签失败，请重试");
                finish();
            }
        });
	}
}
