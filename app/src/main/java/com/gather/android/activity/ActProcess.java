package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.ActProcessAdapter;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActModulesStatusModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.model.ActProcessListModel;
import com.gather.android.model.ActProcessModel;
import com.gather.android.params.ActModulesStatusParam;
import com.gather.android.params.ActMoreInfoParam;
import com.gather.android.params.ActProcessParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/17.
 */
public class ActProcess extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llMenu, llAttention, llIntro, llProcess;
    private NoScrollListView listView;
    private ActProcessAdapter adapter;
    private View headerView;
    private ScrollView scrollView;

    private ActModulesStatusModel modulesStatusModel;
    private ActMoreInfoModel actMoreInfoModel;
    private int actId;
    private Animation alphaIn;
    private LoadingDialog mLoadingDialog;

    @Override
    protected int layoutResId() {
        return R.layout.act_process;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
            if (intent.hasExtra("MORE_INFO_MODEL")) {
                this.actMoreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO_MODEL");
            }
            this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
            this.mLoadingDialog = LoadingDialog.createDialog(ActProcess.this, true);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("活动流程");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.scrollView = (ScrollView) findViewById(R.id.scrollView);
            this.listView = (NoScrollListView) findViewById(R.id.listview);
            this.headerView = LayoutInflater.from(ActProcess.this).inflate(R.layout.item_act_process_header, null);
            this.listView.addHeaderView(headerView);
            this.adapter = new ActProcessAdapter(ActProcess.this);
            this.listView.setAdapter(adapter);
            this.llProcess = (LinearLayout) findViewById(R.id.llProcess);
            this.llMenu = (LinearLayout) findViewById(R.id.llMenu);
            this.llAttention = (LinearLayout) findViewById(R.id.llAttention);
            this.llIntro = (LinearLayout) findViewById(R.id.llIntro);

            this.llMenu.setOnClickListener(this);
            this.llAttention.setOnClickListener(this);
            this.llIntro.setOnClickListener(this);
            this.llProcess.setVisibility(View.GONE);
            if (intent.hasExtra("MODEL")) {
                this.modulesStatusModel = (ActModulesStatusModel) intent.getSerializableExtra("MODEL");
                setActModulesStatus();
            } else {
                scrollView.setVisibility(View.INVISIBLE);
                getActModulesStatus();
            }
            if (actMoreInfoModel == null) {
                getActMoreInfo(false);
            }
        } else {
            toast("获取活动流程错误");
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
            case R.id.llMenu:
                if (!ClickUtil.isFastClick()) {

                }
                break;
            case R.id.llAttention:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActProcess.this, ActAttention.class);
                    if (actMoreInfoModel != null) {
                        intent.putExtra("ATTENTION", actMoreInfoModel.getAttention());
                    }
                    startActivity(intent);
                }
                break;
            case R.id.llIntro:
                if (!ClickUtil.isFastClick()) {
                    if (actMoreInfoModel != null) {
                        Intent intent = new Intent(ActProcess.this, Web.class);
                        intent.putExtra("TITLE", "主办方介绍");
                        intent.putExtra("URL", actMoreInfoModel.getBusi_url());
                        startActivity(intent);
                    } else {
                        getActMoreInfo(true);
                    }
                }
                break;
        }
    }

    /**
     * 设置活动各模块信息
     */
    private void setActModulesStatus() {
        if (modulesStatusModel.getShow_menu() == 1) {
            llMenu.setVisibility(View.VISIBLE);
        } else {
            llMenu.setVisibility(View.GONE);
        }
        if (modulesStatusModel.getShow_attention() == 1) {
            llAttention.setVisibility(View.VISIBLE);
        } else {
            llAttention.setVisibility(View.GONE);
        }
        if (modulesStatusModel.getShow_busi() == 1) {
            llIntro.setVisibility(View.VISIBLE);
        } else {
            llIntro.setVisibility(View.GONE);
        }
        if (modulesStatusModel.getShow_process() == 1) {
            getActProcess();
        } else {
            llProcess.setVisibility(View.GONE);
        }
        if (!scrollView.isShown()) {
            scrollView.setVisibility(View.VISIBLE);
            scrollView.startAnimation(alphaIn);
        }
    }

    /**
     * 活动更多信息
     */
    private void getActMoreInfo(final boolean isClickUrl) {
        if (isClickUrl) {
            mLoadingDialog.setMessage("加载中...");
            mLoadingDialog.show();
        }
        ActMoreInfoParam param = new ActMoreInfoParam(ActProcess.this, actId);
        HttpStringPost task = new HttpStringPost(ActProcess.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (isClickUrl && mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    actMoreInfoModel = gson.fromJson(object.getString("act_info"), ActMoreInfoModel.class);
                    if (isClickUrl) {
                        Intent intent = new Intent(ActProcess.this, Web.class);
                        intent.putExtra("TITLE", "主办方介绍");
                        intent.putExtra("URL", actMoreInfoModel.getBusi_url());
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    actMoreInfoModel = null;
                    e.printStackTrace();
                }
            }

            @Override
            public void relogin(String msg) {
                if (isClickUrl && mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                if (isClickUrl && mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                actMoreInfoModel = null;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (isClickUrl && mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                actMoreInfoModel = null;
            }
        }, param.getParameters());
        executeRequest(task);
    }

    /**
     * 设置活动流程数据
     */
    private void setActProcess(ArrayList<ActProcessModel> list) {
        adapter.setActProcessList(list);
        llProcess.setVisibility(View.VISIBLE);
    }

    /**
     * 获取活动模块信息
     */
    private void getActModulesStatus() {
        mLoadingDialog.setMessage("加载中...");
        mLoadingDialog.show();
        ActModulesStatusParam param = new ActModulesStatusParam(ActProcess.this, actId);
        HttpStringPost task = new HttpStringPost(ActProcess.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    modulesStatusModel = gson.fromJson(object.getString("act_modules"), ActModulesStatusModel.class);
                    if (modulesStatusModel != null) {
                        setActModulesStatus();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
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
                if (Constant.SHOW_LOG) {
                    toast("获取活动模块信息失败");
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                modulesStatusModel = new ActModulesStatusModel();
                modulesStatusModel.setShow_process(1);
                modulesStatusModel.setShow_attention(1);
                modulesStatusModel.setShow_busi(1);
                modulesStatusModel.setShow_menu(1);
                setActModulesStatus();;
                if (Constant.SHOW_LOG) {
                    toast("获取活动模块信息失败");
                }
            }
        }, param.getParameters());
        executeRequest(task);
    }

    /**
     * 获取活动流程信息
     */
    private void getActProcess() {
        ActProcessParam param = new ActProcessParam(ActProcess.this, actId, 1, 3);
        HttpStringPost task = new HttpStringPost(ActProcess.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                Gson gson = new Gson();
                ActProcessListModel list = gson.fromJson(result, ActProcessListModel.class);
                if (list != null && list.getAct_process().size() > 0) {
                    setActProcess(list.getAct_process());
                } else {
                    llProcess.setVisibility(View.GONE);
                }
            }

            @Override
            public void relogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                if (Constant.SHOW_LOG) {
                    toast("获取活动流程信息失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ArrayList<ActProcessModel> list = new ArrayList<ActProcessModel>();
                for (int i = 0; i < 10; i++) {
                    ActProcessModel model = new ActProcessModel();
                    model.setB_time("20:30");
                    model.setE_time("12:20");
                    model.setStatus(2);
                    model.setSubject("测试数据，真是烦测试数据，真是烦测试数据，真是烦测试数据，真是烦");
                    list.add(model);
                }
                setActProcess(list);
                if (Constant.SHOW_LOG) {
                    toast("获取活动流程信息失败");
                }
            }
        }, param.getParameters());
        executeRequest(task);
    }
}
