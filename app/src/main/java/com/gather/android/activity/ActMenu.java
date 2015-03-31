package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActMenuModelList;
import com.gather.android.params.ActMenuParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 活动菜单
 * Created by Christain on 2015/3/30.
 */
public class ActMenu extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llLunch, llDinner, llLunchMenu, llDinnerMenu;
    private TextView tvLunchTime, tvDinnerTime;
    private ScrollView scrollView;

    private Animation alphaIn;

    private int actId;
    private LoadingDialog mLoadingDialog;

    @Override
    protected int layoutResId() {
        return R.layout.act_menu;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
        }
        this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        this.mLoadingDialog = LoadingDialog.createDialog(ActMenu.this, true);
        this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
        this.ivRight = (ImageView) findViewById(R.id.ivRight);
        this.tvLeft = (TextView) findViewById(R.id.tvLeft);
        this.tvTitle = (TextView) findViewById(R.id.tvTitle);
        this.tvRight = (TextView) findViewById(R.id.tvRight);
        this.tvLeft.setVisibility(View.GONE);
        this.ivRight.setVisibility(View.GONE);
        this.tvRight.setVisibility(View.GONE);
        this.ivLeft.setVisibility(View.VISIBLE);
        this.tvTitle.setText("宴会菜单");
        this.ivLeft.setImageResource(R.drawable.title_back_click_style);
        this.ivLeft.setOnClickListener(this);

        this.scrollView = (ScrollView) findViewById(R.id.scrollView);
        this.llLunch = (LinearLayout) findViewById(R.id.llLunch);
        this.llDinner = (LinearLayout) findViewById(R.id.llDinner);
        this.llLunchMenu = (LinearLayout) findViewById(R.id.llLunchMenu);
        this.llDinnerMenu = (LinearLayout) findViewById(R.id.llDinnerMenu);
        this.tvLunchTime = (TextView) findViewById(R.id.tvLunchTime);
        this.tvDinnerTime = (TextView) findViewById(R.id.tvDinnerTime);

        this.scrollView.setVisibility(View.INVISIBLE);

        getLunchMenu();
    }

    /**
     * 午餐菜单
     */
    private void getLunchMenu() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("加载中");
            mLoadingDialog.show();
        }
        ActMenuParam param = new ActMenuParam(ActMenu.this, actId, 1);
        HttpStringPost task = new HttpStringPost(ActMenu.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String time = TimeUtil.getActDetailTime(object.getString("time"));
                    String address = object.getString("addr");
                    tvLunchTime.setText(time + " " + address);
                    Gson gson = new Gson();
                    ActMenuModelList list = gson.fromJson(result, ActMenuModelList.class);
                    if (list != null && list.getAct_menus().size() > 0) {
                        int padding = getResources().getDimensionPixelOffset(R.dimen.act_menu_text_padding);
                        for (int i = 0; i < list.getAct_menus().size(); i++) {
                            TextView textView = new TextView(ActMenu.this);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText(list.getAct_menus().get(i).getSubject());
                            textView.setTextColor(0xFF6C7379);
                            textView.setTextSize(17);
                            textView.setPadding(padding,padding,padding,padding);
                            llLunch.addView(textView);
                        }
                        getDinner();
                    } else {
                        toast("没有菜单...");
                        finish();
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
                toast("加载失败...");
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("加载失败...");
                finish();
            }
        }, param.getParameters());
        executeRequest(task);
    }

    /**
     * 晚餐列表
     */
    private void getDinner() {
        ActMenuParam param = new ActMenuParam(ActMenu.this, actId, 2);
        HttpStringPost task = new HttpStringPost(ActMenu.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(result);
                    String time = TimeUtil.getActDetailTime(object.getString("time"));
                    String address = object.getString("addr");
                    tvDinnerTime.setText(time + " " + address);
                    Gson gson = new Gson();
                    ActMenuModelList list = gson.fromJson(result, ActMenuModelList.class);
                    if (list != null && list.getAct_menus().size() > 0) {
                        int padding = getResources().getDimensionPixelOffset(R.dimen.act_menu_text_padding);
                        for (int i = 0; i < list.getAct_menus().size(); i++) {
                            TextView textView = new TextView(ActMenu.this);
                            textView.setText(list.getAct_menus().get(i).getSubject());
                            textView.setTextColor(0xFF6C7379);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextSize(17);
                            textView.setPadding(padding,padding,padding,padding);
                            llDinner.addView(textView);
                        }
                        if (scrollView != null && !scrollView.isShown()) {
                            scrollView.startAnimation(alphaIn);
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        toast("没有菜单...");
                        finish();
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
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
