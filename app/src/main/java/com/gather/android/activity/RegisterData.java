package com.gather.android.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogDateSelect;
import com.gather.android.dialog.DialogDateSelect.OnDateClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.model.RegisterDataModel;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.gather.android.widget.swipeback.SwipeBackLayout;

@SuppressLint("InflateParams")
public class RegisterData extends SwipeBackActivity implements OnClickListener {

	private TextView tvNext;
	private DialogTipsBuilder dialog;
	private RegisterDataModel model;

	private EditText etNickName, etPassword;
	private ImageView ivFemaleSmall, ivFemaleBig, ivMaleBig, ivMaleSmall;
	private LinearLayout llSexMale, llSexFemale;
	private LinearLayout llBirthday;
	private View PasswordLine;
	private TextView tvBirthday;
	private String birth = "";
	private int sex; // 1男，2女
	private int type;

	@Override
	protected int layoutResId() {
		return R.layout.register_data;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("TYPE")) {
			type = intent.getExtras().getInt("TYPE");
			
			SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
			mSwipeBackLayout.setEnableGesture(false);
			this.model = new RegisterDataModel();
			this.dialog = DialogTipsBuilder.getInstance(RegisterData.this);
			this.etNickName = (EditText) findViewById(R.id.etNickName);
			this.PasswordLine = (View) findViewById(R.id.PasswordLine);
			this.etPassword = (EditText) findViewById(R.id.etPassword);
			if (type == AppPreference.TYPE_SELF) {
				etPassword.setVisibility(View.VISIBLE);
				PasswordLine.setVisibility(View.VISIBLE);
			} else {
				etPassword.setVisibility(View.GONE);
				PasswordLine.setVisibility(View.GONE);
			}
			this.tvNext = (TextView) findViewById(R.id.tvNext);
			this.llSexMale = (LinearLayout) findViewById(R.id.llSexMale);
			this.llSexFemale = (LinearLayout) findViewById(R.id.llSexFemale);
			this.ivFemaleSmall = (ImageView) findViewById(R.id.ivFemaleSmall);
			this.ivFemaleBig = (ImageView) findViewById(R.id.ivFemaleBig);
			this.ivMaleBig = (ImageView) findViewById(R.id.ivMaleBig);
			this.ivMaleSmall = (ImageView) findViewById(R.id.ivMaleSmall);
			this.llBirthday = (LinearLayout) findViewById(R.id.llBirthday);
			this.tvBirthday = (TextView) findViewById(R.id.tvBirthday);
			
			this.tvNext.setOnClickListener(this);
			this.llBirthday.setOnClickListener(this);
			this.llSexFemale.setOnClickListener(this);
			this.llSexMale.setOnClickListener(this);
			
			this.init();
		} else {
			finish();
			toast("登录类型错误");
		}
	}

	private void init() {
		ivMaleBig.setVisibility(View.GONE);
		ivMaleSmall.setVisibility(View.VISIBLE);
		ivFemaleBig.setVisibility(View.VISIBLE);
		ivFemaleSmall.setVisibility(View.GONE);
		sex = 2;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			DialogChoiceBuilder dialogChoice = DialogChoiceBuilder.getInstance(RegisterData.this);
			dialogChoice.setMessage("您确定要退出吗？").withDuration(300).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			}).show();
			break;
		case R.id.tvNext:
			if (TextUtils.isEmpty(etNickName.getText().toString().trim())) {
				dialog.setMessage("请输入您的昵称").withEffect(Effectstype.Shake).show();
				return;
			}
			if (type == AppPreference.TYPE_SELF) {
				int passwordLength = etPassword.getText().toString().trim().length();
				if (passwordLength == 0) {
					dialog.setMessage("请输入密码").withEffect(Effectstype.Shake).show();
					return;
				}
				if (passwordLength < 6 || passwordLength > 16) {
					dialog.setMessage("密码长度应该在6~16之间").withEffect(Effectstype.Shake).show();
					return;
				}
			}
			if (birth.length() < 2) {
				dialog.setMessage("请选择您的生日").withEffect(Effectstype.Shake).show();
				return;
			}
			model.setNickname(etNickName.getText().toString().trim());
			model.setPassword(etPassword.getText().toString().trim());
			model.setSex(sex);
			model.setBirthday(birth);
			Intent intent = new Intent(RegisterData.this, RegisterIcon.class);
			intent.putExtra("MODEL", model);
			intent.putExtra("TYPE", type);
			startActivityForResult(intent, 100);
			break;
		case R.id.llBirthday:
			DialogDateSelect dialogDate = new DialogDateSelect(RegisterData.this, R.style.dialog_date);
			dialogDate.withDuration(300).withEffect(Effectstype.Fall).setOnSureClick(new OnDateClickListener() {
				@Override
				public void onDateListener(String date) {
					if (TimeUtil.getUserAge(date) == -1) {
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("年龄选择错误").withEffect(Effectstype.Shake).show();
						}
					} else {
						birth = date;
						tvBirthday.setText(date+"");
					}
				}
			}).show();
			break;
		case R.id.llSexMale:
			if (sex != 1) {
				sex = 1;
				ivMaleBig.setVisibility(View.VISIBLE);
				ivMaleSmall.setVisibility(View.GONE);
				ivFemaleBig.setVisibility(View.GONE);
				ivFemaleSmall.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.llSexFemale:
			if (sex != 2) {
				sex = 2;
				ivMaleBig.setVisibility(View.GONE);
				ivMaleSmall.setVisibility(View.VISIBLE);
				ivFemaleBig.setVisibility(View.VISIBLE);
				ivFemaleSmall.setVisibility(View.GONE);
			}
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 100:
				if (data != null && data.hasExtra("MODEL")) {
					model = (RegisterDataModel) data.getSerializableExtra("MODEL");
				}
				break;
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(RegisterData.this);
			dialog.setMessage("您确定到退出吗？").withDuration(300).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			}).show();
		}
		return true;
	}

	/**
	 * 判断邮箱格式
	 * @param email
	 * @return
	 */
	private boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

}
