package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.StrategyAndMemoryAndTicketSearchAdapter;
import com.gather.android.adapter.StrategyAndMemoryAndTicketSearchAdapter.OnActDetailClickListener;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.NewsModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.XListView.IXListViewListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 攻略，回忆，票务搜索
 */
public class StrategyAndMemoryAndTicketSearch extends SwipeBackActivity implements OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private EditText etSearch;
    private XListView listView;
    private StrategyAndMemoryAndTicketSearchAdapter adapter;
    private DialogTipsBuilder dialog;
    private GatherApplication application;
    private String keyWords = "";
    private int typeId;

    @Override
    protected int layoutResId() {
        return R.layout.act_search;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("TYPE")) {
            typeId = intent.getExtras().getInt("TYPE");
            this.application = (GatherApplication) getApplication();
            this.dialog = DialogTipsBuilder.getInstance(StrategyAndMemoryAndTicketSearch.this);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.VISIBLE);
            this.ivLeft.setVisibility(View.VISIBLE);
            switch (typeId) {
                case ActStrategyAndMemoryAndTicket.STRATEGY:
                    this.tvTitle.setText("搜索攻略");
                    break;
                case ActStrategyAndMemoryAndTicket.MEMORY:
                    this.tvTitle.setText("搜索记忆");
                    break;
                case ActStrategyAndMemoryAndTicket.TICKET:
                    this.tvTitle.setText("搜索订购");
                    break;
                default:
                    this.tvTitle.setText("搜索");
                    break;
            }
            this.tvRight.setText("确认");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);
            this.tvRight.setOnClickListener(this);

            this.etSearch = (EditText) findViewById(R.id.etSearch);
            this.listView = (XListView) findViewById(R.id.listview);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            this.listView.setPullLoadEnable(true);
            this.listView.setPullRefreshEnable(true);
            this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
            this.listView.stopLoadMoreMessageBox();
            this.listView.setXListViewListener(new IXListViewListener() {
                @Override
                public void onRefresh() {
                    adapter.getSearchList(application.getCityId(), typeId, keyWords);
                }

                @Override
                public void onLoadMore() {
                    adapter.loadMore();
                }
            });

            this.adapter = new StrategyAndMemoryAndTicketSearchAdapter(StrategyAndMemoryAndTicketSearch.this);
            this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
                @Override
                public void refreshOver(int code, String msg) {
                    listView.stopRefresh();
                    switch (code) {
                        case 0:
                            if (msg.equals(SuperAdapter.ISNULL)) {
                                switch (typeId) {
                                    case ActStrategyAndMemoryAndTicket.STRATEGY:
                                        listView.setFooterImageView("还没有这类攻略", R.drawable.no_result);
                                        break;
                                    case ActStrategyAndMemoryAndTicket.MEMORY:
                                        listView.setFooterImageView("还没有这类记忆", R.drawable.no_result);
                                        break;
                                    case ActStrategyAndMemoryAndTicket.TICKET:
                                        listView.setFooterImageView("还没有这类订购", R.drawable.no_result);
                                        break;
                                }
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
            this.adapter.setOnActDetailClickListener(new OnActDetailClickListener() {
                @Override
                public void OnDetailListener(NewsModel model, int position) {
                    if (!ClickUtil.isFastClick()) {
                        Intent intent = new Intent(StrategyAndMemoryAndTicketSearch.this, WebStrategy.class);
                        intent.putExtra("MODEL", model);
                        switch (typeId) {
                            case ActStrategyAndMemoryAndTicket.STRATEGY:
                                intent.putExtra("TITLE", "攻略详情");
                                break;
                            case ActStrategyAndMemoryAndTicket.MEMORY:
                                intent.putExtra("TITLE", "记忆详情");
                                break;
                            case ActStrategyAndMemoryAndTicket.TICKET:
                                intent.putExtra("TITLE", "订购详情");
                                break;
                        }
                        startActivity(intent);
                    }
                }
            });
            this.listView.setAdapter(adapter);
            this.listView.setVisibility(View.GONE);
        } else {
            toast("搜索信息有误");
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
                    if (TextUtils.isEmpty(etSearch.getText().toString().trim())) {
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("请输入关键字").withEffect(Effectstype.Shake).show();
                        }
                        return;
                    }
                    keyWords = etSearch.getText().toString().trim();
                    if (!listView.isShown()) {
                        listView.setVisibility(View.VISIBLE);
                    }
                    adapter.getSearchList(application.getCityId(), typeId, keyWords);
                }
                break;
        }
    }

}
