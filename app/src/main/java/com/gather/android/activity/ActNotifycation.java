package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.ActNotifycationAdapter;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActNotifyModel;
import com.gather.android.model.ActNotifyModelList;
import com.gather.android.params.ActNotifyParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * 活动通知
 * Created by Christain on 2015/3/18.
 */
public class ActNotifycation extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llEditText;
    private EditText etContent;
    private ImageView btResult;
    private ExpandableListView listView;
    private ActNotifycationAdapter adapter;
    private int actId;
    private  ActNotifyModelList list = null;

    private LoadingDialog mLoadingDialog;
    private boolean isRequest = false;

    @Override
    protected int layoutResId() {
        return R.layout.act_notifycation;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
            this.mLoadingDialog = LoadingDialog.createDialog(ActNotifycation.this, true);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.VISIBLE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("通知");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.llEditText = (LinearLayout) findViewById(R.id.llEditText);
            this.etContent = (EditText) findViewById(R.id.etContent);
            this.btResult = (ImageView) findViewById(R.id.btResult);
            this.btResult.setOnClickListener(this);
            this.listView = (ExpandableListView) findViewById(R.id.listview);
            this.adapter = new ActNotifycationAdapter(ActNotifycation.this);
            this.listView.setAdapter(adapter);

            getNotify();
        } else {
            toast("加载失败，请重试");
            finish();
        }
    }

    private void getNotify() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("加载中...");
            mLoadingDialog.show();
        }
        ActNotifyParam param = new ActNotifyParam(ActNotifycation.this, actId);
        HttpStringPost task = new HttpStringPost(ActNotifycation.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                list = gson.fromJson(result, ActNotifyModelList.class);
                if (list != null && list.getAct_notices().size() > 0){
                    adapter.setNotifyMessage(list.getAct_notices());
                } else {
                    toast("还没有新通知");
                }
            }

            @Override
            public void relogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("加载失败，请重试");
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("加载失败，请重试");
                finish();
            }
        }, param.getParameters());
        executeRequest(task);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLeft:
                if (!ClickUtil.isFastClick()) {
                    finish();
                }
                break;
            case R.id.btResult:
                if (!ClickUtil.isFastClick()) {
                    if (list != null) {
                        if (!TextUtils.isEmpty(etContent.getText().toString())) {
                            if (!isRequest) {
                                isRequest = true;
                                StringCompare(etContent.getText().toString(), list.getAct_notices());
                            }
                        } else {
                            toast("请输入搜索内容");
                            return;
                        }
                     } else {
                        toast("还没有通知消息");
                        return;
                    }
                }
                break;
        }
    }

    private void StringCompare(String compare, ArrayList<ActNotifyModel> list) {
        ArrayList<ActNotifyModel> hasList = new ArrayList<ActNotifyModel>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSubject().contains(compare) || list.get(i).getDescri().contains(compare)) {
                hasList.add(list.get(i));
            }
        }
        if (hasList.size() > 0) {
            adapter.setSearchNotifyMessage(compare, hasList);
        }
        isRequest = false;
    }
}
