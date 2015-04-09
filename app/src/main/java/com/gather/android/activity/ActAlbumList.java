package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.ActAlbumListAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.XListView;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 活动相册列表
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumList extends SwipeBackActivity implements View.OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private XListView listView;
    private ActAlbumListAdapter adapter;

    private DialogTipsBuilder dialog;

    private int actId;
    private ActMoreInfoModel moreInfoModel;
    private GatherApplication application;


    @Override
    protected int layoutResId() {
        return R.layout.act_album_list;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID") && intent.hasExtra("MORE_INFO")) {
            this.actId = intent.getExtras().getInt("ID");
            this.moreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO");
            this.application = GatherApplication.getInstance();
            this.dialog = DialogTipsBuilder.getInstance(ActAlbumList.this);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("相册");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivRight.setImageResource(R.drawable.icon_act_album_add_photo);
            this.ivRight.setPadding(2, 2, 2, 2);
            this.ivLeft.setOnClickListener(this);
            this.ivRight.setOnClickListener(this);

            this.listView = (XListView) findViewById(R.id.listview);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            this.listView.setPullLoadEnable(true);
            this.listView.setPullRefreshEnable(true);
            this.listView.setLoadMoreNeedHigh(metrics.heightPixels);
            this.listView.stopLoadMoreMessageBox();
            this.listView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    adapter.getActAlbumList(actId, application.getCityId());
                }

                @Override
                public void onLoadMore() {
                    adapter.loadMore();
                }
            });

            this.adapter = new ActAlbumListAdapter(ActAlbumList.this);
            this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
                @Override
                public void refreshOver(int code, String msg) {
                    if (moreInfoModel.getEnroll_status() == 3) {
                        ivRight.setVisibility(View.VISIBLE);
                    } else {
                        ivRight.setVisibility(View.GONE);
                    }
                    listView.stopRefresh();
                    switch (code) {
                        case 0:
                            if (msg.equals(SuperAdapter.ISNULL)) {
                                if (adapter.getMsgList().size() == 0) {
                                    listView.setFooterImageView("还没有相册", R.drawable.no_result);
                                } else {
                                    listView.setText("已是全部");
                                }
                            } else {
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
            this.adapter.getActAlbumList(actId, application.getCityId());
        } else {
            toast("加载失败");
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
            case R.id.ivRight:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActAlbumList.this, ActAlbumAddPhoto.class);
                    intent.putExtra("ACT_ID", actId);
                    intent.putExtra("ID", adapter.myAlbumId());
                    startActivityForResult(intent, 500);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 500) {
                listView.onClickRefush();
                adapter.getActAlbumList(actId, application.getCityId());
            }
        }
    }
}
