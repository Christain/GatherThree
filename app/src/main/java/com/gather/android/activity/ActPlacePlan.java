package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.ActPlacePlanAdapter;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 场地平面图
 * Created by Christain on 2015/3/30.
 */
public class ActPlacePlan extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private XListView listView;
    private ActPlacePlanAdapter adapter;
    private int actId;
    private DialogTipsBuilder dialog;

    @Override
    protected int layoutResId() {
        return R.layout.act_place_plan;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            this.actId = intent.getExtras().getInt("ID");
            this.dialog = DialogTipsBuilder.getInstance(ActPlacePlan.this);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("场地平面图");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.listView = (XListView) findViewById(R.id.listview);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            this.listView.setPullLoadEnable(true);
            this.listView.setPullRefreshEnable(true);
            this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
            this.listView.stopLoadMoreMessageBox();
            this.listView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    adapter.getPlaceList(actId);
                }

                @Override
                public void onLoadMore() {
                    adapter.loadMore();
                }
            });

            this.adapter = new ActPlacePlanAdapter(ActPlacePlan.this);
            this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
                @Override
                public void refreshOver(int code, String msg) {
                    listView.stopRefresh();
                    switch (code) {
                        case 0:
                            if (msg.equals(SuperAdapter.ISNULL)) {
                                listView.setFooterImageView("还没有平面图", R.drawable.no_result);
                            } else {
                                listView.setVisibility(View.VISIBLE);
                                listView.setText(msg);
                            }
                            break;
                        case 5:
                            needLogin(msg);
                            break;
                        default:
                            if (dialog != null && !dialog.isShowing()) {
                                dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
                            }
                            break;
                    }
                }
            });
            this.adapter.setLoadMoreOverListener(new OnAdapterLoadMoreOverListener() {
                @Override
                public void loadMoreOver(int code, String msg) {
                    listView.stopLoadMore();
                    switch (code) {
                        case 0:
                            listView.setText(msg);
                            break;
                        case 5:
                            needLogin(msg);
                            break;
                        default:
                            if (dialog != null && !dialog.isShowing()) {
                                dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
                            }
                            break;
                    }
                }
            });
            this.listView.setAdapter(adapter);

            this.listView.onClickRefush();
            this.adapter.getPlaceList(actId);
        } else {
            toast("加载失败，请重试");
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
}
