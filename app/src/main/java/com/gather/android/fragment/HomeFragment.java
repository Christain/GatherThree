package com.gather.android.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.activity.About;
import com.gather.android.activity.ActHot;
import com.gather.android.activity.ActStrategyAndMemoryAndTicket;
import com.gather.android.activity.ActWeek;
import com.gather.android.activity.LoginIndex;
import com.gather.android.activity.QRCodeScan;
import com.gather.android.activity.SelectCity;
import com.gather.android.activity.TipOff;
import com.gather.android.activity.VipList;
import com.gather.android.activity.WebStrategy;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.CityList;
import com.gather.android.model.NewsModel;
import com.gather.android.model.NewsModelList;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.BindPushParam;
import com.gather.android.params.GetUserInfoParam;
import com.gather.android.params.HomePicParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.service.PushMessageReceiver;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.InfiniteLoopViewPager;
import com.gather.android.widget.InfiniteLoopViewPagerAdapter;
import com.gather.android.widget.MyViewPager.OnPageChangeListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 切换卡首页
 */
@SuppressLint("HandlerLeak")
public class HomeFragment extends BaseFragment implements OnClickListener {

    private View convertView;
    private GatherApplication mApplication;
    private DisplayImageOptions options;

    /**
     * 标题栏
     */
    private TextView tvAbout, tvTitle, tvLocation;

    /**
     * 轮播图
     */
    private InfiniteLoopViewPager mViewPager;
    private InfiniteLoopViewPagerAdapter pagerAdapter;
    private LinearLayout llGroup;
    private ArrayList<ImageView> mImageList;
    private Handler mHandler;
    private int sleepTime = 4000; // 4秒轮播一次
    private Drawable mIndicatorUnfocused;
    private Drawable mIndicatorFocused;
    private ArrayList<NewsModel> imgList;// 轮播图

    /**
     * 色块
     */
    private LinearLayout llPartOne, llPartTwo, llPartThree, llPartFour, llPartFive, llPartSix, llPartSeven;

    private DialogTipsBuilder dialog;
    private DialogChoiceBuilder choiceBuilder;
    private boolean openCity = false;// 判断城市是否可用
    private boolean hasLogin = false;// 判断是否登录
    private UserInfoModel userInfoModel;

    @Override
    protected void OnCreate(Bundle savedInstanceState) {
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        this.convertView = inflater.inflate(R.layout.fragment_home, (ViewGroup) getActivity().findViewById(R.id.tabhost), false);

        this.hasLogin = AppPreference.hasLogin(getActivity());
        this.dialog = DialogTipsBuilder.getInstance(getActivity());
        this.choiceBuilder = DialogChoiceBuilder.getInstance(getActivity());
        this.mApplication = (GatherApplication) getActivity().getApplication();
        this.tvAbout = (TextView) convertView.findViewById(R.id.tvAbout);
        this.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        this.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
        this.mViewPager = (InfiniteLoopViewPager) convertView.findViewById(R.id.viewPager);
        this.mViewPager.setVisibility(View.INVISIBLE);
        this.llGroup = (LinearLayout) convertView.findViewById(R.id.slide_indicator);
        this.llPartOne = (LinearLayout) convertView.findViewById(R.id.llPartOne);
        this.llPartTwo = (LinearLayout) convertView.findViewById(R.id.llPartTwo);
        this.llPartThree = (LinearLayout) convertView.findViewById(R.id.llPartThree);
        this.llPartFour = (LinearLayout) convertView.findViewById(R.id.llPartFour);
        this.llPartFive = (LinearLayout) convertView.findViewById(R.id.llPartFive);
        this.llPartSix = (LinearLayout) convertView.findViewById(R.id.llPartSix);
        this.llPartSeven = (LinearLayout) convertView.findViewById(R.id.llPartSeven);

        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) this.mViewPager.getLayoutParams();
        param.width = widthPixels - getResources().getDimensionPixelOffset(R.dimen.home_padding_left_right) * 2;
        param.height = param.width * 1 / 2;
        this.mViewPager.setLayoutParams(param);

        LinearLayout.LayoutParams partTwo = (LinearLayout.LayoutParams) this.llPartTwo.getLayoutParams();
        partTwo.width = (widthPixels - getResources().getDimensionPixelOffset(R.dimen.home_padding_left_right) * 2 - getResources().getDimensionPixelOffset(R.dimen.home_part_padding) * 2) / 3;
        this.llPartTwo.setLayoutParams(partTwo);

