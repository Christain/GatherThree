package com.gather.android.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gather.android.R;
import com.gather.android.adapter.FriendsListAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;

public class FriendsFragment extends BaseFragment {
	
	private View convertView;
	private int titleType;
	private XListView listView;
	private FriendsListAdapter adapter;
	private DialogTipsBuilder dialog;
	private int uid;
	private GatherApplication application;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		titleType = bundle.getInt("TYPE");
		uid = bundle.getInt("UID");
		this.application = (GatherApplication) getActivity().getApplication();
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.convertView = inflater.inflate(R.layout.fragment_friends_list_item, (ViewGroup) getActivity().findViewById(R.id.viewpager), false);
		this.listView = (XListView) convertView.findViewById(R.id.listview);
		
		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getFansAndFocusList(uid, application.getCityId(), titleType+1);
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});
		
		this.adapter = new FriendsListAdapter(getActivity());
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setFooterImageView("没有数据", R.drawable.no_result);
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
		this.listView.setAdapter(adapter);
		
		this.listView.onClickRefush();
		this.adapter.getFansAndFocusList(uid, application.getCityId(), titleType+1);
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
