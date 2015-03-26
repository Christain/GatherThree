package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModulesStatusModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.model.TrendsPicModel;
import com.gather.android.params.ActModulesStatusParam;
import com.gather.android.params.ActMoreInfoParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 活动图示
 * Created by Christain on 2015/3/18.
 */
public class ActShowPic extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llMapGuide, llActLocationPic;
    private LoadingDialog mLoadingDialog;

    private ActModulesStatusModel modulesStatusModel;
    private ActMoreInfoModel actMoreInfoModel;
    private ActModel actModel;
    private int actId;

    @Override
    protected int layoutResId() {
        return R.layout.act_show_pic;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
            this.actModel = (ActModel) intent.getSerializableExtra("ACT_MODEL");
            if (intent.hasExtra("MORE_INFO_MODEL")) {
                this.actMoreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO_MODEL");
            }
            this.mLoadingDialog = LoadingDialog.createDialog(ActShowPic.this, true);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("活动图示");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.llMapGuide = (LinearLayout) findViewById(R.id.llMapGuide);
            this.llMapGuide.setVisibility(View.GONE);
            this.llActLocationPic = (LinearLayout) findViewById(R.id.llActLocationPic);
            this.llActLocationPic.setVisibility(View.GONE);

            this.llMapGuide.setOnClickListener(this);
            this.llActLocationPic.setOnClickListener(this);

            if (intent.hasExtra("MODEL")) {
                this.modulesStatusModel = (ActModulesStatusModel) intent.getSerializableExtra("MODEL");
                setActModulesStatus();
            } else {
                getActModulesStatus();
            }
        } else {
            toast("加载图示错误");
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
            case R.id.llMapGuide:
                if (!ClickUtil.isFastClick() && actModel != null && actModel.getLat() != 0) {
                    Intent intent = new Intent(ActShowPic.this, ActDetailMapLocation.class);
                    intent.putExtra("LAT", actModel.getLat());
                    intent.putExtra("LON", actModel.getLon());
                    startActivity(intent);
                }
                break;
            case R.id.llActLocationPic:
                if (!ClickUtil.isFastClick()) {
                    if (actMoreInfoModel != null) {
                        if (!actMoreInfoModel.getPlace_img_url().equals("")) {
                            ArrayList<TrendsPicModel> list = new ArrayList<TrendsPicModel>();
                            TrendsPicModel picModel = new TrendsPicModel();
                            picModel.setImg_url(actMoreInfoModel.getPlace_img_url());
                            list.add(picModel);
                            Intent intent = new Intent(ActShowPic.this, TrendsPicGallery.class);
                            intent.putExtra("LIST", list);
                            intent.putExtra("POSITION", 0);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            toast("活动没有场地图片");
                        }
                    } else {
                        getActMoreInfo(true);
                    }
                }
                break;
        }
    }

    private void setActModulesStatus() {
        if (modulesStatusModel.getShow_navi() == 1) {
            llMapGuide.setVisibility(View.VISIBLE);
        } else {
            llMapGuide.setVisibility(View.GONE);
        }
        if (modulesStatusModel.getShow_place_img() == 1) {
            llActLocationPic.setVisibility(View.VISIBLE);
        } else {
            llActLocationPic.setVisibility(View.GONE);
        }
    }

    /**
     * 获取活动模块信息
     */
    private void getActModulesStatus() {
        mLoadingDialog.setMessage("加载中...");
        mLoadingDialog.show();
        ActModulesStatusParam param = new ActModulesStatusParam(ActShowPic.this, actId);
        HttpStringPost task = new HttpStringPost(ActShowPic.this, param.getUrl(), new ResponseListener() {
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
                modulesStatusModel.setShow_place_img(1);
                modulesStatusModel.setShow_navi(1);
                if (Constant.SHOW_LOG) {
                    toast("获取活动模块信息失败");
                }
            }
        }, param.getParameters());
        executeRequest(task);
    }

    /**
     * 活动更多信息
     */
    private void getActMoreInfo(final boolean isClick) {
        if (isClick) {
            mLoadingDialog.setMessage("获取中...");
            mLoadingDialog.show();
            ActMoreInfoParam param = new ActMoreInfoParam(ActShowPic.this, actId);
            HttpStringPost task = new HttpStringPost(ActShowPic.this, param.getUrl(), new ResponseListener() {
                @Override
                public void success(int code, String msg, String result) {
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    try {
                        JSONObject object = new JSONObject(result);
                        Gson gson = new Gson();
                        ActMoreInfoModel model = gson.fromJson(object.getString("act_info"), ActMoreInfoModel.class);
                        if (isClick) {
                            ArrayList<TrendsPicModel> list = new ArrayList<TrendsPicModel>();
                            TrendsPicModel picModel = new TrendsPicModel();
                            picModel.setImg_url(model.getPlace_img_url());
                            list.add(picModel);
                            Intent intent = new Intent(ActShowPic.this, TrendsPicGallery.class);
                            intent.putExtra("LIST", list);
                            intent.putExtra("POSITION", 0);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    } catch (JSONException e) {
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
                    toast("获取失败");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    toast("获取失败");
                }
            }, param.getParameters());
            executeRequest(task);
        }
    }
}
