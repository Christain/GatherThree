package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.params.ActMoreInfoParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 活动注意事项
 * Created by Christain on 2015/3/18.
 */
public class ActAttention extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private TextView tvAttention;
    private ScrollView scrollView;
    private Animation alphaIn;
    private LoadingDialog mLoadingDailog;

    private int actId;
    private String attention = "";

    @Override
    protected int layoutResId() {
        return R.layout.act_attention;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
            if (intent.hasExtra("ATTENTION")) {
                this.attention = intent.getStringExtra("ATTENTION");
            }
            this.mLoadingDailog = LoadingDialog.createDialog(ActAttention.this, true);
            this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("注意事项");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.scrollView = (ScrollView) findViewById(R.id.scrollView);
            this.tvAttention = (TextView) findViewById(R.id.tvAttention);

            if (attention.equals("")) {
                getAttention();
            } else {
                this.tvAttention.setText(attention);
            }
        } else {
            toast("注意事项信息错误");
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
        }
    }

    private void getAttention() {
        if (mLoadingDailog != null && !mLoadingDailog.isShowing()) {
            mLoadingDailog.setMessage("加载中...");
            mLoadingDailog.show();
        }
        ActMoreInfoParam param = new ActMoreInfoParam(ActAttention.this, actId);
        HttpStringPost task = new HttpStringPost(ActAttention.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDailog != null && mLoadingDailog.isShowing()) {
                    mLoadingDailog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    ActMoreInfoModel actMoreInfoModel = gson.fromJson(object.getString("act_info"), ActMoreInfoModel.class);
                    tvAttention.setText(actMoreInfoModel.getAttention());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!scrollView.isShown()) {
                    scrollView.setVisibility(View.VISIBLE);
                    scrollView.startAnimation(alphaIn);
                }
            }

            @Override
            public void relogin(String msg) {
                if (mLoadingDailog != null && mLoadingDailog.isShowing()) {
                    mLoadingDailog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                if (mLoadingDailog != null && mLoadingDailog.isShowing()) {
                    mLoadingDailog.dismiss();
                }
                toast("加载注意事项失败，请重试");
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mLoadingDailog != null && mLoadingDailog.isShowing()) {
                    mLoadingDailog.dismiss();
                }
                toast("加载注意事项失败，请重试");
                finish();
            }
        }, param.getParameters());
        executeRequest(task);
    }
}