        LinearLayout.LayoutParams partOne = (LinearLayout.LayoutParams) this.llPartOne.getLayoutParams();
        partOne.width = (widthPixels - getResources().getDimensionPixelOffset(R.dimen.home_padding_left_right) * 2 - getResources().getDimensionPixelOffset(R.dimen.home_part_padding) - partTwo.width);
        this.llPartOne.setLayoutParams(partOne);

        LinearLayout.LayoutParams partThree = (LinearLayout.LayoutParams) this.llPartThree.getLayoutParams();
        partThree.width = partTwo.width;
        this.llPartThree.setLayoutParams(partThree);

        LinearLayout.LayoutParams partFour = (LinearLayout.LayoutParams) this.llPartFour.getLayoutParams();
        partFour.width = partTwo.width;
        this.llPartFour.setLayoutParams(partFour);

        LinearLayout.LayoutParams partFive = (LinearLayout.LayoutParams) this.llPartFive.getLayoutParams();
        partFive.width = partTwo.width;
        this.llPartFive.setLayoutParams(partFive);

        LinearLayout.LayoutParams partSix = (LinearLayout.LayoutParams) this.llPartSix.getLayoutParams();
        partSix.width = partTwo.width;
        this.llPartSix.setLayoutParams(partSix);

        LinearLayout.LayoutParams partSeven = (LinearLayout.LayoutParams) this.llPartSeven.getLayoutParams();
        partSeven.width = partOne.width;
        this.llPartSeven.setLayoutParams(partSeven);

        this.tvAbout.setOnClickListener(this);
        this.tvLocation.setOnClickListener(this);
        this.llPartOne.setOnClickListener(this);
        this.llPartTwo.setOnClickListener(this);
        this.llPartThree.setOnClickListener(this);
        this.llPartFour.setOnClickListener(this);
        this.llPartFive.setOnClickListener(this);
        this.llPartSix.setOnClickListener(this);
        this.llPartSeven.setOnClickListener(this);
        this.llPartOne.setOnClickListener(this);

