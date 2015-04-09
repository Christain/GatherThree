package com.gather.android.baseclass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.activity.LoginIndex;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.listener.OnAdapterLoadMoreOverListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.preference.AppPreference;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class SuperAdapter extends BaseAdapter {
	protected static final int REFRESH = 1;
	protected static final int LOADMORE = 2;
	public static final String ISOVER = "已是全部";
	public static final String ISNULL = "NO_CONTENT";
	public static final String ISERROR = "ERROR_DATA";
	public static final String CLICK_MORE = "点击更多";
	public static final String CACHE = "没有了";
	protected int loadType;
	private OnAdapterRefreshOverListener refreshOverListener;
	private OnAdapterLoadMoreOverListener loadMoreOverListener;
	protected boolean isRequest;
	private List msgList;
	protected Context context;
	protected DialogTipsBuilder loginDialog;
	protected LayoutInflater mInflater;

	public SuperAdapter(Context context) {
		this.context = context;
		this.msgList = new ArrayList<Object>();
		this.isRequest = false;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.loginDialog = DialogTipsBuilder.getInstance(context);
		this.loginDialog.setCanceledOnTouchOutside(false);
	}

	public boolean isRequest() {
		return isRequest;
	}
	
	public ArrayList<Object> getList(){
		return (ArrayList<Object>) msgList;
	}

	public void setRefreshOverListener(OnAdapterRefreshOverListener listener) {
		this.refreshOverListener = listener;
	}

	public void refreshOver(int code, String msg) {
		if (refreshOverListener != null) {
			refreshOverListener.refreshOver(code, msg);
		}
	}

	public void setLoadMoreOverListener(OnAdapterLoadMoreOverListener listener) {
		this.loadMoreOverListener = listener;
	}

	public void loadMoreOver(int code, String msg) {
		if (loadMoreOverListener != null) {
			loadMoreOverListener.loadMoreOver(code, msg);
		}
	}

	public List getMsgList() {
		return msgList;
	}

	public Context getContext() {
		return this.context;
	}

	public int getCount() {
		return this.msgList.size();
	}

	public Object getItem(int position) {
		if (position >= 0 && position < getCount()) {
			return this.msgList.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void clearList() {
		msgList.clear();
		notifyDataSetChanged();
	}

	public void refreshItems(List items) {
		msgList.clear();
		if (items != null) {
			msgList.addAll(items);
		}
		notifyDataSetChanged();
	}

	public void addItems(List items) {
		if (items != null) {
			msgList.addAll(getCount(), items);
		}
		notifyDataSetChanged();
	}
	
	public void addItemsNoChanged(List items) {
		if (items != null) {
			msgList.addAll(getCount(), items);
		}
//		notifyDataSetChanged();
	}

	public void addItemsInFront(List items) {
		if (items != null) {
			msgList.addAll(0, items);
		}
		notifyDataSetChanged();
	}

	public void addItem(Object item) {
		if (item != null) {
			msgList.add(item);
		}
		notifyDataSetChanged();
	}

	public void addItem(Object item, int position) {
		if (item != null) {
			msgList.add(position, item);
		}
		notifyDataSetChanged();
	}

	public boolean removeItem(int position) {
		if (position >= 0 && position < getCount()) {
			msgList.remove(position);
			notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public boolean remove(int position) {
		if (position >= 0 && position < getCount()) {
			msgList.remove(position);
			return true;
		}
		return false;
	}
	
	public void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	

	/**
	 * 重新登录
	 */
	protected void needLogin(String msg){
		if (loginDialog != null && !loginDialog.isShowing()) {
			loginDialog.setCanceledOnTouchOutside(false);
			loginDialog.withDuration(300).withEffect(Effectstype.Fadein).setMessage(msg).isCancelableOnTouchOutside(false).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (PushManager.isPushEnabled(getContext())) {
						PushManager.stopWork(getContext());
					}
					GatherApplication application = (GatherApplication) context.getApplicationContext();
					application.setUserInfoModel(null);
					AppPreference.clearInfo(context);
					Intent intent = new Intent(context, LoginIndex.class);
					context.startActivity(intent);
				}
			}).setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
						return true;
					} else {
						return false;
					}
				}
			});
			loginDialog.show();
		}
	}
	
	/**
	 * 网络检查
	 */
	protected void NetWorkDialog() {
		final DialogTipsBuilder netDialog = DialogTipsBuilder.getInstance(context);
		if (netDialog != null && !netDialog.isShowing()) {
			netDialog.setMessage("最遥远的距离就是没网，请检查网络").withEffect(Effectstype.Shake).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					netDialog.dismiss();
					Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
					context.startActivity(intent);
				}
			}).show();
		}
	}

	public abstract void refresh();

	public abstract void loadMore();
}
