package com.gather.android.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.ViewFlipper;

import com.gather.android.utils.TouchTool;

public class ListViewImagePro extends ListView {

	private Context mContext;
	private Scroller mScroller;
	TouchTool tool;
	int left, top;
	float startX, startY, currentX, currentY;
	int bgViewH, iv1W;
	int rootW, rootH;
	View headView;
	FrameLayout bgView;
	boolean scrollerType;
	ViewFlipper flipper;
	static final int len = 0xc8;
	private OnRefreshListener listener;

	public ListViewImagePro(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		mScroller = new Scroller(mContext);
		checkSDK();
	}

	public ListViewImagePro(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mScroller = new Scroller(mContext);
		checkSDK();
	}

	public ListViewImagePro(Context context) {
		super(context);
		this.mContext = context;
		mScroller = new Scroller(mContext);
		checkSDK();
	}

	private void checkSDK() {
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) {
			setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}

	public void setHeaderView(View headerView, FrameLayout frameLayout) {
		headView = headerView;
		bgView = frameLayout;
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
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (!mScroller.isFinished()) {
			return super.onTouchEvent(event);
		}
		currentX = event.getX();
		currentY = event.getY();
		headView.getTop();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			left = bgView.getLeft();
			top = bgView.getBottom();
			rootW = getWidth();
			rootH = getHeight();
			bgViewH = bgView.getHeight();
			startX = currentX;
			startY = currentY;
			// Log.e("bb", bgView.getBottom()+"");
			tool = new TouchTool(bgView.getLeft(), bgView.getBottom(), bgView.getLeft(), bgView.getBottom() + len);
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.i("ListView2", "ACTION_MOVE!!!");
			// Log.i("ListView2", "currentX" + currentX);
			// Log.i("ListView2", "currentY" + currentY);
			// Log.i("ListView2", "headView.getTop()=" + headView.getTop());
			// Log.i("ListView2", "headView.isShown()=" + headView.isShown());

			if (headView.isShown() && headView.getTop() >= 0) {
				if (tool != null) {
					int t = tool.getScrollY(currentY - startY);
					if (t >= top && t <= headView.getBottom() + len) {
						bgView.setLayoutParams(new RelativeLayout.LayoutParams(bgView.getWidth(), t));
					}
				}
				scrollerType = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			// Log.i("ListView2", "ACTION_UP!!!");
			// Log.e("aa", bgView.getBottom()+"");
			if (listener != null) {
				if (bgView.getBottom() - top > (int) (top / 3)) {
					listener.OnRefresh();
				}
			}
			scrollerType = true;
			mScroller.startScroll(bgView.getLeft(), bgView.getBottom(), 0 - bgView.getLeft(), bgViewH - bgView.getBottom(), 500);
			invalidate();
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	public interface OnRefreshListener {
		public void OnRefresh();
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.listener = listener;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			bgView.layout(0, 0, x + bgView.getWidth(), y);
			invalidate();
			if (!mScroller.isFinished() && scrollerType && y > len) {
				bgView.setLayoutParams(new RelativeLayout.LayoutParams(bgView.getWidth(), y));
			}
		}
	}
}