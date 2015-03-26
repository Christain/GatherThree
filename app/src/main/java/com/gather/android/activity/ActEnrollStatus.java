package com.gather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.adapter.ActEnrollStatusCommentAdapter;
import com.gather.android.adapter.ActEnrollStatusGridViewAdapter;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActEnrollStatusCommentListModel;
import com.gather.android.model.ActEnrollStatusCommentModel;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModulesStatusModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.ActEnrollStatusCommentParam;
import com.gather.android.params.ActEnrollStatusCommentSendParam;
import com.gather.android.params.ActModulesStatusParam;
import com.gather.android.params.ActMoreInfoParam;
import com.gather.android.params.UpdateVersionParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Christain on 2015/3/17.
 */
public class ActEnrollStatus extends SwipeBackActivity implements View.OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private LinearLayout llMember, llManager, llActProcess, llActShowPic, llActNotify, llActPhoto, llMain, llComment, llEditText, llNoMember, llNoComment;
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
    private int totalNum, maxPage, page = 1, size = 1, isOver;

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
        if (intent.hasExtra("MODEL")) {
            this.model = (ActModel) intent.getSerializableExtra("MODEL");
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvLeft.setVisibility(View.GONE);
            this.ivRight.setVisibility(View.VISIBLE);
            this.tvRight.setVisibility(View.GONE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.tvTitle.setText("报名情况");
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.ivRight.setImageResource(R.drawable.icon_act_enroll_status_group);
            this.ivRight.setBackgroundDrawable(null);
            this.ivRight.setPadding(10, 10, 10, 10);
            this.ivLeft.setOnClickListener(this);
            this.ivRight.setOnClickListener(this);

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
            this.llNoComment = (LinearLayout) findViewById(R.id.llNoComment);
            this.memberGridView = (NoScrollGridView) findViewById(R.id.memberGridView);
            this.managerGradView = (NoScrollGridView) findViewById(R.id.managerGradView);
            this.tvMemberMore = (TextView) findViewById(R.id.tvMemberMore);
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
                        Intent intent = new Intent(ActEnrollStatus.this, UserCenter.class);
                        intent.putExtra("UID", model.getUid());
                        intent.putExtra("MODEL", model);
                        startActivity(intent);
                    }
                }
            });
            this.llMain.setVisibility(View.INVISIBLE);
            this.llComment.setVisibility(View.GONE);
            this.listView.setVisibility(View.GONE);
            this.llEditText.setVisibility(View.GONE);
            this.getActModulesStatus();
            this.getMemberList();
            this.getActMoreInfo();
        } else {
            toast("查看失败，请重试");
            finish();
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

                }
                break;
            case R.id.ivRight:
                if (!ClickUtil.isFastClick()) {

                }
                break;
            case R.id.llActProcess:
                if (!ClickUtil.isFastClick() && model != null) {
                    Intent intent = new Intent(ActEnrollStatus.this, ActProcess.class);
                    if (modulesStatusModel != null) {
                        intent.putExtra("MODEL", modulesStatusModel);
                    }
                    if (actMoreInfoModel != null) {
                        intent.putExtra("MORE_INFO_MODEL", actMoreInfoModel);
                    }
                    intent.putExtra("ID", model.getId());
                    startActivity(intent);
                }
                break;
            case R.id.llActShowPic:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActEnrollStatus.this, ActShowPic.class);
                    if (modulesStatusModel != null) {
                        intent.putExtra("MODEL", modulesStatusModel);
                    }
                    if (actMoreInfoModel != null) {
                        intent.putExtra("MORE_INFO_MODEL", actMoreInfoModel);
                    }
                    intent.putExtra("ACT_MODEL", model);
                    intent.putExtra("ID", model.getId());
                    startActivity(intent);
                }
                break;
            case R.id.llActNotify:
                if (!ClickUtil.isFastClick()) {
                    Intent intent = new Intent(ActEnrollStatus.this, ActNotifycation.class);
                    intent.putExtra("ID", model.getId());
                    startActivity(intent);
                }
                break;
            case R.id.llActPhoto:
                if (!ClickUtil.isFastClick()) {

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

        }
    }

    /**
     * 获取活动模块信息
     */
    private void getActModulesStatus() {
        ActModulesStatusParam param = new ActModulesStatusParam(ActEnrollStatus.this, model.getId());
        HttpStringPost task = new HttpStringPost(ActEnrollStatus.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    modulesStatusModel = gson.fromJson(object.getString("act_modules"), ActModulesStatusModel.class);

                    modulesStatusModel = new ActModulesStatusModel();
                    modulesStatusModel.setShow_process(1);
                    modulesStatusModel.setShow_attention(1);
                    modulesStatusModel.setShow_busi(1);
                    modulesStatusModel.setShow_menu(1);
                    modulesStatusModel.setShow_message(1);

                    if (modulesStatusModel != null && modulesStatusModel.getShow_message() == 1) {
                        llComment.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        llEditText.setVisibility(View.VISIBLE);
                        getCommentList();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void relogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                if (Constant.SHOW_LOG) {
                    toast("获取活动模块信息失败");
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (Constant.SHOW_LOG) {
                    toast("获取活动模块信息失败");
                }
            }
        }, param.getParameters());
        executeRequest(task);
    }

    /**
     * 活动更多信息
     */
    private void getActMoreInfo() {
        ActMoreInfoParam param = new ActMoreInfoParam(ActEnrollStatus.this, model.getId());
        HttpStringPost task = new HttpStringPost(ActEnrollStatus.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    actMoreInfoModel = gson.fromJson(object.getString("act_info"), ActMoreInfoModel.class);
                } catch (JSONException e) {
                    actMoreInfoModel = null;
                    e.printStackTrace();
                }
            }

            @Override
            public void relogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                actMoreInfoModel = null;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                actMoreInfoModel = null;
            }
        }, param.getParameters());
        executeRequest(task);
    }

    /**
     * 获取报名人列表
     */
    private void getMemberList() {
        mLoadingDialog.setMessage("加载中...");
        mLoadingDialog.show();
        UpdateVersionParam param = new UpdateVersionParam(ActEnrollStatus.this);
        HttpStringPost task = new HttpStringPost(ActEnrollStatus.this, param.getUrl(), new com.gather.android.http.ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                ArrayList<UserInfoModel> list = new ArrayList<UserInfoModel>();
                for (int i = 0; i < 4; i++) {
                    UserInfoModel model = new UserInfoModel();
                    model.setNick_name(i + "名字");
                    model.setUid(i);
                    list.add(model);
                }
                memberAdapter = new ActEnrollStatusGridViewAdapter(ActEnrollStatus.this, list);
                memberGridView.setAdapter(memberAdapter);

                managerAdapter = new ActEnrollStatusGridViewAdapter(ActEnrollStatus.this, list);
                managerGradView.setAdapter(managerAdapter);
                if (!llMain.isShown()) {
                    llMain.setVisibility(View.VISIBLE);
                    llMain.startAnimation(alphaIn);
                }
                switch (model.getEnroll_status()) {
                    case 0:
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("您还未报名").withEffect(Effectstype.Fadein).show();
                        }
                        break;
                    case 1:
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("报名审核中").withEffect(Effectstype.Fadein).show();
                        }
                        break;
                    case 2:
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("您已报名成功").withEffect(Effectstype.Fadein).show();
                        }
                        break;
                    case 3:
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("您的报名被拒绝").withEffect(Effectstype.Fadein).show();
                        }
                        break;
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
                toast("加载报名情况失败，请重试");
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("加载报名情况失败，请重试");
                finish();
            }
        }, param.getParameters());
        executeRequest(task);
    }

    /**
     * 获取留言列表
     */
    private void getCommentList() {
        ActEnrollStatusCommentParam param = new ActEnrollStatusCommentParam(ActEnrollStatus.this, model.getId(), page, size);
        HttpStringPost task = new HttpStringPost(ActEnrollStatus.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
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
                    ArrayList<ActEnrollStatusCommentModel> lists = new ArrayList<ActEnrollStatusCommentModel>();
                    for (int i = 0; i < 15; i++) {
                        ActEnrollStatusCommentModel commentModel = new ActEnrollStatusCommentModel();
                        commentModel.setAuthor_id(1);
                        commentModel.setContent("这个就是我的留言" + i);
                        commentModel.setCreate_time("2015-03-07 14:36:00");
                        UserInfoModel model = new UserInfoModel();
                        model.setNick_name(i + "名字");
                        model.setUid(i);
                        commentModel.setUser(model);
                        lists.add(commentModel);
                    }
                    if (page != maxPage) {
                        page++;
                        loadMoreOver(code, "CLICK_MORE");
                    } else {
                        isOver = 1;
                        loadMoreOver(code, "ISOVER");
                    }
                    commentAdapter.addItems(lists);
                } else {
                    isOver = 1;
                    loadMoreOver(code, "NULL");
                }
                isRefresh = false;
            }

            @Override
            public void relogin(String msg) {
                isRefresh = false;
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                isRefresh = false;
                errorMessage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRefresh = false;
                errorMessage();
            }
        }, param.getParameters());
        executeRequest(task);
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
        ActEnrollStatusCommentSendParam param = new ActEnrollStatusCommentSendParam(ActEnrollStatus.this, model.getId(), content);
        HttpStringPost task = new HttpStringPost(ActEnrollStatus.this, param.getUrl(), new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
                if (page == 1) {
                    ActEnrollStatusCommentModel model = new ActEnrollStatusCommentModel();
                    model.setAuthor_id(AppPreference.getUserPersistentInt(ActEnrollStatus.this, AppPreference.USER_ID));
                    model.setContent(content);
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
            public void relogin(String msg) {
                isComment = false;
                needLogin(msg);
            }

            @Override
            public void error(int code, String msg) {
                isComment = false;
                toast("留言失败，请重试");

                if (page == 1) {
                    ActEnrollStatusCommentModel model = new ActEnrollStatusCommentModel();
                    model.setAuthor_id(AppPreference.getUserPersistentInt(ActEnrollStatus.this, AppPreference.USER_ID));
                    model.setContent(content);
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isComment = false;
                toast("留言失败，请重试");

                if (page == 1) {
                    ActEnrollStatusCommentModel model = new ActEnrollStatusCommentModel();
                    model.setAuthor_id(AppPreference.getUserPersistentInt(ActEnrollStatus.this, AppPreference.USER_ID));
                    model.setContent(content);
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
        }, param.getParameters());
        executeRequest(task);
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
