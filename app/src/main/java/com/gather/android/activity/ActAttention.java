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
import com.gather.android.adapter.ActAttentionAdapter;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActAttentionListModel;
import com.gather.android.params.ActAttentionParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

/**
 * 活动注意事项
 * Created by Christain on 2015/3/18.
 */
public class ActAttention extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private NoScrollListView listView;
    private ActAttentionAdapter adapter;
    private TextView tvAttention;
    private ScrollView scrollView;
    private Animation alphaIn;
    private LoadingDialog mLoadingDailog;

    private int actId;

    @Override
    protected int layoutResId() {
        return R.layout.act_attention;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
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

            this.listView = (NoScrollListView) findViewById(R.id.listview);
            this.scrollView = (ScrollView) findViewById(R.id.scrollView);
            this.tvAttention = (TextView) findViewById(R.id.tvAttention);

            this.scrollView.setVisibility(View.INVISIBLE);
            getAttention();
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
        ActAttentionParam param = new ActAttentionParam(ActAttention.this, actId);
        HttpStringPost task = new HttpStringPost(ActAttention.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDailog != null && mLoadingDailog.isShowing()) {
                    mLoadingDailog.dismiss();
                }
                Gson gson = new Gson();
                ActAttentionListModel list = gson.fromJson(result, ActAttentionListModel.class);
                if (list != null && list.getAct_attentions().size() > 0) {
                    adapter = new ActAttentionAdapter(ActAttention.this, list.getAct_attentions());
                    listView.setAdapter(adapter);
                } else {
                    toast("没有注意事项");
                    finish();
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
