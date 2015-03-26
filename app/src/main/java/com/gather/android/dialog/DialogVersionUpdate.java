package com.gather.android.dialog;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.widget.DatePicker;

public class DialogVersionUpdate extends Dialog {

	private View mDialogView;
	private TextView tvTips, tvCancel, tvSure;
	private TextView tvVersionName, tvVersionSize, tvVersionContent;
	private LinearLayout mLinearLayoutView;
	private int mDuration;
	private OnUpdateClickListener listener;

	private Effectstype type = null;

	public DialogVersionUpdate(Context context) {
		super(context);
		init(context);
	}

	public DialogVersionUpdate(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((WindowManager.LayoutParams) params);

	}

	private void init(Context context) {
		mDialogView = View.inflate(context, R.layout.dialog_version_update, null);
		mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
		tvTips = (TextView) mDialogView.findViewById(R.id.tvTips);
		tvCancel = (TextView) mDialogView.findViewById(R.id.tvCancel);
		tvSure = (TextView) mDialogView.findViewById(R.id.tvSure);
		
		this.tvVersionName = (TextView) mDialogView.findViewById(R.id.tvVersionName);
		this.tvVersionSize = (TextView) mDialogView.findViewById(R.id.tvVersionSize);
		this.tvVersionContent = (TextView) mDialogView.findViewById(R.id.tvVersionContent);
		
		setContentView(mDialogView);
		this.setCanceledOnTouchOutside(true);
		this.withDuration(300);
		this.tvVersionSize.setText(Constant.VERSION_SIZE);
		this.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {

				mLinearLayoutView.setVisibility(View.VISIBLE);
				if (type == null) {
					type = Effectstype.Slidetop;
				}
				start(type);

			}
		});
		this.tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		this.tvSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (listener != null) {
					listener.onUpdateListener();
					dismiss();
				}
			}
		});
	}

	public DialogVersionUpdate setTips(CharSequence title) {
		tvTips.setText(title);
		return this;
	}

	public DialogVersionUpdate setVersionName(CharSequence name) {
		tvVersionName.setText(name);
		return this;
	}

	public DialogVersionUpdate setVersionSize(CharSequence size) {
		tvVersionSize.setText(size);
		return this;
	}

	public DialogVersionUpdate setVersionContent(CharSequence content) {
		tvVersionContent.setText(content);
		return this;
	}

	public DialogVersionUpdate setCancel(CharSequence cancel) {
		tvCancel.setText(cancel);
		return this;
	}

	public DialogVersionUpdate setSure(CharSequence sure) {
		tvSure.setText(sure);
		return this;
	}

	public DialogVersionUpdate setOnCancelClick(View.OnClickListener cancelClick) {
		tvSure.setOnClickListener(cancelClick);
		return this;
	}

	public DialogVersionUpdate setOnSureClick(OnUpdateClickListener sureClick) {
		this.listener = sureClick;
		return this;
	}

	public DialogVersionUpdate withDuration(int duration) {
		this.mDuration = duration;
		return this;
	}

	public DialogVersionUpdate withEffect(Effectstype type) {
		this.type = type;
		return this;
	}

	public DialogVersionUpdate isCancelableOnTouchOutside(boolean cancelable) {
		this.setCanceledOnTouchOutside(cancelable);
		return this;
	}

	private void start(Effectstype type) {
		BaseEffects animator = type.getAnimator();
		if (mDuration != -1) {
			animator.setDuration(Math.abs(mDuration));
		}
		animator.start(mLinearLayoutView);
	}

	public interface OnUpdateClickListener {
		public void onUpdateListener();
	}

	public void setOnUpdateListener(OnUpdateClickListener listener) {
		this.listener = listener;
	}

}
