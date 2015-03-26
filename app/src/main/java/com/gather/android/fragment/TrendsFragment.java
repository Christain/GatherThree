package com.gather.android.fragment;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.activity.LoginIndex;
import com.gather.android.activity.PublishTrends;
import com.gather.android.activity.TrendsComment;
import com.gather.android.activity.UserTrends;
import com.gather.android.adapter.TrendsAdapter;
import com.gather.android.adapter.TrendsAdapter.OnItemAllListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.TrendsModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.preference.AppPreference;
import com.gather.android.service.PublishTrendsService;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.tendcloud.tenddata.TCAgent;

/**
 * 切换卡动态
 */
public class TrendsFragment extends BaseFragment implements OnClickListener {

	private View convertView;
	private TextView tvLeft, tvRight, tvTitle;
	private ImageView ivLeft, ivRight;
	private XListView listView;
	private TrendsAdapter adapter;
	private DialogTipsBuilder dialog;
	private GatherApplication application;
	private UserInfoModel userInfoModel;
	private PublishOverBroadCast publishOverBroadCast;
	private DialogChoiceBuilder choiceBuilder;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {

		this.application = (GatherApplication) getActivity().getApplication();
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
		this.choiceBuilder = DialogChoiceBuilder.getInstance(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.convertView = inflater.inflate(R.layout.fragment_trends, (ViewGroup) getActivity().findViewById(R.id.tabhost), false);

		this.tvLeft = (TextView) convertView.findViewById(R.id.tvLeft);
		this.tvRight = (TextView) convertView.findViewById(R.id.tvRight);
		this.ivLeft = (ImageView) convertView.findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) convertView.findViewById(R.id.ivRight);
		this.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

		this.tvLeft.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.GONE);
		this.tvRight.setText("发布");
		this.tvTitle.setText("动态");
		this.ivLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.VISIBLE);
		this.ivRight.setImageResource(R.drawable.title_publish_trends_click_style);
		this.ivRight.setBackgroundDrawable(null);
		this.ivRight.setPadding(10, 10, 10, 10);
		this.ivRight.setOnClickListener(this);

		this.listView = (XListView) convertView.findViewById(R.id.listview);
		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels - getResources().getDimensionPixelOffset(R.dimen.tab_host_high));
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getTrendsList(application.getCityId());
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new TrendsAdapter(getActivity());
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				if (!msg.contains(SuperAdapter.CACHE)) {
					listView.stopRefresh();
					switch (code) {
					case 0:
						if (msg.equals(SuperAdapter.ISNULL)) {
							listView.setFooterImageView("没有动态信息", R.drawable.no_result);
						} else {
							if (!listView.isShown()) {
								listView.setVisibility(View.VISIBLE);
							}
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
		View view = new View(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.tab_host_high));
		view.setLayoutParams(params);
		listView.addFooterView(view);
		this.listView.setAdapter(adapter);
		this.adapter.setOnItemAllListener(new OnItemAllListener() {
			@Override
			public void OnItemListener(TrendsModel model, int position) {
				if (!ClickUtil.isFastClick()) {
					if (AppPreference.hasLogin(getActivity())) {
						if (null != model) {
							Intent intent = new Intent(getActivity(), TrendsComment.class);
							intent.putExtra("MODEL", model);
							intent.putExtra("POSITION", position);
							startActivityForResult(intent, 100);
						}
					} else {
						NetWorkDialog();
					}
				}
			}
		});
		this.ivRight.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				if (AppPreference.hasLogin(getActivity())) {
					Intent intent = new Intent(getActivity(), PublishTrends.class);
					startActivityForResult(intent, 101);
				} else {
					DialogLogin();
				}
				return true;
			}
		});
		this.registerPublishOverReceiver();
		this.initView();

		if (AppPreference.hasLogin(getActivity())) {
			this.adapter.getCacheTrendsList(application.getCityId());
		}
		this.listView.onClickRefush();
		this.adapter.getTrendsList(application.getCityId());
	}

	private void initView() {
		userInfoModel = application.getUserInfoModel();
		if (userInfoModel == null) {
			userInfoModel = AppPreference.getUserInfo(getActivity());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
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
			case 101:// 发布动态返回
				ArrayList<TrendsModel> list = adapter.getDataBaseTrends();
				if (list != null && list.size() > 0) {
					adapter.addItem(list.get(0), 0);
					if (!listView.getFooterTitle().isShown()) {
						listView.setMessage("已是全部");
					}
					Intent intent = new Intent(PublishTrendsService.SERVICE_NAME);
					intent.putExtra("MODEL", list.get(0));
					getActivity().startService(intent);
				} else {
					toast("动态处理失败，请重试");
				}
				break;
			}
		}
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

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivRight:
			if (!ClickUtil.isFastClick()) {
				if (AppPreference.hasLogin(getActivity())) {
					Intent intent = new Intent(getActivity(), UserTrends.class);
					if (userInfoModel != null) {
						intent.putExtra("MODEL", userInfoModel);
					}
					startActivity(intent);
				} else {
					DialogLogin();
				}
			}
			break;
		}
	}

	public void registerPublishOverReceiver() {
		publishOverBroadCast = new PublishOverBroadCast();
		IntentFilter intentFilter = new IntentFilter(PublishTrendsService.PUBLISH_OVER);
		getActivity().registerReceiver(publishOverBroadCast, intentFilter);
	}

	public class PublishOverBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			TCAgent.onEvent(getActivity(), "发布动态");
			if (intent != null) {
				adapter.getTrendsList(application.getCityId());
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
					if (PushManager.isPushEnabled(getActivity().getApplicationContext())) {
						PushManager.stopWork(getActivity().getApplicationContext());
					}
					application.setUserInfoModel(null);
					AppPreference.clearInfo(getActivity());
					Intent intent = new Intent(getActivity(), LoginIndex.class);
					startActivity(intent);
					choiceBuilder.dismiss();
				}
			}).show();
		}
	}

}
