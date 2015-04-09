package com.gather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.ActEnrollStatusCommentAdapter;
import com.gather.android.adapter.ActEnrollStatusGridViewAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActCheckInListModel;
import com.gather.android.model.ActEnrollStatusCommentListModel;
import com.gather.android.model.ActEnrollStatusCommentModel;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModulesStatusModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.model.VipListModel;
import com.gather.android.params.ActCheckInListParam;
import com.gather.android.params.ActEnrollStatusCommentParam;
import com.gather.android.params.ActEnrollStatusCommentSendParam;
import com.gather.android.params.ActManagerListParam;
import com.gather.android.params.ActMemeberListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.DES;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Christain on 2015/3/17.
 */
public class ActEnrollStatus extends SwipeBackActivity implements View.OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llMember, llManager, llActProcess, llActShowPic, llActNotify, llActPhoto, llMain, llComment, llEditText, llNoMember, llNoManager, llNoComment, llPay;
    private TextView tvActTitle, tvActTime;
    private NoScrollGridView memberGridView, managerGradView;
    private ActEnrollStatusGridViewAdapter memberAdapter, managerAdapter;
    private TextView tvMemberMore;
    private NoScrollListView listView;
    private View footerView;
    private ActEnrollStatusCommentAdapter commentAdapter;
    private EditText etContent;
    private Button btComment;
    private ProgressBar footer_progress;
    private TextView footer_textview;
    private RelativeLayout footer_all;
    private int totalNum, maxPage, page = 1, size = 15, isOver;

    private boolean isMemberShow = true, isRefresh, isComment = false;
    private Animation alphaIn;
    private LoadingDialog mLoadingDialog;
    private DialogTipsBuilder dialog;
    private ActModel model;
    private ActModulesStatusModel modulesStatusModel;
    private ActMoreInfoModel actMoreInfoModel;

    @Override
    protected int layoutResId() {
        return R.layout.act_enroll_status;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("MODEL") && intent.hasExtra("MODULE") && intent.hasExtra("MORE_INFO")) {
            this.model = (ActModel) intent.getSerializableExtra("MODEL");
            this.modulesStatusModel = (ActModulesStatusModel) intent.getSerializableExtra("MODULE");
            this.actMoreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO");
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("报名情况");

            if (modulesStatusModel.getShow_checkin() == 1) {
                if (actMoreInfoModel.getEnroll_status() == 3) {
                    this.tvRight.setVisibility(View.VISIBLE);
                    this.tvRight.setText("签到");
                    this.tvRight.setOnClickListener(this);
                } else {
                    this.tvRight.setVisibility(View.GONE);
                }
            } else {
                this.tvRight.setVisibility(View.GONE);
            }
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivLeft.setOnClickListener(this);

            this.mLoadingDialog = LoadingDialog.createDialog(ActEnrollStatus.this, true);
            this.dialog = DialogTipsBuilder.getInstance(ActEnrollStatus.this);
            this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
            this.llMember = (LinearLayout) findViewById(R.id.llMember);
            this.llManager = (LinearLayout) findViewById(R.id.llManager);
            this.llActProcess = (LinearLayout) findViewById(R.id.llActProcess);
            this.llActShowPic = (LinearLayout) findViewById(R.id.llActShowPic);
            this.llActNotify = (LinearLayout) findViewById(R.id.llActNotify);
            this.llActPhoto = (LinearLayout) findViewById(R.id.llActPhoto);
            this.llMain = (LinearLayout) findViewById(R.id.llMain);
            this.llComment = (LinearLayout) findViewById(R.id.llComment);
            this.llEditText = (LinearLayout) findViewById(R.id.llEditText);
            this.llNoMember = (LinearLayout) findViewById(R.id.llNoMember);
            this.llNoManager = (LinearLayout) findViewById(R.id.llNoManager);
            this.llNoComment = (LinearLayout) findViewById(R.id.llNoComment);
            this.llPay = (LinearLayout) findViewById(R.id.llPay);
            this.memberGridView = (NoScrollGridView) findViewById(R.id.memberGridView);
            this.managerGradView = (NoScrollGridView) findViewById(R.id.managerGradView);
            this.tvMemberMore = (TextView) findViewById(R.id.tvMemberMore);
            this.tvActTitle = (TextView) findViewById(R.id.tvActTitle);
            this.tvActTime = (TextView) findViewById(R.id.tvActTime);
            this.listView = (NoScrollListView) findViewById(R.id.listview);
            this.etContent = (EditText) findViewById(R.id.etContent);
            this.btComment = (Button) findViewById(R.id.btComment);
            this.footerView = LayoutInflater.from(ActEnrollStatus.this).inflate(R.layout.act_enroll_status_load_more, null);
            this.footer_progress = (ProgressBar) footerView.findViewById(R.id.footer_progress);
            this.footer_textview = (TextView) footerView.findViewById(R.id.footer_textview);
            this.footer_all = (RelativeLayout) footerView.findViewById(R.id.footer_all);
            this.listView.addFooterView(footerView);
            this.commentAdapter = new ActEnrollStatusCommentAdapter(ActEnrollStatus.this);
            this.listView.setAdapter(commentAdapter);

            this.tvMemberMore.setOnClickListener(this);
            this.llActProcess.setOnClickListener(this);
            this.llActShowPic.setOnClickListener(this);
            this.llActNotify.setOnClickListener(this);
            this.llActPhoto.setOnClickListener(this);
            this.llPay.setOnClickListener(this);
            this.btComment.setOnClickListener(this);
            this.footer_all.setOnClickListener(this);

            this.memberGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    UserInfoModel model = memberAdapter.getItem(position);
                    if (model != null && !ClickUtil.isFastClick()) {
                        Intent intent = new Intent(ActEnrollStatus.this, UserCenter.class);
                        intent.putExtra("UID", model.getUid());
                        intent.putExtra("MODEL", model);
                        startActivity(intent);
                    }
                }
            });
            this.managerGradView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    UserInfoModel model = managerAdapter.getItem(position);
                    if (model != null && !ClickUtil.isFastClick()) {
                        Intent intent = new Intent(ActEnrollStatus.this, Chat.class);
                        intent.putExtra("UID", model.getUid());
                        intent.putExtra("MODEL", model);
                        startActivity(intent);
                    }
                }
            });
            this.llMain.setVisibility(View.INVISIBLE);
            this.initView();
            this.getManagerList();
            this.getMemberList();

            String str = "{\"filter\":\"checkin_id\",\"value\":99}";
            // 在这里使用的是encode方式，返回的是byte类型加密数据，可使用new String转为String类型
            String strBase64 = new String(Base64.encode(str.getBytes(), Base64.DEFAULT));
            Log.i("Test", "encode >>>" + strBase64);
            // 这里 encodeToString 则直接将返回String类型的加密数据
            String enToStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
            Log.i("Test", "encodeToString >>> " + enToStr);
            // 对base64加密后的数据进行解密
            Log.i("Test", "decode >>>" + new String(Base64.decode(strBase64.getBytes(), Base64.DEFAULT)));
            try {
                DES des = new DES("zero2all");
                Log.e("json", des.encrypt("{\"filter\":\"checkin_id\",\"value\":99}"));
                String json = des.decrypt(des.encrypt("{\"filter\":\"checkin_id\",\"value\":99}"));
                Log.e("json", json);
            }catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            toast("查看失败，请重试");
            finish();
        }
    }

    private void initView() {
        if (model != null) {
            tvActTitle.setText(model.getIntro());
            tvActTime.setText(TimeUtil.getActEnrollStatus(model.getB_time()));
        }
        if (modulesStatusModel.getShow_message() == 1) {
            this.llComment.setVisibility(View.VISIBLE);
            this.listView.setVisibility(View.VISIBLE);
            this.llEditText.setVisibility(View.VISIBLE);
            this.getCommentList();
        } else {
            this.llComment.setVisibility(View.GONE);
            this.listView.setVisibility(View.GONE);
            this.llEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                if (!ClickUtil.isFastClick()) {
                    finish();
                }
                break;
            case R.id.tvMemberMore:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActEnrollStatus.this, ActMemberList.class);
                    intent.putExtra("ID", model.getId());
                    intent.putExtra("MODULE", modulesStatusModel);
                    intent.putExtra("MORE_INFO", actMoreInfoModel);
                    startActivity(intent);
                }
                break;
            case R.id.tvRight:
                if (!ClickUtil.isFastClick()) {
                    getCheckin();
                }
                break;
            case R.id.llActProcess:
                if (!ClickUtil.isFastClick() && model != null) {
                    Intent intent = new Intent(ActEnrollStatus.this, ActProcess.class);
                    intent.putExtra("MODULE", modulesStatusModel);
                    intent.putExtra("MORE_INFO", actMoreInfoModel);
                    intent.putExtra("ID", model.getId());
                    startActivity(intent);
                }
                break;
            case R.id.llActShowPic:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActEnrollStatus.this, ActShowPic.class);
                    intent.putExtra("MODULE", modulesStatusModel);
                    intent.putExtra("MORE_INFO", actMoreInfoModel);
                    intent.putExtra("MODEL", model);
                    intent.putExtra("ID", model.getId());
                    startActivity(intent);
                }
                break;
            case R.id.llActNotify:
                if (!ClickUtil.isFastClick()) {
                    if (modulesStatusModel.getShow_notice() == 1) {
                        Intent intent = new Intent(ActEnrollStatus.this, ActNotifycation.class);
                        intent.putExtra("ID", model.getId());
                        startActivity(intent);
                    } else {
                        toast("活动还没有通知");
                    }
                }
                break;
            case R.id.llActPhoto:
                if (!ClickUtil.isFastClick()) {
                    if (modulesStatusModel.getShow_album() == 1) {
                        Intent intent = new Intent(ActEnrollStatus.this, ActAlbumList.class);
                        intent.putExtra("ID", model.getId());
                        intent.putExtra("MORE_INFO", actMoreInfoModel);
                        startActivity(intent);
                    } else {
                        toast("活动还没有开放相册");
                    }
                }
                break;
            case R.id.btComment:
                if (!ClickUtil.isFastClick()) {
                    if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
                        if (!isComment) {
                            isComment = true;
                            closeKeyboard();
                            sendComment(etContent.getText().toString().trim());
                        }
                    } else {
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("请输入留言内容").withEffect(Effectstype.Shake).show();
                        }
                    }
                }
            case R.id.footer_all:
                if (!ClickUtil.isFastClick() && !isRefresh && isOver != 1) {
                    isRefresh = true;
                    footer_textview.setVisibility(View.GONE);
                    footer_progress.setVisibility(View.VISIBLE);
                    getCommentList();
                }
                break;
            case R.id.llPay:

                break;

        }
    }

    /**
     * 获取报名人列表
     */
    private void getMemberList() {
        mLoadingDialog.setMessage("加载中...");
        mLoadingDialog.show();
        GatherApplication application = GatherApplication.getInstance();
        ActMemeberListParam param = new ActMemeberListParam(application.getCityId(), model.getId(), 1, 5);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                VipListModel list = gson.fromJson(result, VipListModel.class);
                if (list != null && list.getUsers() != null && list.getUsers().size() > 0) {
                    llNoMember.setVisibility(View.GONE);
                    memberGridView.setVisibility(View.VISIBLE);
                    if (list.getUsers().size() > 4) {
                        tvMemberMore.setVisibility(View.VISIBLE);
                    } else {
                        tvMemberMore.setVisibility(View.VISIBLE);
                    }
                    int myUserId = AppPreference.getUserPersistentInt(ActEnrollStatus.this, AppPreference.USER_ID);
                    for (int i = 0; i < list.getUsers().size(); i++) {
                        if (myUserId == list.getUsers().get(i).getId()) {
                            list.getUsers().remove(i);
                            break;
                        }
                    }
                    if (actMoreInfoModel.getEnroll_status() == 3) {
                        list.getUsers().add(0,GatherApplication.getInstance().getUserInfoModel());
                    }
                    memberAdapter = new ActEnrollStatusGridViewAdapter(ActEnrollStatus.this, list.getUsers());
                    memberGridView.setAdapter(memberAdapter);
                } else {
                    llNoMember.setVisibility(View.VISIBLE);
                    memberGridView.setVisibility(View.GONE);
                    tvMemberMore.setVisibility(View.GONE);
                }

                if (!llMain.isShown()) {
                    llMain.setVisibility(View.VISIBLE);
                    llMain.startAnimation(alphaIn);
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("加载报名情况失败，请重试");
                finish();
            }
        });
    }

    /**
     * 获取管理员列表
     */
    private void getManagerList() {
        GatherApplication application = GatherApplication.getInstance();
        ActManagerListParam param = new ActManagerListParam(application.getCityId(), model.getId(), 1, 4);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                VipListModel list = gson.fromJson(result, VipListModel.class);
                if (list != null && list.getUsers() != null && list.getUsers().size() > 0) {
                    llNoManager.setVisibility(View.GONE);
                    managerGradView.setVisibility(View.VISIBLE);
                    managerAdapter = new ActEnrollStatusGridViewAdapter(ActEnrollStatus.this, list.getUsers());
                    managerGradView.setAdapter(managerAdapter);
                } else {
                    llNoManager.setVisibility(View.VISIBLE);
                    managerGradView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                llNoManager.setVisibility(View.VISIBLE);
                managerGradView.setVisibility(View.GONE);
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                llNoManager.setVisibility(View.VISIBLE);
                managerGradView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取留言列表
     */
    private void getCommentList() {
        ActEnrollStatusCommentParam param = new ActEnrollStatusCommentParam(model.getId(), page, size);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (page == 1) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(result);
                        totalNum = object.getInt("total_num");
                        if (totalNum % size == 0) {
                            maxPage = totalNum / size;
                        } else {
                            maxPage = (totalNum / size) + 1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    } finally {
                        object = null;
                    }
                }
                Gson gson = new Gson();
                ActEnrollStatusCommentListModel list = gson.fromJson(result, ActEnrollStatusCommentListModel.class);
                if (list != null && list.getMessages() != null && list.getMessages().size() > 0) {
                    if (page != maxPage) {
                        page++;
                        loadMoreOver(returnCode, "CLICK_MORE");
                    } else {
                        isOver = 1;
                        loadMoreOver(returnCode, "ISOVER");
                    }
                    commentAdapter.addItems(list.getMessages());
                } else {
                    isOver = 1;
                    loadMoreOver(returnCode, "NULL");
                }
                isRefresh = false;
            }

            @Override
            public void onNeedLogin(String msg) {
                isRefresh = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                isRefresh = false;
                errorMessage();
            }
        });
    }

    private void loadMoreOver(int code, String msg) {
        if (msg.equals("CLICK_MORE")) {
            footer_textview.setText("点击更多");
            footer_textview.setVisibility(View.VISIBLE);
            footer_progress.setVisibility(View.GONE);
        } else if (msg.equals("ISOVER")) {
            footer_textview.setText("已是全部");
            footer_textview.setVisibility(View.VISIBLE);
            footer_progress.setVisibility(View.GONE);
        } else if (msg.equals("NULL")) {
            footer_textview.setVisibility(View.GONE);
            footer_progress.setVisibility(View.GONE);
            llNoComment.setVisibility(View.VISIBLE);
        }
    }

    private void errorMessage() {
        llNoComment.setVisibility(View.VISIBLE);
        footer_textview.setVisibility(View.GONE);
        footer_progress.setVisibility(View.GONE);
//        footer_textview.setText("加载失败，请重试");
//        footer_textview.setVisibility(View.VISIBLE);
//        footer_progress.setVisibility(View.GONE);
    }

    private void sendComment(final String content) {
        ActEnrollStatusCommentSendParam param = new ActEnrollStatusCommentSendParam(model.getId(), content);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (page == 1) {
                    ActEnrollStatusCommentModel model = new ActEnrollStatusCommentModel();
                    model.setAuthor_id(AppPreference.getUserPersistentInt(ActEnrollStatus.this, AppPreference.USER_ID));
                    model.setContent(content);
                    model.setCreate_time(TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
                    model.setUser(AppPreference.getUserInfo(ActEnrollStatus.this));
                    commentAdapter.addItem(model, commentAdapter.getCount());
                    if (llNoComment.isShown()) {
                        footer_textview.setText("已是全部");
                        footer_textview.setVisibility(View.VISIBLE);
                        footer_progress.setVisibility(View.GONE);
                        llNoComment.setVisibility(View.GONE);
                    }
                    etContent.setText("");
                    isComment = false;
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                isComment = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                isComment = false;
                toast("留言失败，请重试");
            }
        });
    }

    /**
     * 签到列表
     */
    private void getCheckin() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("加载中...");
            mLoadingDialog.show();
        }
        ActCheckInListParam param = new ActCheckInListParam(model.getId());
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                ActCheckInListModel list = gson.fromJson(result, ActCheckInListModel.class);
                if (list != null ) {
                    if (list.getCheckins().size() == 0) {
                        Intent checkin = new Intent(ActEnrollStatus.this, QRCodeScan.class);
                        checkin.putExtra("MORE_INFO", actMoreInfoModel);
                        startActivity(checkin);
                    } else {
                        Intent intent = new Intent(ActEnrollStatus.this, ActPassPort.class);
                        intent.putExtra("MODEL", list.getCheckins().get(list.getCheckins().size() - 1));
                        intent.putExtra("MORE_INFO", actMoreInfoModel);
                        startActivity(intent);
                    }
                } else {
                    toast("签到失败，请重试");
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("签到失败，请重试");
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("签到失败，请重试");
            }
        });
    }

    /**
     * 关闭软键盘
     */
    protected void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 打开软键盘
     */
    protected void openKeyboard() {
        delayToDo(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.showSoftInput(getCurrentFocus(), 0);
                }
            }
        }, 100);
    }

    /**
     * 延迟操作
     *
     * @param task
     * @param delay
     */
    protected void delayToDo(TimerTask task, long delay) {
        Timer timer = new Timer();
        timer.schedule(task, delay);
    }

}
