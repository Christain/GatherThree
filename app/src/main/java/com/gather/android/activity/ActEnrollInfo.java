package com.gather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.ActEnrollInfoAdapter;
import com.gather.android.dialog.DialogDateSelect;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActEnrollCustomKeyModel;
import com.gather.android.model.ActEnrollCustomKeyModelList;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.params.ActEnrollParam;
import com.gather.android.params.EnrollCustomKeyParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.MobileUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * 活动报名填写
 * Created by Christain on 2015/3/31.
 */
public class ActEnrollInfo extends SwipeBackActivity implements View.OnClickListener{

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private ListView listView;
    private ActEnrollInfoAdapter adapter;

    private View headerView, footerView;
    private EditText etUserName, etUserPhone, etUserNum;
    private LinearLayout llUserNum;
    private TextView tvBirth, tvUserSex, tvEnroll;
    private Animation alphaIn;

    private int actId;
    private boolean hasCustomKey = false;
    private ActMoreInfoModel actMoreInfoModel;
    private LoadingDialog mLoadingDialog;
    private String userName, birth, userPhone;
    private int userSex;

    private DialogTipsBuilder dialog;

    @Override
    protected int layoutResId() {
        return R.layout.act_enroll_info;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ID") && intent.hasExtra("MORE_INFO")) {
            this.actId = intent.getExtras().getInt("ID");
            this.actMoreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO");
            this.mLoadingDialog = LoadingDialog.createDialog(ActEnrollInfo.this, true);
            this.dialog = DialogTipsBuilder.getInstance(ActEnrollInfo.this);
            this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("报名表");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.listView = (ListView) findViewById(R.id.listview);
            this.listView.requestFocus();
            LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.headerView = mInflater.inflate(R.layout.act_enroll_info_header, null);
            this.footerView = mInflater.inflate(R.layout.act_enroll_info_footer, null);
            this.etUserName = (EditText) headerView.findViewById(R.id.etUserName);
            this.etUserPhone = (EditText) headerView.findViewById(R.id.etUserPhone);
            this.etUserPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
            this.etUserPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            this.tvBirth = (TextView) headerView.findViewById(R.id.tvBirth);
            this.tvUserSex = (TextView) headerView.findViewById(R.id.tvUserSex);
            this.llUserNum = (LinearLayout) footerView.findViewById(R.id.llUserNum);
            this.etUserNum = (EditText) footerView.findViewById(R.id.etUserNum);
            this.tvEnroll = (TextView) footerView.findViewById(R.id.tvEnroll);
            this.listView.addHeaderView(headerView);
            this.listView.addFooterView(footerView);
            this.adapter = new ActEnrollInfoAdapter(ActEnrollInfo.this);
            this.listView.setAdapter(adapter);

            this.listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                    EditText editText = (EditText) view.findViewById(R.id.etContent);
                    editText.requestFocus();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    listView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                }
            });

            this.tvBirth.setOnClickListener(this);
            this.tvEnroll.setOnClickListener(this);

            this.initView();
            getCustomKey();
        } else {
            toast("加载报名表失败");
            finish();
        }
    }

    private void initView() {
        this.listView.setVisibility(View.INVISIBLE);
        if (actMoreInfoModel != null) {
            if (actMoreInfoModel.getCan_with_people() == 1) {
                llUserNum.setVisibility(View.VISIBLE);
            } else {
                llUserNum.setVisibility(View.GONE);
            }
        }
        userName = AppPreference.getUserPersistent(ActEnrollInfo.this, AppPreference.REAL_NAME);
        birth = AppPreference.getUserPersistent(ActEnrollInfo.this, AppPreference.USER_BIRTHDAY);
        userSex = AppPreference.getUserPersistentInt(ActEnrollInfo.this, AppPreference.USER_SEX);
        userPhone = AppPreference.getUserPersistent(ActEnrollInfo.this, AppPreference.CONTACT_PHONE);
        etUserName.setText(userName);
        tvBirth.setText(TimeUtil.getUserbirthString(birth));
        if (userSex == 1) {
            tvUserSex.setText("男");
        } else {
            tvUserSex.setText("女");
        }
        etUserPhone.setText(userPhone);
    }

    private void getCustomKey() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("加载中...");
            mLoadingDialog.show();
        }
        EnrollCustomKeyParam param = new EnrollCustomKeyParam(ActEnrollInfo.this, actId);
        HttpStringPost task = new HttpStringPost(ActEnrollInfo.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                ActEnrollCustomKeyModelList list = gson.fromJson(result, ActEnrollCustomKeyModelList.class);
                if (list != null && list.getCustom_keys().size() > 0) {
                    hasCustomKey = true;
                    adapter.setCustomKey(list.getCustom_keys());
                }
                if (listView != null && !listView.isShown()){
                    listView.startAnimation(alphaIn);
                    listView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void relogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("加载失败");
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("加载失败");
                finish();
            }
        }, param.getParameters());
        executeRequest(task);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                if (!ClickUtil.isFastClick()) {
                    finish();
                }
                break;
            case R.id.tvBirth:
                DialogDateSelect dialogDate = new DialogDateSelect(ActEnrollInfo.this, R.style.dialog_date);
                dialogDate.withDuration(300).withEffect(Effectstype.Fadein).setOnSureClick(new DialogDateSelect.OnDateClickListener() {
                    @Override
                    public void onDateListener(final String date) {
                        if (TimeUtil.getUserAge(date) == -1) {
                            if (dialog != null && !dialog.isShowing()) {
                                dialog.setMessage("年龄选择错误").withEffect(Effectstype.Shake).show();
                            }
                        } else {
                            birth = date;
                            tvBirth.setText(birth);
                        }
                    }
                }).show();
                break;
            case R.id.tvEnroll:
                if (!ClickUtil.isFastClick()) {
                    userName = etUserName.getText().toString();
                    birth = tvBirth.getText().toString();
                    userPhone = etUserPhone.getText().toString();
                    ArrayList<ActEnrollCustomKeyModel> list = adapter.getList();
                    if (hasCustomKey) {
                        for (int i = 0; i < list.size(); i++) {
                            if (TextUtils.isEmpty(list.get(i).getContent())) {
                                if (dialog != null && !dialog.isShowing()) {
                                    dialog.setMessage("报名信息不能有空").withEffect(Effectstype.Shake).show();
                                }
                                return;
                            }
                        }
                    }
                    if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(birth) || TextUtils.isEmpty(userPhone)) {
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("报名信息不能有空").withEffect(Effectstype.Shake).show();
                        }
                        return;
                    }
                    if (!checkNameChese(userName)) {
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("姓名只能输入中文").withEffect(Effectstype.Shake).show();
                        }
                        return;
                    }
                    if (MobileUtil.execute(userPhone).equals("未知")) {
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
                        }
                        return;
                    }
                    Enroll(list);
                }
                break;
        }
    }

    private void Enroll(ArrayList<ActEnrollCustomKeyModel> list) {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("提交中...");
            mLoadingDialog.show();
        }
        ActEnrollParam param = new ActEnrollParam(ActEnrollInfo.this, actId, userName, userSex, birth, userPhone, list);
        HttpStringPost task = new HttpStringPost(ActEnrollInfo.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("报名成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void relogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("报名失败，请重试");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("报名失败，请重试");
            }
        }, param.getParameters());
        executeRequest(task);
    }

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
