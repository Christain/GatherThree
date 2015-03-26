package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

public class VipSearch extends SwipeBackActivity implements OnClickListener {
	
	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;
	
	private EditText etSearch;
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.vip_search;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
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
		this.tvRight.setText("确定");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);
		
		this.dialog = DialogTipsBuilder.getInstance(VipSearch.this);
		this.etSearch = (EditText) findViewById(R.id.etSearch);
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
				Intent intent = new Intent();
				intent.putExtra("keyWords", etSearch.getText().toString().trim());
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		}
	}

}
