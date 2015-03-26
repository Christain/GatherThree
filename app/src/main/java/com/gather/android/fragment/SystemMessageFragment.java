package com.gather.android.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.gather.android.R;
import com.gather.android.adapter.SystemMessageListAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;

/**
 * 切换卡消息上面的系统消息
 */
public class SystemMessageFragment extends BaseFragment {

	private View convertView;
	private XListView listView;
	private SystemMessageListAdapter adapter;
	private DialogTipsBuilder dialog;
	private GatherApplication application;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		this.application = (GatherApplication) getActivity().getApplication();
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.convertView = inflater.inflate(R.layout.fragment_friends_list_item, (ViewPager) getParentFragment().getActivity().findViewById(R.id.viewpager), false);
		this.listView = (XListView) convertView.findViewById(R.id.listview);

		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels - getResources().getDimensionPixelOffset(R.dimen.tab_host_high));
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getSystemMessageList();
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new SystemMessageListAdapter(getActivity());
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setFooterImageView("没有系统消息", R.drawable.no_result);
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

		this.adapter.getCacheList();
		this.listView.onClickRefush();
		this.adapter.getSystemMessageList();
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
