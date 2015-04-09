package com.gather.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gather.android.R;
import com.gather.android.activity.ActRelationList;
import com.gather.android.activity.UserCenterGallery;
import com.gather.android.activity.UserTrends;
import com.gather.android.activity.WebStrategy;
import com.gather.android.application.GatherApplication;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.NewsModelList;
import com.gather.android.model.UserInfoModel;
import com.gather.android.model.UserPhotoList;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.params.GetUserPhotoParam;
import com.gather.android.params.GetUserTrendsPicParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.HorizontalListView;
import com.gather.android.widget.HorizontalListView.OnScrollingListener;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserCenterListViewAdapter extends BaseAdapter {
	
	private Activity mContext;
	private UserCenterPicAdapter adapter;
	private int userId, page = 1, size = 10, isOver, totalNum, maxPage, lastItem;
	private UserInfoModel userInfoModel;
	private boolean isRequest = false, isMe = false, loadmore = false;
	private GatherApplication application;
	private ArrayList<UserPhotoModel> picList = new ArrayList<UserPhotoModel>();
	private ViewHolder holder;
	
	public UserCenterListViewAdapter(Activity context,int userId, UserInfoModel userInfoModel, boolean isMe) {
		this.mContext = context;
		this.isMe = isMe;
		this.userId = userId;
		this.userInfoModel = userInfoModel;
		this.adapter = new UserCenterPicAdapter(mContext);
		this.getUserPhoto();
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int apositionrg0) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_user_center_listview, null);
			holder = new ViewHolder();
			holder.horizontalListView = (HorizontalListView) convertView.findViewById(R.id.horizontalListview);
			holder.horizontalListView.setVisibility(View.GONE);
			holder.tvUserHobby = (TextView) convertView.findViewById(R.id.tvUserHobby);
			holder.tvUserBrief = (TextView) convertView.findViewById(R.id.tvUserBrief);
			holder.llTrends = (LinearLayout) convertView.findViewById(R.id.llTrends);
			holder.llAct = (LinearLayout) convertView.findViewById(R.id.llAct);
			holder.llInterview = (LinearLayout) convertView.findViewById(R.id.llInterview);
			holder.llInterview.setVisibility(View.GONE);
			holder.view = (View) convertView.findViewById(R.id.view);
			if (isMe) {
				holder.view.setVisibility(View.GONE);
			} else {
				holder.view.setVisibility(View.VISIBLE);
			}
			
			holder.llTrends.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!ClickUtil.isFastClick() && userInfoModel != null) {
						Intent intent = new Intent(mContext, UserTrends.class);
						intent.putExtra("MODEL", userInfoModel);
						mContext.startActivity(intent);
					}
				}
			});
			holder.llAct.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick() && userInfoModel != null) {
						Intent intent = new Intent(mContext, ActRelationList.class);
						intent.putExtra("UID", userInfoModel.getUid());
						mContext.startActivity(intent);
					}
				}
			});
			
			DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) holder.horizontalListView.getLayoutParams();
			param.width = (int) metrics.widthPixels;
			param.height = (int) (param.width - 40) / 3 * 35 / 23;
			holder.horizontalListView.setLayoutParams(param);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.horizontalListView.setAdapter(adapter);
		
		holder.horizontalListView.setOnScrollingListener(new OnScrollingListener() {
			@Override
			public void onScroll(int firstItem, int lastItem) {
				if (UserCenterListViewAdapter.this.lastItem < lastItem && (lastItem == picList.size() - 5 || lastItem == picList.size() - 1) && isOver != 1 && !loadmore) {
					page++;
					getUserTrendsPic();
				}
				UserCenterListViewAdapter.this.lastItem = lastItem;
			}
		});
		holder.horizontalListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				UserPhotoModel model = adapter.getItem(position);
				if (null != model) {
					Intent intent = new Intent(mContext, UserCenterGallery.class);
					intent.putExtra("LIST", picList);
					intent.putExtra("POSITION", position);
					intent.putExtra("PAGE", page);
					intent.putExtra("UID", userId);
					intent.putExtra("MAX_PAGE", maxPage);
					intent.putExtra("SIZE", size);
					intent.putExtra("ISOVER", isOver);
					mContext.startActivityForResult(intent, 100);
					mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}
		});
		
		if (userInfoModel != null) {
			if (!userInfoModel.getHobby().equals("")) {
				holder.tvUserHobby.setText(userInfoModel.getHobby());
			} else {
				holder.tvUserHobby.setText("还没有填写爱好");
			}
			if (userInfoModel.getIntro().equals("")) {
				holder.tvUserBrief.setText("还没有填写个性签名");
			} else {
				holder.tvUserBrief.setText(userInfoModel.getIntro());
			}
		}
		return convertView;
	}
	
	private static class ViewHolder {
		public HorizontalListView horizontalListView;
		public TextView tvUserHobby, tvUserBrief;
		public LinearLayout llTrends, llAct, llInterview;
		public View view;
	}
	
	public void setUserInfoModel(UserInfoModel userInfoModel) {
		this.userInfoModel = userInfoModel;
		if (userInfoModel != null) {
			if (!userInfoModel.getHobby().equals("")) {
				holder.tvUserHobby.setText(userInfoModel.getHobby());
			} else {
				holder.tvUserHobby.setText("还没有填写爱好");
			}
			if (userInfoModel.getIntro().equals("")) {
				holder.tvUserBrief.setText("还没有填写个性签名");
			} else {
				holder.tvUserBrief.setText(userInfoModel.getIntro());
			}
		}
	}
	
	public void setInterview(final NewsModelList interviewList) {
		if (interviewList != null && interviewList.getNews() != null && interviewList.getNews().size() > 0) {
			holder.llInterview.setVisibility(View.VISIBLE);
			holder.llInterview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick()) {
						Intent intent = new Intent(mContext, WebStrategy.class);
						intent.putExtra("MODEL", interviewList.getNews().get(0));
						mContext.startActivity(intent);
					}
				}
			});
		} else {
			holder.llInterview.setVisibility(View.GONE);
		}
	}
	
	public UserCenterPicAdapter getPicAdapter() {
		return adapter;
	}
	
	public HorizontalListView getPicListView() {
		return holder.horizontalListView;
	}
	
	/**
	 * 获取用户照片墙图片
	 */
	private void getUserPhoto() {
		page = 1;
		isOver = 0;
		GetUserPhotoParam param = new GetUserPhotoParam(userId, 1, 10);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                UserPhotoList list = gson.fromJson(result, UserPhotoList.class);
                if (list != null && list.getPhotos() != null) {
                    picList = list.getPhotos();
                    getUserTrendsPic();
                }
            }

            @Override
            public void onNeedLogin(String msg) {

            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                Toast.makeText(mContext, "获取用户图片出错，请重试", Toast.LENGTH_SHORT).show();
            }
        });
	}

	/**
	 * 获取用户动态图片
	 */
	private void getUserTrendsPic() {
		loadmore = true;
		GetUserTrendsPicParam param = new GetUserTrendsPicParam(userId, page, size);
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
                UserPhotoList list = gson.fromJson(result, UserPhotoList.class);
                if (list != null && list.getPhotos() != null) {
                    picList.addAll(list.getPhotos());
                    if (list.getPhotos().size() == 0) {
                        isOver = 1;
                    }
                }
                if (picList.size() == 0) {
                    if (holder.horizontalListView.isShown()) {
                        holder.horizontalListView.setVisibility(View.GONE);
                    }
                } else {
                    if (!holder.horizontalListView.isShown()) {
                        holder.horizontalListView.setVisibility(View.VISIBLE);
                    }
                    if (page == maxPage) {
                        isOver = 1;
                    }
                    adapter.setNotifyChanged(picList);
                }
                loadmore = false;
            }

            @Override
            public void onNeedLogin(String msg) {
                page--;
                loadmore = false;
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (picList.size() == 0) {
                    if (holder.horizontalListView.isShown()) {
                        holder.horizontalListView.setVisibility(View.GONE);
                    }
                } else {
                    if (!holder.horizontalListView.isShown()) {
                        holder.horizontalListView.setVisibility(View.VISIBLE);
                    }
                    adapter.setNotifyChanged(picList);
                }
                page--;
                loadmore = false;
                Toast.makeText(mContext, "获取用户图片出错，请重试", Toast.LENGTH_SHORT).show();
            }
        });
	}

}