        this.initHome();
        this.initPageInfo();
    }

    @Override
    protected View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) convertView.getParent();
        if (p != null) {
            p.removeAllViews();
        }
        return convertView;
    }

    @Override
    protected void OnSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void OnActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHandler != null) {
            if (!mApplication.isRun) {
                mApplication.isRun = true;
                mHandler.sendEmptyMessageDelayed(0, sleepTime);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHandler != null) {
            mApplication.isRun = false;
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 初始化轮播图Handler
     */
    private void initViewPager() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (imgList != null && imgList.size() > 1) {
                    switch (msg.what) {
                        case 0:
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                            if (mApplication.isRun && !mApplication.isDown) {
                                this.sendEmptyMessageDelayed(0, sleepTime);
                            }
                            break;
                        case 1:
                            if (mApplication.isRun && !mApplication.isDown) {
                                this.sendEmptyMessageDelayed(0, sleepTime);
                            }
                            break;
                    }
                }
            }
        };
    }

    private void initHome() {
        this.tvTitle.setText("集合啦");
        if (mApplication.mLocation == null || mApplication.mLocation.getCity() == null) {
            if (AppPreference.getUserPersistent(getActivity(), "LOCATION_CITY").length() > 1) {
                this.tvLocation.setText(AppPreference.getUserPersistent(getActivity(), "LOCATION_CITY"));
                mApplication.setCityId(AppPreference.getUserPersistentInt(getActivity(), AppPreference.LOCATION_CITY_CODE));
            } else {
                this.tvLocation.setText("定位中..");
            }
        } else {
            String city = mApplication.mLocation.getCity();
            if (city.contains("市")) {
                city = city.substring(0, city.length() - 1);
            }
            this.tvLocation.setText(city);
            city = null;
        }
    }

    /**
     * 判断城市是否可用
     */
    private void initPageInfo() {
        SharedPreferences cityPreferences = getActivity().getSharedPreferences("CITY_LIST", Context.MODE_PRIVATE);
        String city = cityPreferences.getString("CITY", "");
        if (city.contains(tvLocation.getText().toString())) {
            Gson gson = new Gson();
            CityList list = gson.fromJson(city, CityList.class);
            for (int i = 0; i < list.getCities().size(); i++) {
                if (list.getCities().get(i).getName().contains(tvLocation.getText().toString())) {
                    mApplication.setCityId(list.getCities().get(i).getId());
                    AppPreference.save(getActivity(), AppPreference.LOCATION_CITY, list.getCities().get(i).getName());
                    AppPreference.save(getActivity(), AppPreference.LOCATION_CITY_CODE, list.getCities().get(i).getId());
                    if (hasLogin) {
                        getUserInfo(list.getCities().get(i).getId());
                    }
                    getHomePic();
                    if (hasLogin && PushMessageReceiver.baiduUserId.length() > 1 && PushMessageReceiver.baiduChannelId.length() > 1) {
                        BindService(getActivity(), PushMessageReceiver.baiduUserId, PushMessageReceiver.baiduChannelId);
                    }
                }
            }
            openCity = true;
        } else {
            openCity = false;
            ChoiceCity();
        }
    }

    /**
     * 获取个人信息
     */
    private void getUserInfo(int cityId) {
        GetUserInfoParam param = new GetUserInfoParam(cityId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    Gson gson = new Gson();
                    userInfoModel = gson.fromJson(object.getString("user"), UserInfoModel.class);
                    if (userInfoModel != null) {
                        if (mApplication != null) {
                            mApplication.setUserInfoModel(userInfoModel);
                        }
                        AppPreference.saveUserInfo(getActivity(), userInfoModel);
                    } else {
                        Toast.makeText(getActivity(), "获取个人信息失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "个人信息解析失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                }
            }
        });
    }

    /**
     * 获取首页轮播图片
     */
    private void getHomePic() {
        HomePicParam param = new HomePicParam(mApplication.getCityId(), 1, 10);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                NewsModelList list = gson.fromJson(result, NewsModelList.class);
                if (list != null && list.getNews() != null) {
                    imgList = list.getNews();
                    setViewPager();
                } else {
                    imgList = new ArrayList<NewsModel>();
                }
            }

            @Override
            public void onNeedLogin(String msg) {

            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (Constant.SHOW_LOG) {
                    toast("获取轮播图 失败");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tvAbout:
                if (!ClickUtil.isFastClick()) { // 关于
                    intent = new Intent(getActivity(), About.class);
                    startActivity(intent);
                }
                break;
            case R.id.tvLocation: // 城市
                if (!ClickUtil.isFastClick()) {
                    intent = new Intent(getActivity(), SelectCity.class);
                    startActivityForResult(intent, 100);
                }
                break;
            case R.id.llPartOne: // 活动
                if (!ClickUtil.isFastClick() && openCity) {
                    intent = new Intent(getActivity(), ActWeek.class);
                    startActivity(intent);
                }
                break;
            case R.id.llPartTwo: // 爆料
                if (!ClickUtil.isFastClick() && openCity) {
                    intent = new Intent(getActivity(), TipOff.class);
                    startActivityForResult(intent, 10);
                }
                break;
            case R.id.llPartThree: // 热门
                if (!ClickUtil.isFastClick() && openCity) {
                    intent = new Intent(getActivity(), ActHot.class);
                    startActivity(intent);
                }
                break;
            case R.id.llPartFour: // 记忆
                if (!ClickUtil.isFastClick() && openCity) {
                    intent = new Intent(getActivity(), ActStrategyAndMemoryAndTicket.class);
                    intent.putExtra("TYPE", ActStrategyAndMemoryAndTicket.MEMORY);
                    startActivity(intent);
                }
                break;
            case R.id.llPartFive: // 签到
                if (!ClickUtil.isFastClick() && openCity) {
                    if (AppPreference.hasLogin(getActivity())) {
                        intent = new Intent(getActivity(), QRCodeScan.class);
                        startActivity(intent);
                    } else {
                        DialogLogin();
                    }
                }
                break;
            case R.id.llPartSix: // 订购
                if (!ClickUtil.isFastClick() && openCity) {
                    intent = new Intent(getActivity(), ActStrategyAndMemoryAndTicket.class);
                    intent.putExtra("TYPE", ActStrategyAndMemoryAndTicket.TICKET);
                    startActivity(intent);
                }
                break;
            case R.id.llPartSeven: // 人物
                if (!ClickUtil.isFastClick() && openCity) {
                    intent = new Intent(getActivity(), VipList.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 100:
                    if (data != null) {
                        openCity = true;
                        String city = data.getStringExtra("CITY");
                        if (city.contains("市")) {
                            city = city.substring(0, city.length() - 1);
                        }
                        tvLocation.setText(city);
                        city = null;
                        if (data.hasExtra("PIC_LIST")) {
                            imgList = null;
                            llGroup.removeAllViews();
                            imgList = (ArrayList<NewsModel>) data.getSerializableExtra("PIC_LIST");
                            setViewPager();
                        }
                        toast("切换城市成功");
                    }
                    break;
                case 10:// 爆料成功
                    final DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(getActivity());
                    dialog.setTips("感谢提交");
                    dialog.setMessage("谢谢您提交的线索\n如果您提供的线索真实有效的话\n有机会获得我们的感谢奖品哦！").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            dialog.dismiss();
                        }
                    }).show();
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void setViewPager() {
        if (imgList != null && imgList.size() == 0) {
            mViewPager.setVisibility(View.INVISIBLE);
        } else {
            mViewPager.setVisibility(View.VISIBLE);
            initViewPager();
            if (imgList.size() > 1) {
                if (!mApplication.isRun) {
                    mApplication.isRun = true;
                    mHandler.sendEmptyMessageDelayed(0, sleepTime);
                }
                mIndicatorUnfocused = getResources().getDrawable(R.drawable.slide_indicator_unfocused);
                mIndicatorFocused = getResources().getDrawable(R.drawable.slide_indicator_focused);
                mImageList = new ArrayList<ImageView>();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                for (int i = 0; i < imgList.size(); i++) {
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (metrics.density * 7 + 0.5f), (int) (metrics.density * 7 + 0.5f)));
                    imageView.setBackgroundDrawable(mIndicatorUnfocused);
                    mImageList.add(imageView);
                    llGroup.addView(imageView);
                    if (i != imgList.size() - 1) {
                        View view = new View(getActivity());
                        view.setLayoutParams(new ViewGroup.LayoutParams((int) (metrics.density * 5 + 0.5f), 1));
                        llGroup.addView(view);
                    }
                }
                mImageList.get(0).setBackgroundDrawable(mIndicatorFocused);
            }

            pagerAdapter = new InfiniteLoopViewPagerAdapter(new PicAdapter());
            mViewPager.setInfinateAdapter(mHandler, pagerAdapter);
            mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        }

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
            if (mImageList != null && mIndicatorFocused != null && mIndicatorUnfocused != null) {
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
    private class PicAdapter extends com.gather.android.widget.MyPagerAdapter {
        @Override
        public int getCount() {
            return imgList.size();
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
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (!ClickUtil.isFastClick()) {
                        Intent intent = new Intent(getActivity(), WebStrategy.class);
                        intent.putExtra("MODEL", imgList.get(position));
                        startActivity(intent);
                    }
                }
            });
            imageLoader.displayImage(imgList.get(position).getH_img_url(), imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }
            });
            container.addView(imageView);
            return imageView;
        }
    }

    /**
     * 绑定推送到服务端
     *
     * @param context
     * @param baiduUserId
     * @param baiduChannelId
     */
    private void BindService(final Context context, String baiduUserId, String baiduChannelId) {
        BindPushParam param = new BindPushParam(GatherApplication.cityId, 3, baiduUserId, baiduChannelId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (Constant.SHOW_LOG) {
                    Toast.makeText(context, "绑定服务成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNeedLogin(String msg) {

            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (Constant.SHOW_LOG) {
                    Toast.makeText(context, "绑定服务失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 选择城市
     */
    protected void ChoiceCity() {
        final DialogTipsBuilder dialog = DialogTipsBuilder.getInstance(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.withDuration(300).withEffect(Effectstype.Fall).setMessage("你当前位置不在我们所开放的城市范围内，请点击右上角选择一个城市").isCancelableOnTouchOutside(false).setOnClick(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), SelectCity.class);
                startActivityForResult(intent, 100);
                dialog.dismiss();
            }
        }).setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        dialog.show();
    }

    /**
     * 需要去登录
     */
    private void DialogLogin() {
        if (choiceBuilder != null && !choiceBuilder.isShowing()) {
            choiceBuilder.setMessage("想看更多内容，现在就去登录吧？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (PushManager.isPushEnabled(getActivity().getApplicationContext())) {
                        PushManager.stopWork(getActivity().getApplicationContext());
                    }
                    mApplication.setUserInfoModel(null);
                    AppPreference.clearInfo(getActivity());
                    Intent intent = new Intent(getActivity(), LoginIndex.class);
                    startActivity(intent);
                    choiceBuilder.dismiss();
                }
            }).show();
        }
    }

}
