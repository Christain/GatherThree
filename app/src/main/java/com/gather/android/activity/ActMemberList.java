package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.VipListAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActModulesStatusModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.model.VipListModel;
import com.gather.android.params.ActMemeberListParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 活动所有成员
 * Created by Christain on 2015/4/1.
 */
public class ActMemberList extends SwipeBackActivity implements View.OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private GridView gridView;
    private VipListAdapter adapter;

    private TextView tvFooter;
    private RelativeLayout rlFooter;
    private ProgressBar pbFooter;

    private int cityId, actId, totalNum, maxPage, page = 1, size = 18, isOver;
    private String keyWords = "";
    private static final int REFRESH = 0x10;
    private static final int LOADMORE = 0x11;
    private int loadType;
    private boolean isRefresh = false, isKeywords = false;

    private GatherApplication application;
    private LoadingDialog mLoadingDialog;
    private DialogTipsBuilder dialog;

    private ActModulesStatusModel modulesStatusModel;
    private ActMoreInfoModel moreInfoModel;


    @Override
    protected int layoutResId() {
        return R.layout.act_member_list;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID") && intent.hasExtra("MODULE") && intent.hasExtra("MORE_INFO")) {
            this.actId = intent.getExtras().getInt("ID");
            this.modulesStatusModel = (ActModulesStatusModel) intent.getSerializableExtra("MODULE");
            this.moreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO");
            this.application = (GatherApplication) getApplication();
            this.mLoadingDialog = LoadingDialog.createDialog(ActMemberList.this, true);
            this.dialog = DialogTipsBuilder.getInstance(ActMemberList.this);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.VISIBLE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("成员列表");
            this.tvRight.setText("我的小组");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);
            this.tvRight.setOnClickListener(this);

            this.tvFooter = (TextView) findViewById(R.id.tvFooter);
            this.pbFooter = (ProgressBar) findViewById(R.id.pbFooter);
            this.rlFooter = (RelativeLayout) findViewById(R.id.rlFooter);
            this.rlFooter.setOnClickListener(this);

            this.gridView = (GridView) findViewById(R.id.gridView);
            this.adapter = new VipListAdapter(ActMemberList.this);
            this.gridView.setAdapter(adapter);

            this.initView();
        } else {
            toast("加载失败，请重试");
            finish();
        }
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
                    Intent intent = new Intent(ActMemberList.this, ActGroupMemberList.class);
                    intent.putExtra("ID", actId);
                    intent.putExtra("GROUP_ID", moreInfoModel.getGroup_id());
                    startActivity(intent);
                }
                break;
            case R.id.rlFooter:
                if (!ClickUtil.isFastClick() && !isRefresh && isOver != 1) {
                    tvFooter.setVisibility(View.GONE);
                    pbFooter.setVisibility(View.VISIBLE);
                    loadType = LOADMORE;
                    getMemberList();
                }
                break;
        }
    }

    private void initView() {
        if (modulesStatusModel != null) {
            if (modulesStatusModel.getShow_group() == 1 && moreInfoModel.getGroup_id() != -1) {
                this.tvRight.setVisibility(View.VISIBLE);
            } else {
                this.tvRight.setVisibility(View.GONE);
            }
        }
        gridView.setVisibility(View.GONE);
        page = 1;
        isOver = 0;
        isRefresh = true;
        loadType = REFRESH;
        rlFooter.setBackgroundColor(0x00000000);
        pbFooter.setVisibility(View.VISIBLE);
        tvFooter.setVisibility(View.GONE);
        cityId = application.getCityId();
        if (cityId == 0) {
            toast("请先定位您的城市信息");
            finish();
        }
        isKeywords = false;
        getMemberList();
    }

    /**
     * 获取成员列表
     */
    private void getMemberList() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("加载中...");
            mLoadingDialog.show();
        }
        ActMemeberListParam param = new ActMemeberListParam(cityId, actId, page, size);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (!gridView.isShown()) {
                    gridView.setVisibility(View.VISIBLE);
                }
                if (page == 1) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(result);
                        totalNum = object.getInt("total_num");
                        if (totalNum % size == 0) {
                            maxPage = totalNum / size;
                        } else {
                            maxPage = (totalNum / size) + 1;
                        }
                    } catch (JSONException e) {
                        isRefresh = false;
                        e.printStackTrace();
                        return;
                    } finally {
                        object = null;
                    }
                }
                Gson gson = new Gson();
                VipListModel list = gson.fromJson(result, VipListModel.class);
                if (list != null && list.getUsers() != null) {
                    switch (loadType) {
                        case REFRESH:
                            if (totalNum == 0) {
                                isOver = 1;
                                refreshOver(returnCode, "ISNULL");
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(returnCode, "ISOVER");
                            } else {
                                page++;
                                refreshOver(returnCode, "CLICK_MORE");
                            }
                            adapter.refreshItems(list.getUsers());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(returnCode, "CLICK_MORE");
                            } else {
                                isOver = 1;
                                loadMoreOver(returnCode, "ISOVER");
                            }
                            adapter.addItems(list.getUsers());
                            break;
                    }
                } else {
                    switch (loadType) {
                        case REFRESH:
                            refreshOver(returnCode, "ISNULL");
                            break;
                        case LOADMORE:
                            isOver = 1;
                            loadMoreOver(returnCode, "ISOVER");
                            break;
                    }
                }
                isRefresh = false;
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRefresh = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("获取成员失败，请重试").withEffect(Effectstype.Shake).show();
                }
                isRefresh = false;
                errorMessage();
            }
        });
    }

    private void refreshOver(int code, String msg) {
        if (msg.equals("ISNULL")) {
            rlFooter.setVisibility(View.VISIBLE);
            rlFooter.setBackgroundColor(0x00000000);
            pbFooter.setVisibility(View.GONE);
            tvFooter.setVisibility(View.VISIBLE);
            tvFooter.setText("还没有成员");
        } else if (msg.equals("CLICK_MORE")) {
            rlFooter.setVisibility(View.VISIBLE);
            pbFooter.setVisibility(View.GONE);
            tvFooter.setVisibility(View.VISIBLE);
            tvFooter.setText("点击更多");
        } else if (msg.equals("ISOVER")) {
            rlFooter.setVisibility(View.GONE);
        }
    }

    private void loadMoreOver(int code, String msg) {
        if (msg.equals("ISNULL")) {
            rlFooter.setVisibility(View.GONE);
        } else if (msg.equals("CLICK_MORE")) {
            rlFooter.setVisibility(View.VISIBLE);
            pbFooter.setVisibility(View.GONE);
            tvFooter.setVisibility(View.VISIBLE);
            tvFooter.setText("点击更多");
        } else if (msg.equals("ISOVER")) {
            rlFooter.setVisibility(View.GONE);
        }
    }

    private void errorMessage() {
        if (!rlFooter.isShown()) {
            rlFooter.setVisibility(View.VISIBLE);
        }
        rlFooter.setBackgroundColor(0x00000000);
        pbFooter.setVisibility(View.GONE);
        tvFooter.setVisibility(View.VISIBLE);
        tvFooter.setText("点击重试");
    }
}
