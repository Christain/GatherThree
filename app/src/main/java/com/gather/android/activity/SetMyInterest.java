package com.gather.android.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.SetMyInterestAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.UserInterestList;
import com.gather.android.model.UserInterestModel;
import com.gather.android.params.MyLoveInterestParam;
import com.gather.android.params.UploadUserInterestParam;
import com.gather.android.params.UserLoveInterestParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.KeywordsFlow;
import com.gather.android.widget.KeywordsFlow.AnimationOverListener;
import com.gather.android.widget.KeywordsFlow.OnWordsClickListener;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * 设置自己的爱好
 */
public class SetMyInterest extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	/**
	 * 设置爱好
	 */
	private FrameLayout frameLayout;
	private NoScrollGridView gridView;
	private SetMyInterestAdapter adapter;
	private KeywordsFlow keywordsFlow;
	private boolean canClick;

	private ArrayList<UserInterestModel> myInterestList, allList;
	private int MaxInterest = 5, index;

	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.set_my_interest;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(SetMyInterest.this, true);
		this.dialog = DialogTipsBuilder.getInstance(SetMyInterest.this);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.VISIBLE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("我的爱好");
		this.tvRight.setText("确定");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);
		this.frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
		this.gridView = (NoScrollGridView) findViewById(R.id.gridView);
		this.keywordsFlow = (KeywordsFlow) findViewById(R.id.keyWords);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
		params.width = metrics.widthPixels;
		params.height = (int) ((metrics.widthPixels - (metrics.density * 40 + 0.5f) - getResources().getDimensionPixelOffset(R.dimen.set_interest_padding) * 2) / 5);
		frameLayout.setLayoutParams(params);

		this.adapter = new SetMyInterestAdapter(SetMyInterest.this);
		this.gridView.setAdapter(adapter);

		keywordsFlow.setDuration(500l);
		/**
		 * 选择爱好
		 */
		keywordsFlow.setOnItemClickListener(new OnWordsClickListener() {
			@Override
			public void onWordsListener(String text) {
				if (canClick) {
					canClick = false;
					if (index < MaxInterest) {
						for (int i = 0; i < allList.size(); i++) {
							if (allList.get(i).getName().equals(text)) {
								myInterestList.add(allList.get(i));
								index++;
								allList.remove(i);
								break;
							}
						}
						adapter.setNotifyChanged(myInterestList);
						keywordsFlow.rubKeywords();
						for (int i = 0; i < allList.size(); i++) {
							keywordsFlow.feedKeyword(allList.get(i).getName());
						}
						keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
						if (allList.size() == 0) {
							canClick = true;
						}
					} else {
						toast("最多选择" + MaxInterest + "个爱好");
						canClick = true;
					}
				}
			}
		});
		/**
		 * 删除爱好
		 */
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (canClick) {
					canClick = false;
					allList.add(myInterestList.get(position));
					myInterestList.remove(position);
					index--;
					adapter.setNotifyChanged(myInterestList);
					keywordsFlow.rubKeywords();
					for (int i = 0; i < allList.size(); i++) {
						keywordsFlow.feedKeyword(allList.get(i).getName());
					}
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
				}
			}
		});
		/**
		 * 动画结束监听
		 */
		keywordsFlow.setAnimationOverListener(new AnimationOverListener() {
			@Override
			public void OnAnimationOver() {
				canClick = true;
			}
		});

		getAllInterestList();
		getMyInterestList();
	}

	/**
	 * 获取所有爱好
	 */
	private void getAllInterestList() {
		mLoadingDialog.setMessage("正在提交...");
		mLoadingDialog.show();
		UserLoveInterestParam param = new UserLoveInterestParam();
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                UserInterestList list = gson.fromJson(result, UserInterestList.class);
                if (list != null) {
                    allList = list.getTags();
                }
                for (int i = 0; i < allList.size(); i++) {
                    keywordsFlow.feedKeyword(allList.get(i).getName());
                }
                keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("获取爱好出错");
                finish();
            }
        });
	}

	/**
	 * 获取我的爱好
	 */
	private void getMyInterestList() {
		MyLoveInterestParam param = new MyLoveInterestParam(AppPreference.getUserPersistentInt(SetMyInterest.this, AppPreference.USER_ID));
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                UserInterestList list = gson.fromJson(result, UserInterestList.class);
                if (list != null) {
                    myInterestList = list.getTags();
                    index = myInterestList.size();
                    adapter.setNotifyChanged(myInterestList);
                    canClick = true;
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("获取我的爱好出错");
                finish();
            }
        });
	}

	/**
	 * 修改爱好（提交）
	 */
	private void uploadInterest() {
		mLoadingDialog.setMessage("正在提交...");
		mLoadingDialog.show();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < myInterestList.size(); i++) {
			list.add(myInterestList.get(i).getId());
		}
		UploadUserInterestParam param = new UploadUserInterestParam(list);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("修改成功");
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("提交失败，请重试").withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick() && myInterestList != null) {
				uploadInterest();
			}
			break;
		}
	}

}
