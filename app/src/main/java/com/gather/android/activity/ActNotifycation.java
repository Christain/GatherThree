package com.gather.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 活动通知
 * Created by Christain on 2015/3/18.
 */
public class ActNotifycation extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llEditText;
    private EditText etContent;
    private Button btResult;
    private ListView listView;

    @Override
    protected int layoutResId() {
        return R.layout.act_notifycation;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
        this.ivRight = (ImageView) findViewById(R.id.ivRight);
        this.tvLeft = (TextView) findViewById(R.id.tvLeft);
        this.tvTitle = (TextView) findViewById(R.id.tvTitle);
        this.tvRight = (TextView) findViewById(R.id.tvRight);
        this.tvLeft.setVisibility(View.GONE);
        this.ivRight.setVisibility(View.VISIBLE);
        this.tvRight.setVisibility(View.GONE);
        this.ivLeft.setVisibility(View.VISIBLE);
        this.tvTitle.setText("通知");
        this.ivLeft.setImageResource(R.drawable.title_back_click_style);
        this.ivLeft.setOnClickListener(this);

        this.llEditText = (LinearLayout) findViewById(R.id.llEditText);
        this.etContent = (EditText) findViewById(R.id.etContent);
        this.btResult = (Button) findViewById(R.id.btResult);
        this.listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLeft:
                if (!ClickUtil.isFastClick()) {
                    finish();
                }
                break;
        }
    }
}
