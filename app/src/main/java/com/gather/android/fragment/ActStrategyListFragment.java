package com.gather.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gather.android.R;
import com.gather.android.adapter.ActStrategyListAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.widget.ChatListView;
import com.gather.android.widget.ChatListView.IXListViewListener;

/**
 * 活动攻略列表
 */
public class ActStrategyListFragment extends BaseFragment {

	private View convertView;
	private ChatListView listView;
	private ActStrategyListAdapter adapter;
	private DialogTipsBuilder dialog;
	private GatherApplication application;

	private int type, tagId;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		this.tagId = bundle.getInt("TAG_ID");
		this.type = bundle.getInt("TYPE");
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
		this.application = (GatherApplication) getActivity().getApplication();
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.convertView = inflater.inflate(R.layout.fragment_act_strategy_list, (ViewGroup) getActivity().findViewById(R.id.viewpager), false);
		this.listView = (ChatListView) convertView.findViewById(R.id.listview);
		this.listView.setPullRefreshEnable(true);
		this.adapter = new ActStrategyListAdapter(getActivity());

		this.listView.setAdapter(adapter);
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getMoreStrategyList();
			}
		});
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(final int code, String msg) {
				listView.stopRefresh();
				if (msg.contains("登录")) {
					needLogin(msg);
				} else if (msg.contains("错误")) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("获取失败").withEffect(Effectstype.Shake).show();
					}
				} else {
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							listView.setSelection(code);
						}
					});
				}
			}
		});

		if (tagId == 1) {
			adapter.getCacheList(type);
		}
		this.listView.onClickRefush();
		adapter.getStrategyList(application.getCityId(), tagId, type);
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

	}

}
