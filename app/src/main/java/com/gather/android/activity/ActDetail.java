package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.adapter.ActDetailVipAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActAddressAndCarLocationListModel;
import com.gather.android.model.ActAddressModel;
import com.gather.android.model.ActCommentModelList;
import com.gather.android.model.ActDetailModel;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModulesStatusModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.model.NewsModel;
import com.gather.android.model.NewsModelList;
import com.gather.android.model.UserInfoModel;
import com.gather.android.model.UserList;
import com.gather.android.params.ActCommentListParam;
import com.gather.android.params.ActDetailParam;
import com.gather.android.params.ActDetailRelationParam;
import com.gather.android.params.ActDetailVipParam;
import com.gather.android.params.ActModulesStatusParam;
import com.gather.android.params.ActMoreAddressParam;
import com.gather.android.params.ActMoreInfoParam;
import com.gather.android.params.CancelCollectActParam;
import com.gather.android.params.CollectActParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.InfiniteLoopViewPager;
import com.gather.android.widget.InfiniteLoopViewPagerAdapter;
import com.gather.android.widget.MyViewPager.OnPageChangeListener;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.UserCenterScrollView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tendcloud.tenddata.TCAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动详情
 */
public class ActDetail extends SwipeBackActivity implements OnClickListener {

	private LinearLayout llError, llAddress, llActStrategy, llActMemory, llActVip, llActBus, llActComment, llActCommentItem, llWeChat, llSquare, llZoon, llSina, llMenuBar, llEnroll, llComment, llEnrollStatus, llAddressLayout;
	private ImageView ivBack, ivActLove, ivUserIcon, ivErrorImg, ivOnePic;
	private TextView tvTitle, tvActMark, tvActCost, tvAddress, tvAddressDetail, tvActDetailContent, tvActStrategy, tvActMemory, tvActBus, tvActCommentNum, tvActCommentAll, tvUserName, tvUserComment;
	private NoScrollGridView vipGridView;
	private ActDetailVipAdapter vipAdapter;
	private View menuLine, enrollstatusLine;
	private UserCenterScrollView scrollView;
	private ProgressBar progressOnePic;

	private int actId;
	private ActModel actModel;
    private ActMoreInfoModel actMoreInfoModel;
    private ArrayList<ActAddressModel> addressList;
    private ActModulesStatusModel modulesStatusModel;
	private boolean hasLogin;

	private DialogTipsBuilder dialog;
	private DialogChoiceBuilder choiceBuilder;
	private LoadingDialog mLoadingDialog;
	private Animation alphaIn;
	private GatherApplication application;

	/********** 自动循环图片 ********************/
	private InfiniteLoopViewPager mViewPager;
	private InfiniteLoopViewPagerAdapter pagerAdapter;
	private LinearLayout mLinearLayout;
	private Drawable mIndicatorUnfocused;
	private Drawable mIndicatorFocused;
	private List<ImageView> mImageList;
	private Handler mHandler;
	private int sleepTime = 4000;
	private DisplayImageOptions options, userOptions;

	/********** 第三方绑定 *************/
	private Tencent mTencent;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
	private IWXAPI wxApi;
	private String Share_Message = "集合啦！脱宅利器必备神器-你想要的活动，这里都有！";

