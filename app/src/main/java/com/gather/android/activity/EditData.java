package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.utils.MobileUtil;
import com.gather.android.widget.MaxByteEditText;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 编辑个人资料
 */
@SuppressLint("InflateParams")
public class EditData extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private EditText etContent;
	private MaxByteEditText etName;
	private ImageView ivClear;
	private Intent intent;
	private int type;
	private String content = "";
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.edit_data;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		intent = getIntent();
		if (intent.hasExtra("TYPE") && intent.hasExtra("CONTENT")) {
			this.type = intent.getExtras().getInt("TYPE");
			this.content = intent.getStringExtra("CONTENT");
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.VISIBLE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("个人资料");
			this.tvRight.setText("确定");
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			this.tvRight.setOnClickListener(this);

			this.dialog = DialogTipsBuilder.getInstance(EditData.this);
			this.etContent = (EditText) findViewById(R.id.etContent);
			this.etName = (MaxByteEditText) findViewById(R.id.etName);
			this.ivClear = (ImageView) findViewById(R.id.ivClear);

			this.ivClear.setOnClickListener(this);

			this.initView();
		} else {
			finish();
			toast("信息不全~~");
		}
	}

	private void initView() {
		if (content.equals("未填写")) {
			content = "";
		}
		if (type == UserInfo.REQUEST_NICK_NAME) {
			etContent.setVisibility(View.GONE);
			etName.setVisibility(View.VISIBLE);
			etName.setText(content);
			Selection.setSelection(etName.getText(), etName.getText().length());
		} else {
			etContent.setVisibility(View.VISIBLE);
			etName.setVisibility(View.GONE);
			etContent.setText(content);
			Selection.setSelection(etContent.getText(), etContent.getText().length());
		}
		if (content.length() == 0) {
			ivClear.setVisibility(View.GONE);
		} else {
			ivClear.setVisibility(View.VISIBLE);
		}
		switch (type) {
		case UserInfo.REQUEST_NICK_NAME:
			tvTitle.setText("修改昵称");
			etName.setHint("请输入你的昵称（中英文开头4-20字符）");
			etName.setMaxByteLength(20);
			break;
		case UserInfo.REQUEST_USER_NAME:
			etContent.addTextChangedListener(mTextWatcher);
			tvTitle.setText("姓名");
			etContent.setHint("您的真实姓名？");
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
			break;
		case UserInfo.REQUEST_CONTACT_PHONE:
			etContent.addTextChangedListener(mTextWatcher);
			tvTitle.setText("联系电话");
			etContent.setHint("用户接收最新的活动通知及中奖通知");
			etContent.setInputType(InputType.TYPE_CLASS_NUMBER);
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
			break;
		case UserInfo.REQUEST_CONTACT_ADDRESS:
			etContent.addTextChangedListener(mTextWatcher);
			tvTitle.setText("通讯地址");
			etContent.setHint("请输入准确的联系地址");
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
			break;
		case UserInfo.REQUEST_USER_BRIEF:
			etContent.addTextChangedListener(mTextWatcher);
			etContent.setMinHeight(500);
			etContent.setGravity(Gravity.TOP | Gravity.LEFT);
			tvTitle.setText("个性签名");
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });
			break;
		case UserInfo.REQUEST_USER_LOVE:
			etContent.addTextChangedListener(mTextWatcher);
			tvTitle.setText("爱好");
			etContent.setHint("请输入您的爱好（20个字符内）");
			etContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvRight:
			switch (type) {
			case UserInfo.REQUEST_NICK_NAME:
				if (TextUtils.isEmpty(etName.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("昵称不能为空").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				if (etName.getText().toString().trim().length() < 4) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("昵称不能少于4个字符").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("CONTENT", etName.getText().toString().trim());
				setResult(RESULT_OK, intent);
				finish();
				break;
			case UserInfo.REQUEST_USER_NAME:
				if (checkNameChese(etContent.getText().toString().trim())) {
					Intent name = new Intent();
					if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
						name.putExtra("CONTENT", "");
					} else {
						name.putExtra("CONTENT", etContent.getText().toString().trim());
					}
					setResult(RESULT_OK, name);
					finish();
				} else {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("姓名只能输入中文").withEffect(Effectstype.Shake).show();
					}
					return;
				}
				break;
			case UserInfo.REQUEST_CONTACT_PHONE:
				if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
					if (!MobileUtil.execute(etContent.getText().toString().trim().replace(" ", "")).equals("未知")) {
						Intent phone = new Intent();
						phone.putExtra("CONTENT", etContent.getText().toString().trim().replace(" ", ""));
						setResult(RESULT_OK, phone);
						finish();
					} else {
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
						}
						return;
					}
				} else {
					Intent phone = new Intent();
					phone.putExtra("CONTENT", etContent.getText().toString().trim());
					setResult(RESULT_OK, phone);
					finish();
				}
				break;
			case UserInfo.REQUEST_CONTACT_ADDRESS:
				Intent address = new Intent();
				address.putExtra("CONTENT", etContent.getText().toString().trim());
				setResult(RESULT_OK, address);
				finish();
				break;
			case UserInfo.REQUEST_USER_BRIEF:
				Intent brief = new Intent();
				brief.putExtra("CONTENT", etContent.getText().toString().trim());
				setResult(RESULT_OK, brief);
				finish();
				break;
			case UserInfo.REQUEST_USER_LOVE:
				Intent love = new Intent();
				love.putExtra("CONTENT", etContent.getText().toString().trim());
				setResult(RESULT_OK, love);
				finish();
				break;
			}
			break;
		case R.id.ivClear:
			if (type == UserInfo.REQUEST_NICK_NAME) {
				etName.setText("");
			} else {
				etContent.setText("");
			}
			break;
		}
	}

	private TextWatcher mTextWatcher = new TextWatcher() { // 字数监听
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				ivClear.setVisibility(View.GONE);
			} else {
				if (!ivClear.isShown()) {
					ivClear.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}
	};

	/**
	 * 判定输入汉字
	 * 
	 * @param c
	 * @return
	 */
	public boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 检测String是否全是中文
	 * 
	 * @param name
	 * @return
	 */
	public boolean checkNameChese(String name) {
		boolean res = true;
		char[] cTemp = name.toCharArray();
		for (int i = 0; i < name.length(); i++) {
			if (!isChinese(cTemp[i])) {
				res = false;
				break;
			}
		}
		return res;
	}
}
