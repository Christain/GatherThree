package com.gather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.ActLocationAndCarPlaceAdapter;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActAddressAndCarLocationListModel;
import com.gather.android.params.ActMoreAddressParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

/**
 * 活动位置导航及停车点
 * Created by Christain on 2015/3/30.
 */
public class ActLocationAndCarPlace extends SwipeBackActivity implements View.OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private ListView listView;
    private View headerView;
    private ActLocationAndCarPlaceAdapter adapter;
    private int actId;

    private LoadingDialog mLoadingDialog;
    private Animation alphaIn;

    @Override
    protected int layoutResId() {
        return R.layout.act_location_and_car_place;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
            this.mLoadingDialog = LoadingDialog.createDialog(ActLocationAndCarPlace.this, true);
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
            this.tvTitle.setText("位置导航及停车点");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.listView = (ListView) findViewById(R.id.listview);
            LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.headerView = mInflater.inflate(R.layout.item_act_location_and_car_place_header, null);
            this.listView.addHeaderView(headerView);
            this.adapter = new ActLocationAndCarPlaceAdapter(ActLocationAndCarPlace.this);
            this.listView.setAdapter(adapter);

            this.listView.setVisibility(View.INVISIBLE);
            getPlaceList();
        } else {
            toast("加载失败，请重试");
            finish();
        }
    }

    private void getPlaceList() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("加载中...");
            mLoadingDialog.show();
        }
        ActMoreAddressParam param = new ActMoreAddressParam(ActLocationAndCarPlace.this, actId);
        HttpStringPost task = new HttpStringPost(ActLocationAndCarPlace.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                ActAddressAndCarLocationListModel list = gson.fromJson(result, ActAddressAndCarLocationListModel.class);
                if (list != null && list.getAct_addrs().size() > 0) {
                    adapter.setPlaceList(list.getAct_addrs());
                    if (listView != null && !listView.isShown()) {
                        listView.startAnimation(alphaIn);
                        listView.setVisibility(View.VISIBLE);
                    }
                } else {
                    toast("没有活动地点信息");
                    finish();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                if (!ClickUtil.isFastClick()) {
                    finish();
                }
                break;
        }
    }
}
