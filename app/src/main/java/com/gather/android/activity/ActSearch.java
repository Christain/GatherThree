package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.ActSearchAdapter;
import com.gather.android.adapter.ActSearchAdapter.OnActDetailClickListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.ActModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 活动搜索
 */
public class ActSearch extends SwipeBackActivity implements OnClickListener {
	
	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;
	
	private EditText etSearch;
	private XListView listView;
	private ActSearchAdapter adapter;
	private DialogTipsBuilder dialog;
	private GatherApplication application;
	private String keyWords = "";

	@Override
	protected int layoutResId() {
		return R.layout.act_search;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.application = (GatherApplication) getApplication();
		this.dialog = DialogTipsBuilder.getInstance(ActSearch.this);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.VISIBLE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("搜索");
		this.tvRight.setText("确认");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);
		
		this.etSearch = (EditText) findViewById(R.id.etSearch);
		this.listView = (XListView) findViewById(R.id.listview);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		this.listView.setPullLoadEnable(true);
		this.listView.setPullRefreshEnable(true);
		this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
		this.listView.stopLoadMoreMessageBox();
		this.listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				adapter.getSearchActList(application.getCityId(), keyWords);
			}

			@Override
			public void onLoadMore() {
				adapter.loadMore();
			}
		});

		this.adapter = new ActSearchAdapter(ActSearch.this);
		this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
			@Override
			public void refreshOver(int code, String msg) {
				listView.stopRefresh();
				switch (code) {
				case 0:
					if (msg.equals(SuperAdapter.ISNULL)) {
						listView.setFooterImageView("还没有活动", R.drawable.no_result);
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
		this.adapter.setOnActDetailClickListener(new OnActDetailClickListener() {
			@Override
			public void OnDetailListener(ActModel model, int position) {
				if (!ClickUtil.isFastClick() && model != null) {
					Intent intent = new Intent(ActSearch.this, ActDetail.class);
					intent.putExtra("MODEL", model);
					intent.putExtra("ID", model.getId());
					startActivity(intent);
				}
			}
		});
		this.listView.setAdapter(adapter);
		this.listView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (TextUtils.isEmpty(etSearch.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入关键字").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				keyWords = etSearch.getText().toString().trim();
				if (!listView.isShown()) {
					listView.setVisibility(View.VISIBLE);
				}
				adapter.getSearchActList(application.getCityId(), keyWords);
			}
			break;
		}
	}

}
