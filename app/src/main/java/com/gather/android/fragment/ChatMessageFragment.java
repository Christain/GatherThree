package com.gather.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.AbsListView.LayoutParams;

import com.gather.android.R;
import com.gather.android.activity.Chat;
import com.gather.android.adapter.ChatUserListAdapter;
import com.gather.android.adapter.ChatUserListAdapter.OnItemAllClickListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.MessageUserListModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;

/**
 * 切换卡消息上面的私信
 */
public class ChatMessageFragment extends BaseFragment {

	private View convertView;
	private XListView listView;
	private ChatUserListAdapter adapter;
	private DialogTipsBuilder dialog;
	private GatherApplication application;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		this.application = (GatherApplication) getActivity().getApplication();
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.convertView = inflater.inflate(R.layout.fragment_friends_list_item, (ViewGroup) getActivity().findViewById(R.id.tabhost), false);
		this.listView = (XListView) convertView.findViewById(R.id.listview);

		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels - getResources().getDimensionPixelOffset(R.dimen.tab_host_high));
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getChatUserList(application.getCityId());
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new ChatUserListAdapter(getActivity());
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setFooterImageView("还没有私信", R.drawable.no_result);
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
		View view = new View(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.tab_host_high));
		view.setLayoutParams(params);
		listView.addFooterView(view);
		this.listView.setAdapter(adapter);
		this.adapter.setOnItemAllClickListener(new OnItemAllClickListener() {
			@Override
			public void clickListener(MessageUserListModel model, int position) {
				if (!ClickUtil.isFastClick() && model != null) {
					if (model.getNew_msg_num() != 0) {
						model.setNew_msg_num(0);
						View view = listView.getChildAt(position + 1);
						if (view != null) {
							ImageView ivTips = (ImageView) view.findViewById(R.id.ivTips);
							ivTips.setVisibility(View.GONE);
						}
					}
					Intent intent = new Intent(getActivity(), Chat.class);
					intent.putExtra("UID", model.getId());
					UserInfoModel userInfoModel = new UserInfoModel();
					userInfoModel.setNick_name(model.getNick_name());
					userInfoModel.setHead_img_url(model.getHead_img_url());
					userInfoModel.setBaidu_channel_id(model.getBaidu_channel_id());
					userInfoModel.setBaidu_user_id(model.getBaidu_user_id());
					userInfoModel.setLast_login_platform(model.getLast_login_platform());
					userInfoModel.setIs_shield(model.getStatus());
					intent.putExtra("MODEL", userInfoModel);
					getParentFragment().startActivityForResult(intent, 100);
				}
			}
		});

		this.adapter.getCacheList(application.getCityId());

		this.listView.onClickRefush();
		this.adapter.getChatUserList(application.getCityId());
	}

	@Override
	protected View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup p = (ViewGroup) convertView.getParent();
		if (p != null) {
			p.removeAllViewsInLayout();
		}
		return convertView;
	}

	@Override
	protected void OnSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void OnActivityCreated(Bundle savedInstanceState) {
		if (adapter != null && application.getCityId() != 0) {
			adapter.getChatUserList(application.getCityId());
		}
	}

	public void getRefresh() {
		adapter.getChatUserList(application.getCityId());
	}

	public void getSetNotifyDataChenged() {
		adapter.notifyDataSetChanged();
	}

}
