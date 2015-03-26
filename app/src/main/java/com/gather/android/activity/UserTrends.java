package com.gather.android.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.adapter.UserTrendsAdapter;
import com.gather.android.adapter.UserTrendsAdapter.OnItemAllListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.fragment.TrendsFragment.PublishOverBroadCast;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.TrendsModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.preference.AppPreference;
import com.gather.android.service.PublishTrendsService;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 个人动态
 */
@SuppressLint("InflateParams")
public class UserTrends extends SwipeBackActivity implements OnClickListener {

	private TextView tvLeft, tvTitle, tvRight;
	private ImageView ivLeft, ivRight, ivPublishTrends;
	private LinearLayout llHeader, llPublishTrends;
	private XListView listView;
	private UserTrendsAdapter adapter;
	private View headerView;
	private UserInfoModel userInfoModel;
	private DisplayImageOptions options;
	private DialogTipsBuilder dialog;
	private DialogChoiceBuilder choiceBuilder;
	private int myUserId;
	private PublishOverBroadCast publishOverBroadCast;

	@Override
	protected int layoutResId() {
		return R.layout.user_trends;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("MODEL")) {
			userInfoModel = (UserInfoModel) intent.getSerializableExtra("MODEL");
		}
		this.myUserId = AppPreference.getUserPersistentInt(UserTrends.this, AppPreference.USER_ID);
		this.dialog = DialogTipsBuilder.getInstance(UserTrends.this);
		this.choiceBuilder = DialogChoiceBuilder.getInstance(UserTrends.this);
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.GONE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivRight.setVisibility(View.GONE);
		this.ivLeft.setOnClickListener(this);

		this.listView = (XListView) findViewById(R.id.listview);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.headerView = mInflater.inflate(R.layout.user_trends_header, null);
		this.llHeader = (LinearLayout) headerView.findViewById(R.id.llHeader);
		this.llHeader.setBackgroundColor(0xFFFFFFFF);
		this.llPublishTrends = (LinearLayout) headerView.findViewById(R.id.llPublishTrends);
		this.ivPublishTrends = (ImageView) headerView.findViewById(R.id.ivPublishTrends);
		this.listView.addHeaderView(headerView);
		this.adapter = new UserTrendsAdapter(UserTrends.this);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getUserTrendsList(userInfoModel.getUid());
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new UserTrendsAdapter(UserTrends.this);
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				if (!llHeader.isShown()) {
					llHeader.setVisibility(View.VISIBLE);
				}
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setFooterImageView("没有动态信息", R.drawable.no_result);
					} else {
						listView.setVisibility(View.VISIBLE);
						listView.setText(msg);
					}
					break;
				case 5:
					needLogin(msg);
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
				}
			}
		});
		this.adapter.setLoadMoreOverListener(new OnAdapterLoadMoreOverListener() {
			@Override
			public void loadMoreOver(int code, String msg) {
				listView.stopLoadMore();
				switch (code) {
				case 0:
					listView.setText(msg);
					break;
				case 5:
					needLogin(msg);
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
				}
			}
		});
		this.adapter.setOnItemAllListener(new OnItemAllListener() {
			@Override
			public void OnItemListener(TrendsModel model, int position) {
				if (!ClickUtil.isFastClick()) {
					if (AppPreference.hasLogin(UserTrends.this)) {
						if (null != model) {
							Intent intent = new Intent(UserTrends.this, TrendsComment.class);
							if (userInfoModel != null) {
								model.setUser(userInfoModel);
							}
							intent.putExtra("MODEL", model);
							intent.putExtra("POSITION", position);
							startActivityForResult(intent, 100);
						}
					} else {
						DialogLogin();
					}
				}
			}
		});
		this.listView.setAdapter(adapter);

		this.listView.onClickRefush();
		this.adapter.getUserTrendsList(userInfoModel.getUid());

		if (userInfoModel.getUid() == myUserId) {
			this.registerPublishOverReceiver();
		}
		this.initView();
	}

	private void initView() {
		llHeader.setVisibility(View.INVISIBLE);
		if (null != userInfoModel) {
			if (myUserId == userInfoModel.getUid()) {
				tvTitle.setText("我的动态");
				llPublishTrends.setVisibility(View.VISIBLE);
				ivPublishTrends.setOnClickListener(this);
			} else {
				tvTitle.setText(userInfoModel.getNick_name());
				llPublishTrends.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 100:
				if (data != null) {
					if (data.hasExtra("POSITION")) {
						if (data.hasExtra("MODEL")) {
							TrendsModel model = (TrendsModel) adapter.getItem(data.getExtras().getInt("POSITION"));
							TrendsModel cacheModel = (TrendsModel) data.getSerializableExtra("MODEL");
							model.setComment_num(cacheModel.getComment_num());
							adapter.notifyDataSetChanged();
							cacheModel = null;
						} else if (data.hasExtra("DEL")) {
							adapter.remove(data.getExtras().getInt("POSITION"));
							adapter.notifyDataSetChanged();
						}
					}
				}
				break;
			case 101:
				ArrayList<TrendsModel> list = adapter.getDataBaseTrends();
				if (list != null && list.size() > 0) {
					if (adapter.getList().size() == 0) {
						adapter.getTimeMap().put("今天", 0);
					}
					adapter.addItem(list.get(0), 0);
					if (!listView.getFooterTitle().isShown()) {
						listView.setMessage("已是全部");
					}
					Intent intent = new Intent(PublishTrendsService.SERVICE_NAME);
					intent.putExtra("MODEL", list.get(0));
					startService(intent);
				} else {
					toast("动态处理失败，请重试");
				}
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.ivPublishTrends:
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(UserTrends.this, PublishTrends.class);
				startActivityForResult(intent, 101);
			}
			break;
		}
	}

	public void registerPublishOverReceiver() {
		publishOverBroadCast = new PublishOverBroadCast();
		IntentFilter intentFilter = new IntentFilter(PublishTrendsService.PUBLISH_OVER);
		registerReceiver(publishOverBroadCast, intentFilter);
	}

	public class PublishOverBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && userInfoModel.getUid() == myUserId) {
				adapter.getUserTrendsList(userInfoModel.getUid());
			}
		}
	}

	/**
	 * 需要去登录
	 */
	private void DialogLogin() {
		if (choiceBuilder != null && !choiceBuilder.isShowing()) {
			choiceBuilder.setMessage("想看更多内容，现在就去登录吧？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (PushManager.isPushEnabled(getApplicationContext())) {
						PushManager.stopWork(getApplicationContext());
					}
					GatherApplication application = (GatherApplication) getApplication();
					application.setUserInfoModel(null);
					AppPreference.clearInfo(UserTrends.this);
					Intent intent = new Intent(UserTrends.this, LoginIndex.class);
					startActivity(intent);
					choiceBuilder.dismiss();
				}
			}).show();
		}
	}

}
