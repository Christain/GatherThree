package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.params.ActDetailCommentPublishParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

import org.apache.http.Header;

/**
 * 活动评论发布
 */
public class ActDetailCommentPublish extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;
	private EditText etContent;
	private TextView tvTextNum;
	private int MAX_NUM = 120;

	private int actId;
	private boolean isRequest = false;

	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;

	@Override
	protected int layoutResId() {
		return R.layout.act_detail_comment_publish;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("ACT_ID")) {
			this.actId = intent.getExtras().getInt("ACT_ID");
			this.dialog = DialogTipsBuilder.getInstance(ActDetailCommentPublish.this);
			this.mLoadingDialog = LoadingDialog.createDialog(ActDetailCommentPublish.this, true);
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.VISIBLE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("发表评论");
			this.tvRight.setText("确认");
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			this.tvRight.setOnClickListener(this);

			this.etContent = (EditText) findViewById(R.id.etContent);
			this.etContent.addTextChangedListener(mTextWatcher);
			this.tvTextNum = (TextView) findViewById(R.id.tvTextNum);
			this.tvTextNum.setText(MAX_NUM + "/" + MAX_NUM);
		} else {
			toast("发表评论信息有误");
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
					DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(ActDetailCommentPublish.this);
					dialog.setMessage("还未发表评论，是否确定退出？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							finish();
						}
					}).show();
				} else {
					finish();
				}
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
					toast("请输入评论内容");
					return;
				}
				if (!isRequest) {
					isRequest = true;
					publishComment(etContent.getText().toString().trim());
				}
			}
			break;
		}
	}

	private void publishComment(String content) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("发表中...");
			mLoadingDialog.show();
		}
		ActDetailCommentPublishParam param = new ActDetailCommentPublishParam(actId, content);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("发表评论失败，请重试").withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
				DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(ActDetailCommentPublish.this);
				dialog.setMessage("还未发表评论，是否确定退出？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						finish();
					}
				}).show();
			} else {
				finish();
			}
		}
		return true;
	}

	/**
	 * 字数监听
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			int num = MAX_NUM - s.length();
			tvTextNum.setText(num + "/" + MAX_NUM);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}
	};

}