	@Override
	protected int layoutResId() {
		return R.layout.act_detail;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("ID")) {
			this.actId = intent.getExtras().getInt("ID");
			this.application = (GatherApplication) getApplication();
			this.mLoadingDialog = LoadingDialog.createDialog(ActDetail.this, true);
			this.dialog = DialogTipsBuilder.getInstance(ActDetail.this);
			this.choiceBuilder = DialogChoiceBuilder.getInstance(ActDetail.this);
			this.hasLogin = AppPreference.hasLogin(ActDetail.this);
			this.userOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
			this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
			this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
			this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
			this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);
			this.ivBack = (ImageView) findViewById(R.id.ivBack);
			this.ivActLove = (ImageView) findViewById(R.id.ivActLove);
			this.ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);
			this.ivErrorImg = (ImageView) findViewById(R.id.ivErrorImg);

			this.scrollView = (UserCenterScrollView) findViewById(R.id.scrollView);
			this.ivOnePic = (ImageView) findViewById(R.id.ivOnePic);
			this.progressOnePic = (ProgressBar) findViewById(R.id.progressOnePic);
			this.mViewPager = (InfiniteLoopViewPager) findViewById(R.id.viewPager);
			this.llError = (LinearLayout) findViewById(R.id.llError);
			this.llAddress = (LinearLayout) findViewById(R.id.llAddress);
			this.llActStrategy = (LinearLayout) findViewById(R.id.llActStrategy);
			this.llActMemory = (LinearLayout) findViewById(R.id.llActMemory);
			this.llActVip = (LinearLayout) findViewById(R.id.llActVip);
			this.llActBus = (LinearLayout) findViewById(R.id.llActBus);
			this.llActComment = (LinearLayout) findViewById(R.id.llActComment);
			this.llActCommentItem = (LinearLayout) findViewById(R.id.llActCommentItem);
			this.llWeChat = (LinearLayout) findViewById(R.id.llWeChat);
			this.llSquare = (LinearLayout) findViewById(R.id.llSquare);
			this.llZoon = (LinearLayout) findViewById(R.id.llZoon);
			this.llSina = (LinearLayout) findViewById(R.id.llSina);
			this.llMenuBar = (LinearLayout) findViewById(R.id.llMenuBar);
			this.llEnroll = (LinearLayout) findViewById(R.id.llEnroll);
			this.llComment = (LinearLayout) findViewById(R.id.llComment);
            this.llEnrollStatus = (LinearLayout) findViewById(R.id.llEnrollStatus);
            this.llAddressLayout = (LinearLayout) findViewById(R.id.llAddressLayout);

			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvActMark = (TextView) findViewById(R.id.tvActMark);
			this.tvActCost = (TextView) findViewById(R.id.tvActCost);
			this.tvAddress = (TextView) findViewById(R.id.tvAddress);
			this.tvAddressDetail = (TextView) findViewById(R.id.tvAddressDetail);
			this.tvActDetailContent = (TextView) findViewById(R.id.tvActDetailContent);
			this.tvActStrategy = (TextView) findViewById(R.id.tvActStrategy);
			this.tvActMemory = (TextView) findViewById(R.id.tvActMemory);
			this.tvActBus = (TextView) findViewById(R.id.tvActBus);
			this.tvActCommentNum = (TextView) findViewById(R.id.tvActCommentNum);
			this.tvActCommentAll = (TextView) findViewById(R.id.tvActCommentAll);
			this.tvUserName = (TextView) findViewById(R.id.tvUserName);
			this.tvUserComment = (TextView) findViewById(R.id.tvUserComment);

			this.vipGridView = (NoScrollGridView) findViewById(R.id.vipGridView);
			this.menuLine = (View) findViewById(R.id.menuLine);
            this.enrollstatusLine = (View) findViewById(R.id.enrollstatusLine);

			DisplayMetrics metrics = getResources().getDisplayMetrics();
			FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) this.mViewPager.getLayoutParams();
			param.width = metrics.widthPixels;
			param.height = param.width * 6 / 9;
			this.mViewPager.setLayoutParams(param);
			this.ivOnePic.setLayoutParams(param);

			mLinearLayout = (LinearLayout) findViewById(R.id.slide_indicator);
			mIndicatorUnfocused = getResources().getDrawable(R.drawable.slide_indicator_unfocused);
			mIndicatorFocused = getResources().getDrawable(R.drawable.slide_indicator_focused);

			this.ivErrorImg.setOnClickListener(this);
			this.ivOnePic.setOnClickListener(this);
			this.ivBack.setOnClickListener(this);
			this.ivActLove.setOnClickListener(this);
			this.llAddress.setOnClickListener(this);
			this.tvActCommentAll.setOnClickListener(this);
			this.llActCommentItem.setOnClickListener(this);
			this.llWeChat.setOnClickListener(this);
			this.llSquare.setOnClickListener(this);
			this.llZoon.setOnClickListener(this);
			this.llSina.setOnClickListener(this);
			this.llEnroll.setOnClickListener(this);
			this.llComment.setOnClickListener(this);
            this.llEnrollStatus.setOnClickListener(this);

			this.vipGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					if (!ClickUtil.isFastClick()) {
						UserInfoModel model = vipAdapter.getItem(position);
						if (model != null) {
							Intent intent = new Intent(ActDetail.this, UserCenter.class);
							intent.putExtra("UID", model.getUid());
							intent.putExtra("MODEL", model);
							startActivity(intent);
						}
					}
				}
			});

			this.initView();
		} else {
			toast("详情信息错误");
			finish();
		}
	}

	@SuppressLint("HandlerLeak")
	private void initView() {
		llError.setVisibility(View.GONE);
		scrollView.setVisibility(View.GONE);
		llMenuBar.setVisibility(View.GONE);
		getActDetailStrategy();
		getActDetailMemory();
		getActDetailVip();
		getActDetailComment();
        getActMoreAddress();
		getActDetail();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.ivErrorImg:
			if (!ClickUtil.isFastClick()) {
				getActDetailStrategy();
				getActDetailMemory();
				getActDetailVip();
				getActDetailComment();
                getActMoreAddress();
				getActDetail();
			}
			break;
		case R.id.ivOnePic:
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(ActDetail.this, TrendsPicGallery.class);
				intent.putExtra("LIST", actModel.getAct_imgs());
				intent.putExtra("POSITION", 0);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
			break;
		case R.id.ivActLove:
			if (!ClickUtil.isFastClick()) {
				if (hasLogin) {
					switch (actModel.getIs_loved()) {
					case -1:
					case 0:
						AddCollectAct();
						break;
					case 1:
						CancelCollectAct();
						break;
					}
				} else {
					DialogLogin();
				}
			}
			break;
		case R.id.llAddress:
			if (!ClickUtil.isFastClick() && null != addressList && addressList.get(0).getLat() != 0) {
				Intent intent = new Intent(ActDetail.this, ActLocationAndCarPlaceMap.class);
				intent.putExtra("MODEL", addressList.get(0));
				startActivity(intent);
			}
			break;
		case R.id.tvActCommentAll:
		case R.id.llActCommentItem:
		case R.id.llComment:
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(ActDetail.this, ActDetailComment.class);
				intent.putExtra("ACT_ID", actId);
				startActivity(intent);
			}
			break;
		case R.id.llWeChat:
			if (!ClickUtil.isFastClick()) {
				ShareToWeChat();
			}
			break;
		case R.id.llSquare:
			if (!ClickUtil.isFastClick()) {
				ShareToSquare();
			}
			break;
		case R.id.llZoon:
			if (!ClickUtil.isFastClick()) {
				ShareToZone();
			}
			break;
		case R.id.llSina:
			if (!ClickUtil.isFastClick()) {
				if (!AppPreference.getUserPersistent(ActDetail.this, AppPreference.SINA_ID).equals("")) {
					Oauth2AccessToken token = new Oauth2AccessToken();
					token.setUid(AppPreference.getUserPersistent(ActDetail.this, AppPreference.SINA_ID));
					token.setToken(AppPreference.getUserPersistent(ActDetail.this, AppPreference.SINA_TOKEN));
					token.setExpiresTime(AppPreference.getUserPersistentLong(ActDetail.this, AppPreference.SINA_EXPIRES));
					if (!token.isSessionValid()) {
						if (checkSinaPackage()) {
							mSsoHandler = new SsoHandler(ActDetail.this, mWeiboAuth);
							mSsoHandler.authorize(new AuthListener());
						} else {
							mWeiboAuth.anthorize(new AuthListener());
						}
					} else {
						ShareToSina();
					}
				} else {
					if (checkSinaPackage()) {
						mSsoHandler = new SsoHandler(ActDetail.this, mWeiboAuth);
						mSsoHandler.authorize(new AuthListener());
					} else {
						mWeiboAuth.anthorize(new AuthListener());
					}
				}
			}
			break;
		case R.id.llEnroll:
			if (!ClickUtil.isFastClick()) {
				if (hasLogin) {
                    if (actMoreInfoModel.getEnroll_limit() == 1 && actMoreInfoModel.getEnroll_num() < actMoreInfoModel.getEnroll_limit_num()
                            && (actMoreInfoModel.getLimit_sex_num() == 0) || (actMoreInfoModel.getLimit_sex_num() == 1 && ((AppPreference.getUserPersistentInt(ActDetail.this, AppPreference.USER_SEX) == 1 &&actMoreInfoModel.getEnroll_male_num() < actMoreInfoModel.getLimit_male_num()) ||(AppPreference.getUserPersistentInt(ActDetail.this, AppPreference.USER_SEX) == 2 &&actMoreInfoModel.getEnroll_female_num() < actMoreInfoModel.getLimit_female_num())))) {
                        Intent intent = new Intent(ActDetail.this, ActEnrollInfo.class);
                        intent.putExtra("ID", actId);
                        intent.putExtra("MORE_INFO", actMoreInfoModel);
                        startActivityForResult(intent, 400);
                    } else {
                        if (actMoreInfoModel.getEnroll_num() >= actMoreInfoModel.getEnroll_limit_num()) {
                            if (dialog != null && !dialog.isShowing()) {
                                dialog.setMessage("报名人数已满").withEffect(Effectstype.Shake).show();
                            }
                            return;
                        }
                        if (AppPreference.getUserPersistentInt(ActDetail.this, AppPreference.USER_SEX) == 1) {
                            if (dialog != null && !dialog.isShowing()) {
                                dialog.setMessage("报名男性人员已满").withEffect(Effectstype.Shake).show();
                            }
                            return;
                        }
                        if (AppPreference.getUserPersistentInt(ActDetail.this, AppPreference.USER_SEX) == 2) {
                            if (dialog != null && !dialog.isShowing()) {
                                dialog.setMessage("报名女性人员已满").withEffect(Effectstype.Shake).show();
                            }
                            return;
                        }
                    }
				} else {
					DialogLogin();
				}
			}
			break;
        case R.id.llEnrollStatus:
            if (!ClickUtil.isFastClick() && actModel != null && modulesStatusModel != null && actMoreInfoModel != null){
                Intent intent = new Intent(ActDetail.this, ActEnrollStatus.class);
                intent.putExtra("MODEL", actModel);
                intent.putExtra("MODULE", modulesStatusModel);
                intent.putExtra("MORE_INFO", actMoreInfoModel);
                startActivity(intent);
            }
            break;
		}
	}

	@SuppressWarnings("deprecation")
	private void setActDetailMessage() {
		tvTitle.setText(actModel.getIntro());
		tvActMark.setVisibility(View.INVISIBLE);
		if (actModel.getCost() != 0) {
			tvActCost.setText("¥" + actModel.getCost());
		} else {
			tvActCost.setText("免费");
		}
		if (!ivActLove.isShown()) {
			ivActLove.setVisibility(View.VISIBLE);
		}
		if (actModel.getIs_loved() == 1) {
			ivActLove.setImageResource(R.drawable.icon_act_title_love_press);
		} else {
			ivActLove.setImageResource(R.drawable.icon_act_title_love_normal);
		}
		tvActDetailContent.setText(actModel.getDetail());
		if (!actModel.getAddr_route().equals("")) {
			llActBus.setVisibility(View.VISIBLE);
			tvActBus.setText(actModel.getAddr_route());
		} else {
			llActBus.setVisibility(View.GONE);
		}
        if (hasLogin) {
            if (modulesStatusModel.getShow_enroll() == 1) {
                if (actMoreInfoModel.getEnroll_status() == -1 || actMoreInfoModel.getEnroll_status() == 0) {
                    llEnroll.setVisibility(View.VISIBLE);
                    menuLine.setVisibility(View.VISIBLE);
                    llEnrollStatus.setVisibility(View.VISIBLE);
                    enrollstatusLine.setVisibility(View.VISIBLE);
                } else {
                    llEnroll.setVisibility(View.GONE);
                    menuLine.setVisibility(View.GONE);
                    llEnrollStatus.setVisibility(View.VISIBLE);
                    enrollstatusLine.setVisibility(View.VISIBLE);
                }
            } else {
                llEnroll.setVisibility(View.GONE);
                menuLine.setVisibility(View.GONE);
                llEnrollStatus.setVisibility(View.GONE);
                enrollstatusLine.setVisibility(View.GONE);
            }
        } else {
            llEnroll.setVisibility(View.VISIBLE);
            menuLine.setVisibility(View.VISIBLE);
            llEnrollStatus.setVisibility(View.GONE);
            enrollstatusLine.setVisibility(View.GONE);
        }
		if (actModel.getAct_imgs().size() > 1) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case 0:
						mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
						if (application.isRun && !application.isDown) {
							this.sendEmptyMessageDelayed(0, sleepTime);
						}
						break;
					case 1:
						if (application.isRun && !application.isDown) {
							this.sendEmptyMessageDelayed(0, sleepTime);
						}
						break;
					}
				}
			};
			application.isRun = true;
			mHandler.sendEmptyMessageDelayed(0, sleepTime);

			progressOnePic.setVisibility(View.GONE);
			ivOnePic.setVisibility(View.GONE);
			mViewPager.setVisibility(View.VISIBLE);
			mImageList = new ArrayList<ImageView>();
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			for (int i = 0; i < actModel.getAct_imgs().size(); i++) {
				ImageView imageView = new ImageView(ActDetail.this);
				imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (metrics.density * 7 + 0.5f), (int) (metrics.density * 7 + 0.5f)));
				imageView.setBackgroundDrawable(mIndicatorUnfocused);
				mImageList.add(imageView);
				mLinearLayout.addView(imageView);
				if (i != actModel.getAct_imgs().size() - 1) {
					View view = new View(ActDetail.this);
					view.setLayoutParams(new ViewGroup.LayoutParams((int) (metrics.density * 5 + 0.5f), 1));
					mLinearLayout.addView(view);
				}
			}
			mImageList.get(0).setBackgroundDrawable(mIndicatorFocused);
			pagerAdapter = new InfiniteLoopViewPagerAdapter(new ActDetailPicAdapter());
			mViewPager.setInfinateAdapter(mHandler, pagerAdapter);
			mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		} else if (actModel.getAct_imgs().size() == 1) {
			ivOnePic.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			progressOnePic.setVisibility(View.GONE);
			imageLoader.displayImage(actModel.getAct_imgs().get(0).getImg_url(), ivOnePic, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					progressOnePic.setProgress(0);
					progressOnePic.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					progressOnePic.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					progressOnePic.setVisibility(View.GONE);
				}
			}, new ImageLoadingProgressListener() {
				@Override
				public void onProgressUpdate(String imageUri, View view, int current, int total) {
					progressOnePic.setProgress(Math.round(100.0f * current / total));
				}
			});
		} else {
			ivOnePic.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			progressOnePic.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取活动详情
	 */
	private void getActDetail() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("正在加载中...");
			mLoadingDialog.show();
		}
		ActDetailParam param = new ActDetailParam(actId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                ActDetailModel model = gson.fromJson(result, ActDetailModel.class);
                if (model.getAct() != null) {
                    actModel = model.getAct();
                    getActMoreInfo();
                } else {
                    toast("活动信息有误");
                    finish();
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
                errorView();
            }
        });
	}

    /**
     * 获取更多活动信息
     */
    private void getActMoreInfo() {
        ActMoreInfoParam param = new ActMoreInfoParam(actId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    actMoreInfoModel = gson.fromJson(object.getString("act_info"), ActMoreInfoModel.class);
                    if (actMoreInfoModel != null) {
                        getActModulesStatus();
                    } else {
                        toast("活动信息有误");
                        finish();
                    }
                } catch (JSONException e) {
                    actMoreInfoModel = null;
                    e.printStackTrace();
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
                errorView();
            }
        });
    }

    /**
     * 获取活动模块信息
     */
    private void getActModulesStatus() {
        ActModulesStatusParam param = new ActModulesStatusParam(actId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    modulesStatusModel = gson.fromJson(object.getString("act_modules"), ActModulesStatusModel.class);
                    if (modulesStatusModel != null) {
                        setActDetailMessage();
                        if (llError.isShown()) {
                            llError.setVisibility(View.GONE);
                        }
                        if (!scrollView.isShown()) {
                            scrollView.setVisibility(View.VISIBLE);
                            scrollView.startAnimation(alphaIn);
                        }
                        if (!llMenuBar.isShown()) {
                            llMenuBar.setVisibility(View.VISIBLE);
                        }
                    } else {
                        toast("活动信息有误");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                errorView();
            }
        });
    }

	/**
	 * 活动相关攻略
	 */
	private void getActDetailStrategy() {
		ActDetailRelationParam param = new ActDetailRelationParam(actId, 1, 1, 1);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                NewsModelList newsModel = gson.fromJson(result, NewsModelList.class);
                if (newsModel != null && newsModel.getNews().size() > 0) {
                    tvActStrategy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    tvActStrategy.setTextColor(0xFFFF9933);
                    tvActStrategy.setText(newsModel.getNews().get(0).getTitle());
                    llActStrategy.setVisibility(View.VISIBLE);
                    tvActStrategy.setOnClickListener(new OnStretegyAndMemoryClickListener(newsModel.getNews().get(0), "攻略详情"));
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("获取攻略失败");
            }
        });
	}

	/**
	 * 活动相关回顾
	 */
	private void getActDetailMemory() {
		ActDetailRelationParam param = new ActDetailRelationParam(actId, 2, 1, 1);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                NewsModelList newsModel = gson.fromJson(result, NewsModelList.class);
                if (newsModel != null && newsModel.getNews().size() > 0) {
                    tvActMemory.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    tvActMemory.setTextColor(0xFFFF9933);
                    tvActMemory.setText(newsModel.getNews().get(0).getTitle());
                    llActMemory.setVisibility(View.VISIBLE);
                    tvActMemory.setOnClickListener(new OnStretegyAndMemoryClickListener(newsModel.getNews().get(0), "记忆详情"));
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("获取回顾失败");
            }
        });
	}

	/**
	 * 活动相关达人
	 */
	private void getActDetailVip() {
		ActDetailVipParam param = new ActDetailVipParam(application.getCityId(), actId, 1, 4);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                UserList list = gson.fromJson(result, UserList.class);
                if (list != null && list.getUsers().size() > 0) {
                    vipAdapter = new ActDetailVipAdapter(ActDetail.this, list.getUsers());
                    vipGridView.setAdapter(vipAdapter);
                    llActVip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("获取达人失败");
            }
        });
	}

    /**
     * 获取更多地址信息
     */
    private void getActMoreAddress() {
        ActMoreAddressParam param = new ActMoreAddressParam(actId);
        AsyncHttpTask.post(param.getUrl(),param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                ActAddressAndCarLocationListModel list = gson.fromJson(result, ActAddressAndCarLocationListModel.class);
                if (list != null && list.getAct_addrs().size() > 0) {
                    addressList = new ArrayList<ActAddressModel>();
                    addressList = list.getAct_addrs();
                    tvAddress.setText(setAddressInfo(list.getAct_addrs().get(0)));
                    tvAddressDetail.setText(list.getAct_addrs().get(0).getAddr_name());
                    llAddressLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("获取地址失败");
            }
        });
    }

	/**
	 * 攻略，回顾点击
	 */
	private class OnStretegyAndMemoryClickListener implements OnClickListener {

		private NewsModel model;
		private String title;

		public OnStretegyAndMemoryClickListener(NewsModel model, String title) {
			this.model = model;
			this.title = title;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(ActDetail.this, WebStrategy.class);
				intent.putExtra("MODEL", model);
				intent.putExtra("TITLE", title);
				startActivity(intent);
			}
		}
	}

	/**
	 * 活动评论
	 */
	private void getActDetailComment() {
		ActCommentListParam param = new ActCommentListParam(actId, 1, 1);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                ActCommentModelList list = gson.fromJson(result, ActCommentModelList.class);
                if (list != null && list.getComments().size() > 0) {
                    tvActCommentNum.setText(list.getTotal_num() + "条");
                    tvUserName.setText(list.getComments().get(0).getUser().getNick_name());
                    tvUserComment.setText(list.getComments().get(0).getContent());
                    imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(list.getComments().get(0).getUser().getHead_img_url(), 150, 150, 50), ivUserIcon, userOptions);
                    llActComment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("获取评论失败");
            }
        });
	}

	/**
	 * 添加兴趣活动
	 */
	private void AddCollectAct() {
		mLoadingDialog.setMessage("关注中...");
		mLoadingDialog.show();
		CollectActParam param = new CollectActParam(actId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                actModel.setIs_loved(1);
                ivActLove.setImageResource(R.drawable.icon_act_title_love_press);
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
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	/**
	 * 取消兴趣活动
	 */
	private void CancelCollectAct() {
		mLoadingDialog.setMessage("正在取消...");
		mLoadingDialog.show();
		CancelCollectActParam param = new CancelCollectActParam(actId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                actModel.setIs_loved(-1);
                ivActLove.setImageResource(R.drawable.icon_act_title_love_normal);
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
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	/**
	 * 图片切换监听
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onPageSelected(int position) {
			if (mImageList.size() > 1) {
				int size = mImageList.size();
				if (position >= 100000 * size) {
					position = position - 100000 * size;
					for (int i = 0; i < size; i++) {
						if (i == position % size)
							mImageList.get(i).setBackgroundDrawable(mIndicatorFocused);
						else
							mImageList.get(i).setBackgroundDrawable(mIndicatorUnfocused);
					}
				} else {
					position = 100000 * size - position;
					for (int i = size - 1; i >= 0; i--) {
						int index;
						if (position % size == 0) {
							index = size;
						} else {
							index = position % size;
						}
						if (i == size - index)
							mImageList.get(i).setBackgroundDrawable(mIndicatorFocused);
						else
							mImageList.get(i).setBackgroundDrawable(mIndicatorUnfocused);
					}
				}
			}
		}
	}

	/**
	 * 装载图片Adapter
	 */
	private class ActDetailPicAdapter extends com.gather.android.widget.MyPagerAdapter {
		@Override
		public int getCount() {
			return actModel.getAct_imgs().size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (View) object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final ViewHolder holder;
			LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = mInflater.inflate(R.layout.item_act_detail_pic, container, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.imageView);
			holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
			holder.imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick()) {
						Intent intent = new Intent(ActDetail.this, TrendsPicGallery.class);
						intent.putExtra("LIST", actModel.getAct_imgs());
						intent.putExtra("POSITION", position);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}
			});
			imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(actModel.getAct_imgs().get(position).getImg_url(), 500, 500, 50), holder.imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					holder.progressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					holder.progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					holder.progressBar.setVisibility(View.GONE);
				}
			});
			container.addView(view);
			return view;
		}
	}

	private static class ViewHolder {
		public ImageView imageView;
		public ProgressBar progressBar;
	}

	/**
	 * 绑定新浪微博
	 */
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			AppPreference.save(ActDetail.this, AppPreference.SINA_ID, mAccessToken.getUid());
			AppPreference.save(ActDetail.this, AppPreference.SINA_TOKEN, mAccessToken.getToken());
			AppPreference.save(ActDetail.this, AppPreference.SINA_EXPIRES, mAccessToken.getExpiresTime());
			ShareToSina();
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onWeiboException(WeiboException e) {

		}
	}

	/**
	 * 分享到微信好友
	 */
	private void ShareToWeChat() {
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID);
		wxApi.registerApp(Constant.WE_CHAT_APPID);
		if (wxApi.isWXAppInstalled() && wxApi.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = actModel.getShare_url();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = actModel.getTitle();
			msg.description = Share_Message;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.setThumbImage(thumb);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			wxApi.sendReq(req);
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本微信客户端").withEffect(Effectstype.Shake).show();
			}
		}
	}

	/**
	 * 分享到微信朋友圈
	 */
	private void ShareToSquare() {
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID);
		wxApi.registerApp(Constant.WE_CHAT_APPID);
		if (wxApi.isWXAppInstalled() && wxApi.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = actModel.getShare_url();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = actModel.getTitle();
			msg.description = Share_Message;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.setThumbImage(thumb);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			wxApi.sendReq(req);
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本微信客户端").withEffect(Effectstype.Shake).show();
			}
		}
	}

	/**
	 * 分享到QQ空间
	 */
	private void ShareToZone() {
		if (checkQQPackage()) {
			final Bundle params = new Bundle();
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, Share_Message);// 必填
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, actModel.getShare_url());// 必填
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, actModel.getTitle());// 选填
			ArrayList<String> imageUrls = new ArrayList<String>();
			imageUrls.add(actModel.getHead_img_url());
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
			params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
			new Thread(new Runnable() {
				@Override
				public void run() {
					mTencent.shareToQzone(ActDetail.this, params, new IUiListener() {
						@Override
						public void onError(UiError arg0) {
							if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
								mLoadingDialog.dismiss();
							}
							handler.sendEmptyMessage(0);
						}

						@Override
						public void onComplete(Object arg0) {
							if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
								mLoadingDialog.dismiss();
							}
							handler.sendEmptyMessage(1);
						}

						@Override
						public void onCancel() {
							if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
								mLoadingDialog.dismiss();
							}
						}
					});
				}
			}).start();
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本手机QQ或者QQ空间").withEffect(Effectstype.Shake).show();
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				toast("QQ空间分享失败，请重试");
				break;
			case 1:
				TCAgent.onEvent(ActDetail.this, "分享到QQ空间");
				toast("QQ空间分享成功");
				break;
			}
		}
	};

	/**
	 * 分享到新浪微博
	 */
	private void ShareToSina() {
		mLoadingDialog.setMessage("分享到新浪微博...");
		mLoadingDialog.show();
        RequestParams params = new RequestParams();
        params.put("access_token", AppPreference.getUserPersistent(ActDetail.this, AppPreference.SINA_TOKEN));
        params.put("status", "#" + actModel.getTitle() + "#" + Share_Message + "下载链接：" + Constant.OFFICIAL_WEB);
        params.put("url", actModel.getHead_img_url());
        AsyncHttpTask.post("https://api.weibo.com/2/statuses/upload_url_text.json", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                TCAgent.onEvent(ActDetail.this, "分享到新浪微博");
                toast("分享成功");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("分享失败，请重试");
            }
        });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
        if (resultCode == RESULT_OK) {
            if (requestCode == 400) {
                actMoreInfoModel.setEnroll_status(0);
                llEnroll.setVisibility(View.GONE);
                menuLine.setVisibility(View.GONE);
                llEnrollStatus.setVisibility(View.VISIBLE);
                enrollstatusLine.setVisibility(View.VISIBLE);
            }
        }
	}

	/**
	 * 活动地址显示规则
	 */
	private String setAddressInfo(ActAddressModel actAddressModel) {
		StringBuffer sb = new StringBuffer();
		if (!actAddressModel.getAddr_city().equals("")) {
			sb.append(actAddressModel.getAddr_city());
			if (!actAddressModel.getAddr_area().equals("") || !actAddressModel.getAddr_road().equals("")) {
				sb.append("，");
			} else {
				return sb.toString();
			}
		}
		if (!actAddressModel.getAddr_area().equals("")) {
			sb.append(actAddressModel.getAddr_area());
			if (!actAddressModel.getAddr_road().equals("")) {
				sb.append("，");
			} else {
				return sb.toString();
			}
		}
		if (!actAddressModel.getAddr_road().equals("")) {
			sb.append(actAddressModel.getAddr_road());
		}
		return sb.toString();
	}

	/**
	 * 活动时间显示规则
	 */
	private String setTimeInfo() {
		StringBuffer sb = new StringBuffer();
		String begin_time = TimeUtil.getActDetailTime(actModel.getB_time());
		String end_time = TimeUtil.getActDetailTime(actModel.getE_time());
		if (!begin_time.equals("")) {
			sb.append(begin_time);
			if (!end_time.equals("")) {
				if (begin_time.substring(0, begin_time.indexOf(" ")).equals(end_time.substring(0, end_time.indexOf(" ")))) {
					sb.append(" 一 ");
					sb.append(end_time.substring(end_time.indexOf(" ") + 1, end_time.length()));
				} else {
					sb.append(" 一 ");
					sb.append(end_time);
				}
				begin_time = null;
				end_time = null;
				return sb.toString();
			} else {
				begin_time = null;
				end_time = null;
				return sb.toString();
			}
		} else {
			begin_time = null;
			end_time = null;
			return sb.toString();
		}
	}

	/**
	 * 检测新浪微博是否安装
	 * 
	 */
	private boolean checkSinaPackage() {
		PackageManager manager = getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.sina.weibo")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测QQ是否安装
	 * 
	 */
	private boolean checkQQPackage() {
		PackageManager manager = getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.tencent.mobileqq")) {
				return true;
			}
		}
		return false;
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

	/**
	 * 需要去登录
	 */
	private void DialogLogin() {
		if (choiceBuilder != null && !choiceBuilder.isShowing()) {
			choiceBuilder.setMessage("想看更多内容，现在就去登录吧？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (PushManager.isPushEnabled(getApplicationContext())) {
						PushManager.stopWork(getApplicationContext());
					}
					application.setUserInfoModel(null);
					AppPreference.clearInfo(ActDetail.this);
					Intent intent = new Intent(ActDetail.this, LoginIndex.class);
					startActivity(intent);
					choiceBuilder.dismiss();
				}
			}).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mHandler != null) {
			application.isRun = false;
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mHandler != null) {
			application.isRun = true;
			mHandler.sendEmptyMessageDelayed(0, sleepTime);
		}
	}

	private void errorView() {
		llError.setVisibility(View.VISIBLE);
		scrollView.setVisibility(View.GONE);
		llMenuBar.setVisibility(View.GONE);
	}
}
