package com.gather.android.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.gather.android.R;

public class ChatListView extends ListView implements OnScrollListener {

	private float mLastY = -1;
	private Scroller mScroller;
	private OnScrollListener mScrollListener;
	private ChangeListViewBgListener mChangeListViewBgListener;
	private IXListViewListener mListViewListener;

	private ChatListViewHeader mHeaderView;
	private RelativeLayout mHeaderViewContent;
	private int mHeaderViewHeight;
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false;
	private boolean isRefresh = false; // 判断是上拉还是下拉

	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;

	private final static int SCROLL_DURATION = 400;// 回弹的时间
	private final static float OFFSET_RADIO = 2.5f;
	static final int PULL_TO_REFRESH = 0x0;
	static final int RELEASE_TO_REFRESH = 0x1;
	static final int REFRESHING = 0x2;
	static final int MANUAL_REFRESHING = 0x3;

	/**
	 * @param context
	 */
	public ChatListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public ChatListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public ChatListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);

		mHeaderView = new ChatListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);		
//		addHeaderView(mHeaderView);

		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				mHeaderViewHeight = mHeaderViewContent.getHeight();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		checkSDK();
	}

	private void checkSDK() {// 去掉顶部或底部的黄色
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) {
			setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		addHeaderView(mHeaderView);
		super.setAdapter(adapter);
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) {
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}


	// private void invokeOnScrolling() {
	// if (mScrollListener instanceof OnXScrollListener) {
	// OnXScrollListener l = (OnXScrollListener) mScrollListener;
	// l.onXScrolling(this);
	// }
	// }

	private void updateHeaderHeight(float delta) {
		if (mChangeListViewBgListener != null) {
			mChangeListViewBgListener.refreshBackground();
		}
		mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		if (mLastY == -1) {
//			mLastY = ev.getRawY();
//		}
		switch (ev.getAction()& MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mLastY == -1) {
				mLastY = ev.getRawY();
				break;
			}
			final float deltaY = ev.getRawY()  - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				isRefresh = true;
				up(deltaY);
			} 
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mLastY = -1;
			break;
		case MotionEvent.ACTION_UP:
			mLastY = -1; // reset
			if (isRefresh) {
				if (getFirstVisiblePosition() == 0) {
					refush();
				}
			} 
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void up(float deltaY) {
		updateHeaderHeight(deltaY / OFFSET_RADIO);
	}

	public void refush() {
		if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
			mPullRefreshing = true;
			mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
			if (mListViewListener != null) {
				mListViewListener.onRefresh();
			}
		}
		resetHeaderHeight();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} 
			postInvalidate();
		}
		super.computeScroll();
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	public interface IXListViewListener {
		public void onRefresh();
	}

	public interface ChangeListViewBgListener { // 为了改变刷新时ListView背景的颜色
		public void refreshBackground();

		public void loadmoreBackground();
	}

	public void setChangeListViewBgListener(ChangeListViewBgListener l) {
		mChangeListViewBgListener = l;
	}

	public void onClickRefush() {
		up((int)getResources().getDimension(R.dimen.header_height) * OFFSET_RADIO);
		mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
		mPullRefreshing = true;
		resetHeaderHeight();
	}

	// 下拉提示加载更多
	public void pullLoadMore() {
		mHeaderView.pullLoadMore();
	}

}
