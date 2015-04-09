package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.TouchGalleyAdapter;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.UserPhotoList;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.params.GetUserTrendsPicParam;
import com.gather.android.widget.touchgallery.GalleryViewPager;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserCenterGallery extends BaseActivity {
	
	private TextView tvNum;
	private GalleryViewPager gallery;
	private TouchGalleyAdapter adapter;
	private ArrayList<UserPhotoModel> picList;
	private int position, page, maxPage, userId, size, isOver, lastItem, totalNum;
	private boolean loadmore, hasChanged = false;
	ArrayList<String> resources;

	@Override
	protected int layoutResId() {
		return R.layout.touch_gallery;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("LIST") && intent.hasExtra("POSITION")) {
			this.picList = (ArrayList<UserPhotoModel>) intent.getSerializableExtra("LIST");
			this.position = intent.getExtras().getInt("POSITION");
			this.page = intent.getExtras().getInt("PAGE");
			this.maxPage = intent.getExtras().getInt("MAX_PAGE");
			this.userId = intent.getExtras().getInt("UID");
			this.size = intent.getExtras().getInt("SIZE");
			this.isOver = intent.getExtras().getInt("ISOVER");
			this.tvNum = (TextView) findViewById(R.id.tvNum);
			this.gallery = (GalleryViewPager) findViewById(R.id.viewpager);
			resources = new ArrayList<String>();
			for (int i = 0; i < picList.size(); i++) {
				resources.add(picList.get(i).getImg_url());
			}
			this.gallery.setOnPageChangeListener(new myPageChangeListener());
			this.gallery.setOffscreenPageLimit(3);
			this.adapter = new TouchGalleyAdapter(UserCenterGallery.this, resources);
			this.gallery.setAdapter(adapter);
			
			this.initView();
		} else {
			finish();
			toast("图片参数错误~~");
		}
	}
	
	private void initView() {
		if (picList.size() == 1) {
			tvNum.setVisibility(View.GONE);
		} else {
			tvNum.setVisibility(View.VISIBLE);
			tvNum.setText((position+1)+"/"+picList.size());
		}
		gallery.setCurrentItem(position);
	}
	
	private class myPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int position) {
			tvNum.setText((position+1)+"/"+picList.size());
			if (lastItem < position && (position == picList.size() - 5 || position == picList.size() - 1) && isOver != 1 && !loadmore) {
				page++;
				getUserTrendsPic();
			}
			lastItem = position;
		}
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
                    for (int i = 0; i < list.getPhotos().size(); i++) {
                        resources.add(list.getPhotos().get(i).getImg_url());
                    }
                    adapter.setNotifyChanged(resources);
                    hasChanged = true;
                    if (picList.size() == 1) {
                        tvNum.setVisibility(View.GONE);
                    } else {
                        tvNum.setVisibility(View.VISIBLE);
                        tvNum.setText((lastItem+1)+"/"+picList.size());
                    }
                }
                if (page == maxPage) {
                    isOver = 1;
                }
                loadmore = false;
            }

            @Override
            public void onNeedLogin(String msg) {
                page--;
                loadmore = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                page--;
                loadmore = false;
                toast("获取用户图片出错，请重试");
            }
        });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent();
			intent.putExtra("POSITION", lastItem);
			if (hasChanged) {
				intent.putExtra("LIST", picList);
			}
			setResult(RESULT_OK, intent);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		}
		return true;
	}
	
}
