package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModulesStatusModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 活动图示
 * Created by Christain on 2015/3/18.
 */
public class ActShowPic extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llMapGuide, llActLocationPic, llMatchMap;
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
        if (intent.hasExtra("ID") && intent.hasExtra("MODEL") && intent.hasExtra("MODULE") && intent.hasExtra("MORE_INFO")) {
            this.actId = intent.getExtras().getInt("ID");
            this.actModel = (ActModel) intent.getSerializableExtra("MODEL");
            this.actMoreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO");
            this.modulesStatusModel = (ActModulesStatusModel) intent.getSerializableExtra("MODULE");
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
            this.llMatchMap = (LinearLayout) findViewById(R.id.llMatchMap);
            this.llMatchMap.setVisibility(View.GONE);

            this.llMapGuide.setOnClickListener(this);
            this.llActLocationPic.setOnClickListener(this);
            this.llMatchMap.setOnClickListener(this);

            setActModulesStatus();
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
                if (!ClickUtil.isFastClick() && actId != 0) {
                    Intent intent = new Intent(ActShowPic.this, ActLocationAndCarPlace.class);
                    intent.putExtra("ID", actId);
                    startActivity(intent);
                }
                break;
            case R.id.llActLocationPic:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActShowPic.this, ActPlacePlan.class);
                    intent.putExtra("ID", actId);
                    startActivity(intent);
                }
                break;
            case R.id.llMatchMap:
                if (!ClickUtil.isFastClick()) {

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
        if (modulesStatusModel.getShow_route_map() == 1) {
            llMatchMap.setVisibility(View.VISIBLE);
        } else {
            llMatchMap.setVisibility(View.GONE);
        }
    }
}
