package com.gather.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActCheckInModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.params.ActCheckInSureParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.Header;

/**
 * 通行证
 * Created by Christain on 2015/4/7.
 */
public class ActPassPort extends SwipeBackActivity implements View.OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private TextView tvActTitle, tvNum;
    private ImageView ivUserIcon;
    private Button btSure;
    private LinearLayout llAll, llTip;
    private DisplayImageOptions options;
    private LoadingDialog mLoadingDialog;

    private ActCheckInModel model;
    private ActMoreInfoModel actMoreInfoModel;
    private boolean isRequest = false;

    @Override
    protected int layoutResId() {
        return R.layout.act_pass_port;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("MODEL") && intent.hasExtra("MORE_INFO")) {
            this.model = (ActCheckInModel) intent.getSerializableExtra("MODEL");

            this.actMoreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO");
            this.mLoadingDialog = LoadingDialog.createDialog(ActPassPort.this, true);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("通行证");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
            this.tvActTitle = (TextView) findViewById(R.id.tvActTitle);
            this.tvNum = (TextView) findViewById(R.id.tvNum);
            this.ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);
            this.btSure = (Button) findViewById(R.id.btSure);
            this.llTip = (LinearLayout) findViewById(R.id.llTip);
            this.llAll = (LinearLayout) findViewById(R.id.llAll);

            this.btSure.setOnClickListener(this);
            this.tvRight.setText("签到");
            this.tvRight.setOnClickListener(this);

            this.initView();
        } else {
            toast("签到过程错误");
            finish();
        }
    }

    private void initView() {
        if (model != null) {
            tvActTitle.setText(model.getSubject());
            imageLoader.displayImage(AppPreference.getUserPersistent(ActPassPort.this, AppPreference.USER_PHOTO), ivUserIcon, options);
            if (model.getNeed_sure() == 1) {
                if (model.getStatus() == 0) {
                    llAll.setBackgroundColor(Color.parseColor("#"+ model.getRgb_hex()));
                    llTip.setVisibility(View.VISIBLE);
                    btSure.setVisibility(View.VISIBLE);
                    tvRight.setVisibility(View.GONE);
                } else {
                    llAll.setBackgroundColor(0xFF666666);
                    llTip.setVisibility(View.GONE);
                    btSure.setVisibility(View.GONE);
                    tvRight.setVisibility(View.VISIBLE);
                }
            } else {
                if (model.getStatus() == 0) {
                    llAll.setBackgroundColor(Color.parseColor("#"+ model.getRgb_hex()));
                    tvRight.setVisibility(View.GONE);
                } else {
                    tvRight.setVisibility(View.VISIBLE);
                    llAll.setBackgroundColor(0xFF666666);
                }
                llTip.setVisibility(View.GONE);
                btSure.setVisibility(View.GONE);
            }
            if (actMoreInfoModel.getPass_no().equals("null")) {
                tvNum.setText(actMoreInfoModel.getSerial_no() +"15");
            } else {
                tvNum.setText(actMoreInfoModel.getPass_no());
            }
        } else {
            toast("签到信息错误");
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                if (!ClickUtil.isFastClick()) {
                    finish();
                }
                break;
            case R.id.btSure:
                if (!ClickUtil.isFastClick() && !isRequest) {
                    isRequest = true;
                    CheckinSure();
                }
                break;
            case R.id.tvRight:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActPassPort.this, QRCodeScan.class);
                    intent.putExtra("MORE_INFO", actMoreInfoModel);
                    startActivity(intent);
                }
                break;
        }
    }

    private void CheckinSure() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("确认中...");
            mLoadingDialog.show();
        }
        ActCheckInSureParam param = new ActCheckInSureParam(model.getId());
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                llAll.setBackgroundColor(0xFF666666);
                llTip.setVisibility(View.GONE);
                btSure.setVisibility(View.GONE);
                tvRight.setVisibility(View.VISIBLE);
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
                toast("确认失败，请重试");
            }
        });
    }
}
