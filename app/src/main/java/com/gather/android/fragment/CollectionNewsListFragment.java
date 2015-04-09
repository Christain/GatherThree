package com.gather.android.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gather.android.R;
import com.gather.android.adapter.CollectionNewsListAdapter;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;

/**
 * 收藏的资讯列表
 */
public class CollectionNewsListFragment extends BaseFragment {

	private View convertView;
	private XListView listView;
	private CollectionNewsListAdapter adapter;
	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;

	private int userId, type;// type:1资讯，2回忆，3票务

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		this.userId = bundle.getInt("UID");
		this.type = bundle.getInt("TYPE");
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
		this.mLoadingDialog = LoadingDialog.createDialog(getActivity(), false);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		this.convertView = inflater.inflate(R.layout.fragment_act_list, (ViewGroup) getActivity().findViewById(R.id.viewpager), false);
		this.listView = (XListView) convertView.findViewById(R.id.listview);

		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getCollectionNewsList(userId, type);
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new CollectionNewsListAdapter(getActivity());
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						switch (type) {
						case 1:
							listView.setFooterImageView("还没有收藏的攻略", R.drawable.no_result);
							break;
						case 2:
							listView.setFooterImageView("还没有收藏的记忆", R.drawable.no_result);
							break;
						case 3:
							listView.setFooterImageView("还没有收藏的订购", R.drawable.no_result);
							break;
						}
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
		this.adapter.getCollectionNewsList(userId, type);
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
