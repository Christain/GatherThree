package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.CollectionNewsListAdapter;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 收藏
 */
public class Collection extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private XListView listView;
	private CollectionNewsListAdapter adapter;
	private DialogTipsBuilder dialog;

	private int userId;

	@Override
	protected int layoutResId() {
		return R.layout.collection;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("UID")) {
			this.userId = intent.getExtras().getInt("UID");
			this.dialog = DialogTipsBuilder.getInstance(Collection.this);
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.GONE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("收藏");
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);

			this.listView = (XListView) findViewById(R.id.listview);

			DisplayMetrics metrics = getResources().getDisplayMetrics();
			this.listView.setPullLoadEnable(true);
			this.listView.setPullRefreshEnable(true);
			this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
			this.listView.stopLoadMoreMessageBox();
			this.listView.setXListViewListener(new IXListViewListener() {
				@Override
				public void onRefresh() {
					adapter.getCollectionNewsList(userId, 0);
				}

				@Override
				public void onLoadMore() {
					adapter.loadMore();
				}
			});

			this.adapter = new CollectionNewsListAdapter(Collection.this);
			this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
				@Override
				public void refreshOver(int code, String msg) {
					listView.stopRefresh();
					switch (code) {
					case 0:
						if (msg.equals(SuperAdapter.ISNULL)) {
							listView.setFooterImageView("还没有收藏", R.drawable.no_result);
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

			this.adapter.getCacheList();
			this.listView.onClickRefush();
			this.adapter.getCollectionNewsList(userId, 0);
		} else {
			toast("收藏信息有误");
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		}
	}

}
