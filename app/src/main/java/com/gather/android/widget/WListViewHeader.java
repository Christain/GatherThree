package com.gather.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.gather.android.R;

public class WListViewHeader extends LinearLayout{

	private LinearLayout mContainer;
	private int mState = STATE_NORMAL;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	private boolean isPullLoadMore = false;

	public WListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public WListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.wlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);
	}

	public void setState(int state) {
		if (state == mState)
			return;

		if (state == STATE_REFRESHING) { // 显示进度
			
		} else { // 显示箭头图片
			
		}

		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				
			}
			if (mState == STATE_REFRESHING) {
				
			}
			
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				
				if (isPullLoadMore) {
					
				} else {
					
				}
			}
			break;
		case STATE_REFRESHING:
			
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

	// 下拉提示加载更多
	public void pullLoadMore() {
		isPullLoadMore = true;
	}
}
