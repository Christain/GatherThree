package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.adapter.ActDetailCommentAdapter;
import com.gather.android.adapter.ActListAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 活动的评论
 */
public class ActDetailComment extends SwipeBackActivity implements OnClickListener {
	
	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;
	
	private XListView listView;
	private ActDetailCommentAdapter adapter;
	
	private int actId;
	private DialogTipsBuilder dialog;
	private DialogChoiceBuilder choiceBuilder;

	@Override
	protected int layoutResId() {
		return R.layout.act_detail_comment;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("ACT_ID")) {
			actId = intent.getExtras().getInt("ACT_ID");
			this.dialog = DialogTipsBuilder.getInstance(ActDetailComment.this);
			this.choiceBuilder = DialogChoiceBuilder.getInstance(ActDetailComment.this);
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.VISIBLE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("活动评论");
			this.tvRight.setText("发表评论");
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			this.tvRight.setOnClickListener(this);
			
			this.listView = (XListView) findViewById(R.id.listview);
			
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			this.listView.setPullLoadEnable(true);
			this.listView.setPullRefreshEnable(true);
			this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
			this.listView.stopLoadMoreMessageBox();
			this.listView.setXListViewListener(new IXListViewListener() {
				@Override
				public void onRefresh() {
					adapter.getActCommentList(actId);
				}

				@Override
				public void onLoadMore() {
					adapter.loadMore();
				}
			});

			this.adapter = new ActDetailCommentAdapter(ActDetailComment.this);
			this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
				@Override
				public void refreshOver(int code, String msg) {
					listView.stopRefresh();
					switch (code) {
					case 0:
						if (msg.equals(SuperAdapter.ISNULL)) {
							listView.setFooterImageView("还没有评论", R.drawable.no_result);
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
			this.adapter.getActCommentList(actId);
		} else {
			toast("活动评论信息有误");
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
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (AppPreference.hasLogin(ActDetailComment.this)) {
					Intent intent = new Intent(ActDetailComment.this, ActDetailCommentPublish.class);
					intent.putExtra("ACT_ID", actId);
					startActivityForResult(intent, 100);
				} else {
					DialogLogin();
				}
			}
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 100:
				adapter.getActCommentList(actId);
				break;
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
					AppPreference.clearInfo(ActDetailComment.this);
					Intent intent = new Intent(ActDetailComment.this, LoginIndex.class);
					startActivity(intent);
					choiceBuilder.dismiss();
				}
			}).show();
		}
	}
}
