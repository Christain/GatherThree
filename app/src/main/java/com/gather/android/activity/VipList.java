package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.VipListAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.VipListModel;
import com.gather.android.params.VipListParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.DataHelper;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 达人列表
 */
public class VipList extends SwipeBackActivity implements OnClickListener {

	private ImageView ivBack;
	private TextView tvTitle, tvSearch, tvClassify;

	private GridView gridView;
	private VipListAdapter adapter;

	private TextView tvFooter;
	private RelativeLayout rlFooter;
	private ProgressBar pbFooter;

	private int cityId, tagId, sex, userTagId, totalNum, maxPage, page = 1, size = 18, isOver;
	private String keyWords = "";
	private static final int REFRESH = 0x10;
	private static final int LOADMORE = 0x11;
	private int loadType;
	private boolean isRefresh = false, isKeywords = false;

	private GatherApplication application;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	private DataHelper helper;
	private static String VIP = "VIP";

	@Override
	protected int layoutResId() {
		return R.layout.vip_list;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.helper = new DataHelper(VipList.this, VIP);
		this.application = (GatherApplication) getApplication();
		this.mLoadingDialog = LoadingDialog.createDialog(VipList.this, true);
		this.dialog = DialogTipsBuilder.getInstance(VipList.this);
		this.ivBack = (ImageView) findViewById(R.id.ivBack);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvSearch = (TextView) findViewById(R.id.tvSearch);
		this.tvClassify = (TextView) findViewById(R.id.tvClassify);
		this.tvTitle.setText("活动达人");

		this.tvFooter = (TextView) findViewById(R.id.tvFooter);
		this.pbFooter = (ProgressBar) findViewById(R.id.pbFooter);
		this.rlFooter = (RelativeLayout) findViewById(R.id.rlFooter);
		this.rlFooter.setOnClickListener(VipList.this);

		this.gridView = (GridView) findViewById(R.id.gridView);
		this.adapter = new VipListAdapter(VipList.this);
		this.gridView.setAdapter(adapter);

		this.ivBack.setOnClickListener(this);
		this.tvSearch.setOnClickListener(this);
		this.tvClassify.setOnClickListener(this);

		this.initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.tvSearch:
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(VipList.this, VipSearch.class);
				startActivityForResult(intent, 101);
			}
			break;
		case R.id.tvClassify:
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(VipList.this, VipClassify.class);
				intent.putExtra("tagId", tagId);
				intent.putExtra("sex", sex);
				intent.putExtra("userTagId", userTagId);
				startActivityForResult(intent, 100);
			}
			break;
		case R.id.rlFooter:
			if (!ClickUtil.isFastClick() && !isRefresh && isOver != 1) {
				tvFooter.setVisibility(View.GONE);
				pbFooter.setVisibility(View.VISIBLE);
				loadType = LOADMORE;
				getVipList(isKeywords);
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
				if (data != null && data.hasExtra("tagId") && data.hasExtra("sex") && data.hasExtra("userTagId")) {
					tagId = data.getExtras().getInt("tagId");
					sex = data.getExtras().getInt("sex");
					userTagId = data.getExtras().getInt("userTagId");
					if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
						mLoadingDialog.setMessage("更新达人中...");
						mLoadingDialog.show();
					}
					page = 1;
					isOver = 0;
					isRefresh = true;
					loadType = REFRESH;
					gridView.setVisibility(View.GONE);
					rlFooter.setVisibility(View.GONE);
					cityId = application.getCityId();
					if (cityId == 0) {
						toast("请先定位您的城市信息");
						finish();
					}
					isKeywords = false;
					getVipList(isKeywords);
				}
				break;
			case 101:
				if (data != null && data.hasExtra("keyWords")) {
					keyWords = data.getStringExtra("keyWords");
					if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
						mLoadingDialog.setMessage("更新达人中...");
						mLoadingDialog.show();
					}
					page = 1;
					isOver = 0;
					isRefresh = true;
					loadType = REFRESH;
					gridView.setVisibility(View.GONE);
					rlFooter.setVisibility(View.GONE);
					cityId = application.getCityId();
					if (cityId == 0) {
						toast("请先定位您的城市信息");
						finish();
					}
					isKeywords = true;
					getVipList(isKeywords);
				}
				break;
			}
		}
	}

	private void initView() {
		gridView.setVisibility(View.GONE);
		page = 1;
		isOver = 0;
		isRefresh = true;
		loadType = REFRESH;
		rlFooter.setBackgroundColor(0x00000000);
		pbFooter.setVisibility(View.VISIBLE);
		tvFooter.setVisibility(View.GONE);
		cityId = application.getCityId();
		if (cityId == 0) {
			toast("请先定位您的城市信息");
			finish();
		}
		isKeywords = false;
		getVipList(isKeywords);
	}

	/**
	 * 获取达人列表
	 */
	private void getVipList(boolean keywords) {
		VipListParam param;
		if (keywords) {
			param = new VipListParam(cityId, keyWords, page, size);
		} else {
			param = new VipListParam(cityId, tagId, sex, userTagId, page, size);
		}
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (!gridView.isShown()) {
                    gridView.setVisibility(View.VISIBLE);
                }
                if (page == 1) {
                    if (helper != null && !isKeywords) {
                        helper.saveData(result);
                    }
                    JSONObject object = null;
                    try {
                        object = new JSONObject(result);
                        totalNum = object.getInt("total_num");
                        if (totalNum % size == 0) {
                            maxPage = totalNum / size;
                        } else {
                            maxPage = (totalNum / size) + 1;
                        }
                    } catch (JSONException e) {
                        isRefresh = false;
                        e.printStackTrace();
                        return;
                    } finally {
                        object = null;
                    }
                }
                Gson gson = new Gson();
                VipListModel list = gson.fromJson(result, VipListModel.class);
                if (list != null && list.getUsers() != null) {
                    switch (loadType) {
                        case REFRESH:
                            if (totalNum == 0) {
                                isOver = 1;
                                refreshOver(returnCode, "ISNULL");
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(returnCode, "ISOVER");
                            } else {
                                page++;
                                refreshOver(returnCode, "CLICK_MORE");
                            }
                            adapter.refreshItems(list.getUsers());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(returnCode, "CLICK_MORE");
                            } else {
                                isOver = 1;
                                loadMoreOver(returnCode, "ISOVER");
                            }
                            adapter.addItems(list.getUsers());
                            break;
                    }
                } else {
                    switch (loadType) {
                        case REFRESH:
                            refreshOver(returnCode, "ISNULL");
                            break;
                        case LOADMORE:
                            isOver = 1;
                            loadMoreOver(returnCode, "ISOVER");
                            break;
                    }
                }
                isRefresh = false;
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRefresh = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("获取活动达人失败，请重试").withEffect(Effectstype.Shake).show();
                }
                isRefresh = false;
                errorMessage();
            }
        });
	}

	private void refreshOver(int code, String msg) {
		if (msg.equals("ISNULL")) {
			rlFooter.setVisibility(View.VISIBLE);
			rlFooter.setBackgroundColor(0x00000000);
			pbFooter.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
			tvFooter.setText("还没有达人");
		} else if (msg.equals("CLICK_MORE")) {
			rlFooter.setVisibility(View.VISIBLE);
			pbFooter.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
			tvFooter.setText("点击更多");
		} else if (msg.equals("ISOVER")) {
			rlFooter.setVisibility(View.GONE);
		}
	}

	private void loadMoreOver(int code, String msg) {
		if (msg.equals("ISNULL")) {
			rlFooter.setVisibility(View.GONE);
		} else if (msg.equals("CLICK_MORE")) {
			rlFooter.setVisibility(View.VISIBLE);
			pbFooter.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
			tvFooter.setText("点击更多");
		} else if (msg.equals("ISOVER")) {
			rlFooter.setVisibility(View.GONE);
		}
	}

	private void errorMessage() {
		if (!rlFooter.isShown()) {
			rlFooter.setVisibility(View.VISIBLE);
		}
		rlFooter.setBackgroundColor(0x00000000);
		pbFooter.setVisibility(View.GONE);
		tvFooter.setVisibility(View.VISIBLE);
		tvFooter.setText("点击重试");
	}
}
