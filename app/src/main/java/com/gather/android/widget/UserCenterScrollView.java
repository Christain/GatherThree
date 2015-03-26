package com.gather.android.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class UserCenterScrollView extends ScrollView {

	public UserCenterScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initScrollView();
	}

	public UserCenterScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UserCenterScrollView(Context context) {
		this(context, null);
	}

	private void initScrollView() {
		// 设置滚动无阴影( API Level 9 )
		if (Build.VERSION.SDK_INT >= 9) {
			setOverScrollMode(View.OVER_SCROLL_NEVER);
		} else {
			ViewCompat.setOverScrollMode(this, ViewCompat.OVER_SCROLL_NEVER);
		}
	}

	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();

			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}

			break;
		case MotionEvent.ACTION_CANCEL:

		}
		return super.onInterceptTouchEvent(ev);
	}

}
