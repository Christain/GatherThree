package com.gather.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gather.android.R;
import com.gather.android.activity.ActDetail;
import com.gather.android.adapter.ActListAdapter;
import com.gather.android.adapter.ActListAdapter.OnActDetailClickListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.ActModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;

/**
 * 活动列表
 */
public class ActListFragment extends BaseFragment {

	private View convertView;
	private XListView listView;
	private ActListAdapter adapter;
	private DialogTipsBuilder dialog;
	private GatherApplication application;
	private int titleIndex;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		titleIndex = bundle.getInt("TYPE");
		this.application = (GatherApplication) getActivity().getApplication();
		this.dialog = DialogTipsBuilder.getInstance(getActivity());
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
				adapter.getActList(application.getCityId(), titleIndex);
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new ActListAdapter(getActivity());
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				if (!msg.contains(SuperAdapter.CACHE)) {
					listView.stopRefresh();
				}
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setFooterImageView("还没有活动", R.drawable.no_result);
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
		this.adapter.setOnActDetailClickListener(new OnActDetailClickListener() {
			@Override
			public void OnDetailListener(ActModel model, int position) {
				if (!ClickUtil.isFastClick() && model != null) {
					Intent intent = new Intent(getActivity(), ActDetail.class);
					intent.putExtra("MODEL", model);
					intent.putExtra("ID", model.getId());
					startActivity(intent);
				}
			}
		});
		this.listView.setAdapter(adapter);

		this.listView.onClickRefush();
		if (titleIndex == 1) {
			adapter.getCacheList(true);
		}
		this.adapter.getActList(application.getCityId(), titleIndex);

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
