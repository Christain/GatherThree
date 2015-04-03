package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.ActAlbumDetailListAdapter;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.ActAlbumContentModel;
import com.gather.android.model.ActAlbumDetailModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.PullToRefreshLayout;
import com.gather.android.widget.PullableGridView;
import com.gather.android.widget.swipeback.SwipeBackActivity;

import java.util.ArrayList;

/**
 * 活动某一相册全部图片列表
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumDetailList extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private PullToRefreshLayout refreshLayout;
    private PullableGridView gridView;
    private ActAlbumDetailListAdapter adapter;
    private DialogTipsBuilder dialog;

    private ActAlbumContentModel model;
    private int albumId, actId;
    private boolean isMe = false;

    @Override
    protected int layoutResId() {
        return R.layout.act_album_detail_list;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("MODEL") && intent.hasExtra("ACT_ID")) {
            this.model = (ActAlbumContentModel) intent.getSerializableExtra("MODEL");
            this.actId = intent.getExtras().getInt("ACT_ID");
            this.albumId = model.getId();
            this.dialog = DialogTipsBuilder.getInstance(ActAlbumDetailList.this);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            if (model.isOwner()) {
                if (model.getType() == 1) {
                    this.tvTitle.setText("主办方的相册");
                } else {
                    this.tvTitle.setText("主办方的视频");
                }
            } else {
                this.tvTitle.setText(model.getUser().getNick_name() + "的相册");
            }
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            if (intent.hasExtra("IS_ME")) {
                this.isMe = true;
                this.ivRight.setVisibility(View.VISIBLE);
                this.tvTitle.setText("我的相册");
                this.ivRight.setImageResource(R.drawable.icon_act_album_add_photo);
                this.ivRight.setOnClickListener(this);
            } else {
                this.ivRight.setVisibility(View.GONE);
                this.isMe = false;
            }

            this.refreshLayout = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
            this.gridView = (PullableGridView) findViewById(R.id.content_view);
            this.adapter = new ActAlbumDetailListAdapter(ActAlbumDetailList.this);
            this.gridView.setAdapter(adapter);

            this.refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                    adapter.getALbumList(albumId);
                }

                @Override
                public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                    adapter.loadMore();
                }
            });
            this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
                @Override
                public void refreshOver(int code, String msg) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    switch (code) {
                        case 0:

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
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    switch (code) {
                        case 0:

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
            this.adapter.getALbumList(albumId);
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
                    Intent intent = new Intent(ActAlbumDetailList.this, ActAlbumAddPhoto.class);
                    intent.putExtra("LIST", (ArrayList<ActAlbumDetailModel>) adapter.getMsgList());
                    intent.putExtra("ID", albumId);
                    intent.putExtra("ACT_ID", actId);
                    startActivity(intent);
                }
                break;
        }
    }

}
