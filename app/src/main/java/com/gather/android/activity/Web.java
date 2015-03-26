package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 普通网页
 */
@SuppressLint("SetJavaScriptEnabled")
public class Web extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private ProgressBar progressBar;
	private WebView webView;

	private String url, title;

	@Override
	protected int layoutResId() {
		return R.layout.web;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("URL") && intent.hasExtra("TITLE")) {
			this.url = intent.getStringExtra("URL");
			this.title = intent.getStringExtra("TITLE");
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvTitle.setText(title);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.GONE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			
			this.progressBar = (ProgressBar) findViewById(R.id.pbWebView);
			this.webView = (WebView) findViewById(R.id.webView);
			progressBar.setMax(100);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setSupportZoom(false);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			webView.getSettings().setRenderPriority(RenderPriority.HIGH);
			webView.setInitialScale(39);
			
			webView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					if (newProgress == 100) {
						progressBar.setProgress(100);
						progressBar.setVisibility(View.GONE);
					} else {
						progressBar.setProgress(newProgress);
					}
					super.onProgressChanged(view, newProgress);
				}
			});
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					progressBar.setProgress(10);
					view.loadUrl(url);
					return true;
				}
			});
			
			webView.loadUrl(url);
		} else {
			toast("网页有误");
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					finish();
				}
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!ClickUtil.isFastClick()) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					finish();
				}
			}
		}
		return true;
	}

}
