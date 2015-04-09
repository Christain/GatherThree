package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.TrendsCommentAdapter;
import com.gather.android.adapter.TrendsCommentAdapter.OnCommentClickListener;
import com.gather.android.adapter.TrendsCommentAdapter.OnCommentLongClickListener;
import com.gather.android.adapter.TrendsPicAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.TrendsCommentListModel;
import com.gather.android.model.TrendsCommentModel;
import com.gather.android.model.TrendsModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.DelCommentParam;
import com.gather.android.params.DelTrendsParam;
import com.gather.android.params.SendCommentParam;
import com.gather.android.params.TrendDetailParam;
import com.gather.android.params.TrendsCommentListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.OverScrollView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 动态评论
 */
public class TrendsComment extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight, ivUserIcon, ivPic;
	private TextView tvLeft, tvTitle, tvRight, tvUserName, tvContent, tvTime, tvDel, tvComment;
	private NoScrollListView listView;
	private TrendsCommentAdapter commentAdapter;
	private NoScrollGridView gridView;
	private TrendsPicAdapter picAdapter;
	private EditText etContent;
	private Button btComment;
	private OverScrollView scrollView;
	private LinearLayout llItem;

	/**
	 * FooterView
	 */
	private View footerView;
	private TextView tvFooter;
	private RelativeLayout rlFooter;
	private ProgressBar pbFooter;

	private TrendsModel model = null;
	private int myUserId, trendsId = 0, page = 1, size = 10, totalNum, maxPage, isOver;
	private boolean isRequest = false, isCommentRefresh = false, numChanged = false, hasPosition = false;
	private int position;// 如果是从动态列表进来的，会传position过来

	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	private static final int REFRESH = 0x10;
	private static final int LOADMORE = 0x11;
	private int loadType;

	private UserInfoModel atModel;// 评论某人

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options, picOptions;
	private GatherApplication application;

	@Override
	protected int layoutResId() {
		return R.layout.trends_comment;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("MODEL") || intent.hasExtra("ID")) {
			if (intent.hasExtra("MODEL")) {
				model = (TrendsModel) intent.getSerializableExtra("MODEL");
			}
			if (intent.hasExtra("ID")) {
				trendsId = intent.getExtras().getInt("ID");
			}
			if (intent.hasExtra("POSITION")) {
				position = intent.getExtras().getInt("POSITION");
				hasPosition = true;
			}
			this.application = (GatherApplication) getApplication();
			this.dialog = DialogTipsBuilder.getInstance(TrendsComment.this);
			this.mLoadingDialog = LoadingDialog.createDialog(TrendsComment.this, true);
			this.myUserId = AppPreference.getUserPersistentInt(TrendsComment.this, AppPreference.USER_ID);
			this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
			this.picOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);
			this.ivPic = (ImageView) findViewById(R.id.ivPic);

			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvUserName = (TextView) findViewById(R.id.tvUserName);
			this.tvContent = (TextView) findViewById(R.id.tvContent);
			this.tvTime = (TextView) findViewById(R.id.tvTime);
			this.tvDel = (TextView) findViewById(R.id.tvDel);
			this.tvComment = (TextView) findViewById(R.id.tvComment);
			this.listView = (NoScrollListView) findViewById(R.id.listview);
			this.gridView = (NoScrollGridView) findViewById(R.id.gridView);
			this.etContent = (EditText) findViewById(R.id.etContent);
			this.btComment = (Button) findViewById(R.id.btComment);
			this.scrollView = (OverScrollView) findViewById(R.id.ScrollView);
			this.llItem = (LinearLayout) findViewById(R.id.llItem);

			this.tvComment.setOnClickListener(this);
			this.btComment.setOnClickListener(this);
			this.ivLeft.setOnClickListener(this);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.GONE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.tvTitle.setText("评论");
			this.tvLeft.setVisibility(View.GONE);

			LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.footerView = mInflater.inflate(R.layout.item_trends_comment_footer, null);
			this.tvFooter = (TextView) footerView.findViewById(R.id.tvFooter);
			this.pbFooter = (ProgressBar) footerView.findViewById(R.id.pbFooter);
			this.rlFooter = (RelativeLayout) footerView.findViewById(R.id.rlFooter);
			this.rlFooter.setOnClickListener(TrendsComment.this);
			this.listView.addFooterView(footerView);
			this.commentAdapter = new TrendsCommentAdapter(TrendsComment.this);
			this.listView.setAdapter(commentAdapter);
			this.commentAdapter.setOnCommentClickListener(new OnCommentClickListener() {
				@Override
				public void OnCommentClick(TrendsCommentModel model, int position) {
					openKeyboard();
					etContent.requestFocus();
					etContent.setText("");
					etContent.setHint("回复" + model.getAuthor_user().getNick_name());
					atModel = model.getAuthor_user();
				}
			});
			this.commentAdapter.setOnCommentLongClickListener(new OnCommentLongClickListener() {
				@Override
				public void OnCommentLongClick(TrendsCommentModel model, int position) {
					if (model != null && model.getAuthor_id() == myUserId) {
						DelCommentDialog dialog = new DelCommentDialog(TrendsComment.this, R.style.dialog_del_message, position, model.getContent(), model.getId());
						dialog.show();
					}
				}
			});

			this.initView();
		} else {
			toast("评论页面信息错误");
			finish();
		}
	}

	private void initView() {
		if (null != model) {
			setTrendsContent();
		} else {
			llItem.setVisibility(View.INVISIBLE);
			getTrendDetail();
		}
		page = 1;
		isOver = 0;
		isCommentRefresh = true;
		loadType = REFRESH;
		rlFooter.setBackgroundColor(0xFFFFFFFF);
		pbFooter.setVisibility(View.VISIBLE);
		tvFooter.setVisibility(View.GONE);
		getCommentList();
	}

	private void setTrendsContent() {
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getUser().getHead_img_url(), 100, 100, 50), ivUserIcon, options);
		tvUserName.setText(model.getUser().getNick_name());
		tvContent.setText(model.getContent());
		tvTime.setText(TimeUtil.getTrendsTime(TimeUtil.getStringtoLong(model.getCreate_time())));
		if (model.getAuthor_id() == myUserId) {
			tvDel.setVisibility(View.VISIBLE);
			tvDel.setOnClickListener(this);
		} else {
			tvDel.setVisibility(View.GONE);
		}
		switch (model.getType()) {
		case 0:
			ivPic.setVisibility(View.GONE);
			gridView.setVisibility(View.GONE);
			break;
		case 1:
			ivPic.setVisibility(View.VISIBLE);
			imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImgs().getImgs().get(0).getImg_url(), 150, 150, 50), ivPic, picOptions);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivPic.getLayoutParams();
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			params.width = metrics.widthPixels / 3;
			params.height = params.width;
			ivPic.setLayoutParams(params);
			ivPic.setOnClickListener(this);
			gridView.setVisibility(View.GONE);
			break;
		case 2:
			ivPic.setVisibility(View.GONE);
			picAdapter = new TrendsPicAdapter(TrendsComment.this, model.getImgs().getImgs(), 3);
			gridView.setAdapter(picAdapter);
			gridView.setVisibility(View.VISIBLE);
			break;
		}
		atModel = null;
		etContent.setHint("");
		if (!llItem.isShown()) {
			llItem.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 获取动态详情
	 */
	private void getTrendDetail() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("加载中...");
			mLoadingDialog.show();
		}
		TrendDetailParam param = new TrendDetailParam(trendsId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                try {
                    JSONObject object = new JSONObject(result);
                    model = gson.fromJson(object.getString("dynamic"), TrendsModel.class);
                    if (model != null) {
                        setTrendsContent();
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
                finish();
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("获取动态失败");
                finish();
            }
        });
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
					DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(TrendsComment.this);
					dialog.setMessage("还未评论，是否确定退出？").withDuration(300).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (numChanged && hasPosition) {
								Intent intent = new Intent();
								intent.putExtra("MODEL", model);
								intent.putExtra("POSITION", position);
								setResult(RESULT_OK, intent);
							}
							finish();
						}
					}).show();
				} else {
					if (numChanged && hasPosition) {
						Intent intent = new Intent();
						intent.putExtra("MODEL", model);
						intent.putExtra("POSITION", position);
						setResult(RESULT_OK, intent);
					}
					finish();
				}
			}
			break;
		case R.id.ivPic:
			if (!ClickUtil.isFastClick() && model != null) {
				Intent intent = new Intent(TrendsComment.this, TrendsPicGallery.class);
				intent.putExtra("LIST", model.getImgs().getImgs());
				intent.putExtra("POSITION", 0);
				TrendsComment.this.startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
			break;
		case R.id.tvDel:
			if (!ClickUtil.isFastClick()) {
				if (!isRequest) {
					isRequest = true;
					DelTrends(model.getId());
				} else {
					toast("正在删除");
				}
			}
			break;
		case R.id.rlFooter:
			if (!ClickUtil.isFastClick() && !isCommentRefresh && isOver != 1) {
				tvFooter.setVisibility(View.GONE);
				pbFooter.setVisibility(View.VISIBLE);
				loadType = LOADMORE;
				getCommentList();
			}
			break;
		case R.id.btComment:
			if (!ClickUtil.isFastClick()) {
				if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
					closeKeyboard();
					String content = etContent.getText().toString().trim();
					etContent.setText("");
					SendComment(model.getId(), atModel, content);
				} else {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入回复内容").withEffect(Effectstype.Shake).show();
					}
				}
			}
			break;
		case R.id.tvComment:
			if (!ClickUtil.isFastClick()) {
				etContent.requestFocus();
				openKeyboard();
				atModel = null;
				etContent.setHint("");
				etContent.setText("");
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
				DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(TrendsComment.this);
				dialog.setMessage("还未评论，是否确定退出？").withDuration(300).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (numChanged && hasPosition) {
							Intent intent = new Intent();
							intent.putExtra("MODEL", model);
							intent.putExtra("POSITION", position);
							setResult(RESULT_OK, intent);
						}
						finish();
					}
				}).show();
			} else {
				if (numChanged && hasPosition) {
					Intent intent = new Intent();
					intent.putExtra("MODEL", model);
					intent.putExtra("POSITION", position);
					setResult(RESULT_OK, intent);
				}
				finish();
			}
		}
		return true;
	}

	/**
	 * 删除动态
	 */
	private void DelTrends(int dynamicId) {
		mLoadingDialog.setMessage("正在删除...");
		mLoadingDialog.show();
		DelTrendsParam param = new DelTrendsParam(dynamicId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                toast("已成功删除");
                Intent intent = new Intent();
                intent.putExtra("POSITION", position);
                intent.putExtra("DEL", "");
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("删除失败，请重试").withEffect(Effectstype.Shake).show();
                }
            }
        });
	}

	/**
	 * 获取评论
	 */
	private void getCommentList() {
		TrendsCommentListParam param = new TrendsCommentListParam(model.getId(), page, size);
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
                        isCommentRefresh = false;
                        return;
                    } finally {
                        object = null;
                    }
                }
                Gson gson = new Gson();
                TrendsCommentListModel list = gson.fromJson(result, TrendsCommentListModel.class);
                if (list != null && list.getComments() != null) {
                    switch (loadType) {
                        case REFRESH:
                            if (totalNum == 0) {
                                isOver = 1;
                                refreshOver(returnCode, "ISNULL");
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(returnCode, "ISOVER");
                            } else {
                                page++;
                                refreshOver(returnCode, "CLICK_MORE");
                            }
                            commentAdapter.refreshItems(list.getComments());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(returnCode, "CLICK_MORE");
                            } else {
                                isOver = 1;
                                loadMoreOver(returnCode, "ISOVER");
                            }
                            commentAdapter.addItems(list.getComments());
                            break;
                    }
                } else {
                    switch (loadType) {
                        case REFRESH:
                            refreshOver(returnCode, "ISNULL");
                            break;
                        case LOADMORE:
                            isOver = 1;
                            loadMoreOver(returnCode, "ISOVER");
                            break;
                    }
                }
                isCommentRefresh = false;
            }

            @Override
            public void onNeedLogin(String msg) {
                isCommentRefresh = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("获取评论失败，请重试").withEffect(Effectstype.Shake).show();
                }
                isCommentRefresh = false;
            }
        });
	}

	private void refreshOver(int code, String msg) {
		if (msg.equals("ISNULL")) {
			rlFooter.setVisibility(View.VISIBLE);
			rlFooter.setBackgroundColor(0xFFFFFFFF);
			pbFooter.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
			tvFooter.setText("没有评论");
		} else if (msg.equals("CLICK_MORE")) {
			rlFooter.setVisibility(View.VISIBLE);
			rlFooter.setBackgroundResource(R.drawable.trends_comment_footer_click_style);
			pbFooter.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
			tvFooter.setText("点击更多");
		} else if (msg.equals("ISOVER")) {
			rlFooter.setVisibility(View.GONE);
		}
	}

	private void loadMoreOver(int code, String msg) {
		if (msg.equals("ISNULL")) {
			rlFooter.setVisibility(View.GONE);
		} else if (msg.equals("CLICK_MORE")) {
			rlFooter.setVisibility(View.VISIBLE);
			rlFooter.setBackgroundResource(R.drawable.trends_comment_footer_click_style);
			pbFooter.setVisibility(View.GONE);
			tvFooter.setVisibility(View.VISIBLE);
			tvFooter.setText("点击更多");
		} else if (msg.equals("ISOVER")) {
			rlFooter.setVisibility(View.GONE);
		}
	}

	/**
	 * 发评论
	 */
	private void SendComment(int trendsId, final UserInfoModel atModel, final String content) {
		SendCommentParam param;
		if (atModel != null) {
			param = new SendCommentParam(trendsId, atModel.getUid(), content);
		} else {
			param = new SendCommentParam(trendsId, content);
		}
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (isOver == 1) {
                    TrendsCommentModel model = new TrendsCommentModel();
                    if (atModel != null) {
                        model.setAt_user(atModel);
                        model.setAt_id(atModel.getUid());
                    }
                    model.setAuthor_user(application.getUserInfoModel());
                    model.setAuthor_id(application.getUserInfoModel().getUid());
                    model.setContent(content);
                    commentAdapter.addItem(model, 0);
                    if (rlFooter.isShown() && tvFooter.getText().toString().contains("没有评论")) {
                        rlFooter.setVisibility(View.GONE);
                    }
                }
                numChanged = true;
                TrendsComment.this.model.setComment_num(TrendsComment.this.model.getComment_num() + 1);
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("回复失败");
            }
        });
	}
	
	private class DelCommentDialog extends Dialog {

		private Context context;
		private int position;
		private String content;
		private int id;

		public DelCommentDialog(Context context, int position, String content, int id) {
			super(context);
			this.context = context;
			this.position = position;
			this.content = content;
			this.id = id;
		}

		public DelCommentDialog(Context context, int theme, int position, String content, int id) {
			super(context, theme);
			this.context = context;
			this.position = position;
			this.content = content;
			this.id = id;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_del_chat_message);
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			getWindow().setAttributes((WindowManager.LayoutParams) params);
			setCanceledOnTouchOutside(true);
			TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
			View line = (View) findViewById(R.id.line);
			tvUserName.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			TextView tvCopy = (TextView) findViewById(R.id.tvCopy);
			TextView tvDel = (TextView) findViewById(R.id.tvDel);

			tvCopy.setOnClickListener(new View.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick()) {
						dismiss();
						if (Build.VERSION.SDK_INT > 11) {
							android.content.ClipboardManager cmb = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							cmb.setText(content);
						} else {
							android.text.ClipboardManager cmb = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							cmb.setText(content);
						}
						toast("复制成功");
					}
				}
			});
			tvDel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick()) {
						dismiss();
						if (id != 0) {
							DelComment(id, position);
						} else {
							numChanged = true;
							TrendsComment.this.model.setComment_num(TrendsComment.this.model.getComment_num() - 1);
							commentAdapter.getList().remove(position);
							commentAdapter.notifyDataSetChanged();
						}
					}
				}
			});
		}
	}
	
	/**
	 * 删除自己的单条评论
	 */
	private void DelComment(int commentId, final int position) {
		DelCommentParam param = new DelCommentParam(commentId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                numChanged = true;
                TrendsComment.this.model.setComment_num(TrendsComment.this.model.getComment_num() - 1);
                commentAdapter.getList().remove(position);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                toast("删除失败，请重试");
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
