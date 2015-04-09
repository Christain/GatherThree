package com.gather.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.DialogVersionUpdate;
import com.gather.android.dialog.DialogVersionUpdate.OnUpdateClickListener;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manage.VersionManage;
import com.gather.android.model.VersionModel;
import com.gather.android.params.UpdateVersionParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 关于我们
 */
public class About extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private LinearLayout llOne, llTwo, llThree, llFour, llFive, llSix, llSeven, llEight, llNine;

	private ImageView ivLogo;

	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.about;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(About.this, false);
		this.dialog = DialogTipsBuilder.getInstance(About.this);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.GONE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("关于我们");
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);

		this.llOne = (LinearLayout) findViewById(R.id.llOne);
		this.llTwo = (LinearLayout) findViewById(R.id.llTwo);
		this.llThree = (LinearLayout) findViewById(R.id.llThree);
		this.llFour = (LinearLayout) findViewById(R.id.llFour);
		this.llFive = (LinearLayout) findViewById(R.id.llFive);
		this.llSix = (LinearLayout) findViewById(R.id.llSix);
		this.llSeven = (LinearLayout) findViewById(R.id.llSeven);
		this.llEight = (LinearLayout) findViewById(R.id.llEight);
		this.llNine = (LinearLayout) findViewById(R.id.llNine);

		this.llOne.setOnClickListener(this);
		this.llTwo.setOnClickListener(this);
		this.llThree.setOnClickListener(this);
		this.llFour.setOnClickListener(this);
		this.llFive.setOnClickListener(this);
		this.llSix.setOnClickListener(this);
		this.llSeven.setOnClickListener(this);
		this.llEight.setOnClickListener(this);
		this.llNine.setOnClickListener(this);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		LayoutParams params = (LayoutParams) llOne.getLayoutParams();
		params.width = (metrics.widthPixels - 4 - getResources().getDimensionPixelOffset(R.dimen.about_padding_left_right) * 2) / 3;
		params.height = params.width;
		this.llOne.setLayoutParams(params);
		this.llTwo.setLayoutParams(params);
		this.llThree.setLayoutParams(params);
		this.llFour.setLayoutParams(params);
		this.llFive.setLayoutParams(params);
		this.llSix.setLayoutParams(params);
		this.llSeven.setLayoutParams(params);
		this.llEight.setLayoutParams(params);
		this.llNine.setLayoutParams(params);

		this.ivLogo = (ImageView) findViewById(R.id.ivLogo);
		LayoutParams param = (LayoutParams) ivLogo.getLayoutParams();
		param.width = (int) (metrics.widthPixels - getResources().getDimensionPixelOffset(R.dimen.about_padding_left_right) * 2 - (metrics.density * 120 + 0.5f));
		param.height = param.width * 256 / 470;
		this.ivLogo.setLayoutParams(param);

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.llOne:
			if (!ClickUtil.isFastClick()) {
//				intent = new Intent(About.this, Web.class);
//				intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL + "act/site/businessLogin");
//				intent.putExtra("TITLE", "商户登录");
//				startActivity(intent);
                intent = new Intent(About.this, WXPayTest.class);
                startActivity(intent);
			}
			break;
		case R.id.llTwo:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(About.this, Web.class);
				intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL + "act/site/aboutUs");
				intent.putExtra("TITLE", "关于我们");
				startActivity(intent);
			}
			break;
		case R.id.llThree:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(About.this, Web.class);
				intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL + "act/site/copyrightStatement");
				intent.putExtra("TITLE", "版本声明");
				startActivity(intent);
			}
			break;
		case R.id.llFour:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(About.this, Web.class);
				intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL + "act/site/functionIntroduction");
				intent.putExtra("TITLE", "功能介绍");
				startActivity(intent);
			}
			break;
		case R.id.llFive:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(About.this, Web.class);
				intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL + "act/site/vipRecruit");
				intent.putExtra("TITLE", "招募达人");
				startActivity(intent);
			}
			break;
		case R.id.llSix:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(About.this, Web.class);
				intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL + "act/site/businessCooperation");
				intent.putExtra("TITLE", "业务合作");
				startActivity(intent);
			}
			break;
		case R.id.llSeven:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(About.this, Feedback.class);
				startActivity(intent);
			}
			break;
		case R.id.llEight:
			if (!ClickUtil.isFastClick()) {
				getVersionInfo();
			}
			break;
		case R.id.llNine:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(About.this, Web.class);
				// intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL +
				// "act/site/official_website");
				intent.putExtra("URL", Constant.OFFICIAL_WEB);
				intent.putExtra("TITLE", "官方网站");
				startActivity(intent);
			}
			break;
		}
	}

	/**
	 * 获取版本信息
	 */
	private void getVersionInfo() {
		mLoadingDialog.setMessage("检测中...");
		mLoadingDialog.show();
		UpdateVersionParam param = new UpdateVersionParam();
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("app").length() > 2) {
                        Gson gson = new Gson();
                        VersionModel versionModel = gson.fromJson(object.getString("app"), VersionModel.class);
                        if (versionModel.getCode() > VersionManage.getPackageInfo(About.this).versionCode) {
                            updateVersionDialog(versionModel);
                        } else {
                            toast("已是最新版本");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (dialog != null && !dialog.isShowing()) {
                        dialog.setMessage("版本数据格式错误").withEffect(Effectstype.Shake).show();
                    }
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	/**
	 * 版本更新
	 */
	private void updateVersionDialog(final VersionModel versionModel) {
		DialogVersionUpdate versionUpdate = new DialogVersionUpdate(About.this, R.style.dialog_untran);
		versionUpdate.withDuration(400).withEffect(Effectstype.Fadein).setCancel("以后再说").setSure("立即更新").setVersionName("版本号：" + versionModel.getName()).setVersionSize("大小：" + Constant.VERSION_SIZE).setVersionContent(versionModel.getDescri()).setOnSureClick(new OnUpdateClickListener() {
			@Override
			public void onUpdateListener() {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(versionModel.getVer_url()));
				startActivity(intent);
			}
		}).show();
	}

}
