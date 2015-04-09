package com.gather.android.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.gather.android.model.UserInfoModel;

public class AppPreference {

	private final static String NAME = "APP_PREFERENCE";
	private static SharedPreferences preferences;

	/******************* 用户信息 **************************/
	public final static String USER_ID = "USER_ID"; // 用户ID
	public static final String USER_PHOTO = "USER_PHOTO"; // 头像
	public static final String USER_SEX = "USER_SEX"; // 性别
	public static final String NICK_NAME = "NICK_NAME"; // 昵称
	public static final String REAL_NAME = "REAL_NAME"; // 用户姓名
	public static final String USER_PWD = "USER_PWD"; // 密码
	public static final String TELPHONE = "TELPHONE"; // 电话
	public static final String USER_EMAIL = "USER_EMAIL"; // 邮箱
	public static final String USER_BIRTHDAY = "USER_BIRTHDAY"; // 生日
	public static final String INTRO = "INTRO"; // 个性签名
	public static final String HOBBY = "HOBBY"; // 爱好
	public static final String IS_VIP = "IS_VIP"; // 是否是达人
	public static final String FOCUS_NUM = "FOCUS_NUM"; // 关注数
	public static final String FANS_NUM = "FANS_NUM"; // 粉丝数
	public static final String IS_FOCUS = "IS_FOCUS"; // 是否关注
	public static final String ADDRESS = "ADDRESS"; // 地址
	public static final String LOCATION_CITY = "LOCATION_CITY"; // 定位城市
	public static final String LOCATION_CITY_CODE = "LOCATION_CITY_CODE"; //定位城市CODE
	public static final String IS_REGISTER = "IS_REGISTER"; // 是否完善资料
	public static final String CONTACT_PHONE = "CONTACT_PHONE"; // 联系电话
	public static final String CONTACT_QQ = "CONTACT_QQ"; // 联系QQ
	public static final String LOGIN_TYPE = "LOGIN_TYPE"; // 登录方式
	public static final String SINA_ID = "SINA_ID"; // 新浪openid
	public static final String SINA_TOKEN = "SINA_TOKEN"; // 新浪access_token
	public static final String SINA_EXPIRES = "SINA_EXPIRES"; // 新浪expires_in过期时间
	public static final String QQ_ID = "QQ_ID"; // 腾讯openid
	public static final String QQ_TOKEN = "QQ_TOKEN"; // 腾讯access_token
	public static final String QQ_EXPIRES = "QQ_EXPIRES"; // 腾讯expires_in过期时间
	public static final String WECHAT_ID = "WECHAT_ID"; // 微信openid
	public static final String WECHAT_TOKEN = "WECHAT_TOKEN"; // 微信access_token
	public static final String WECHAT_EXPIRES = "WECHAT_EXPIRES"; // 微信expires_in过期时间
	public static final String LAST_LOGIN_TIME = "LAST_LOGIN_TIME"; // 最后登录时间

	/******************* 登录平台TYPE **************************/
	public static final int TYPE_SELF = 100; // 自己平台登录
	public static final int TYPE_SINA = 101; // 新浪登录
	public static final int TYPE_QQ = 102; // QQ登录
	public static final int TYPE_WECHAT = 103; // 微信登录

	private static void getInstance(Context context) {
		if (preferences == null) {
			preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		}
	}

	public static String getUserPersistent(Context context, String key) {
		getInstance(context);
		return preferences.getString(key, "");
	}

	public static int getUserPersistentInt(Context context, String key) {
		getInstance(context);
		return preferences.getInt(key, 0);
	}

	public static long getUserPersistentLong(Context context, String key) {
		getInstance(context);
		return preferences.getLong(key, 0);
	}

	public static void save(Context context, String key, String value) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	}

	public static void save(Context context, String key, int value) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	public static void save(Context context, String key, long value) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putLong(key, value);
		mEditor.commit();
	}

	public static void clearInfo(Context context) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.clear();
		mEditor.commit();
	}

	/**
	 * 更新第三方登录TYPE, openid, access_token
	 */

	public static void saveThirdLoginInfo(Context context, int login_type, String open_id, String access_token, long expires_in) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		if (login_type == TYPE_QQ) {
			mEditor.putString(QQ_ID, open_id);
			mEditor.putString(QQ_TOKEN, access_token);
			mEditor.putLong(QQ_EXPIRES, expires_in);
		} else if (login_type == TYPE_SINA) {
			mEditor.putString(SINA_ID, open_id);
			mEditor.putString(SINA_TOKEN, access_token);
			mEditor.putLong(SINA_EXPIRES, expires_in);
		} else if (login_type == TYPE_WECHAT) {
			mEditor.putString(WECHAT_ID, open_id);
			mEditor.putString(WECHAT_TOKEN, access_token);
			mEditor.putLong(WECHAT_EXPIRES, expires_in);
		}
		mEditor.commit();
	}

	/**
	 * 更新手机号登录信息
	 */
	public static void savePhoneLoginInfo(Context context, String phoneNum) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putInt(LOGIN_TYPE, TYPE_SELF);
		mEditor.putString(TELPHONE, phoneNum);
		mEditor.commit();
	}

	/**
	 * 保存个人信息
	 * 
	 * @param context
	 * @param model
	 */
	public static void saveUserInfo(Context context, UserInfoModel model) {
		getInstance(context);
		SharedPreferences.Editor mEditor = preferences.edit();
		mEditor.putInt(USER_ID, model.getUid());
		mEditor.putInt(USER_SEX, model.getSex());
		mEditor.putString(USER_BIRTHDAY, model.getBirth());
		mEditor.putString(NICK_NAME, model.getNick_name());
		mEditor.putString(REAL_NAME, model.getReal_name());
		mEditor.putString(USER_PHOTO, model.getHead_img_url());
		mEditor.putString(TELPHONE, model.getPho_num());
		mEditor.putString(USER_EMAIL, model.getEmail());
		mEditor.putString(ADDRESS, model.getAddress());
		mEditor.putString(INTRO, model.getIntro());
		mEditor.putString(HOBBY, model.getHobby());
		mEditor.putInt(IS_VIP, model.getIs_vip());
		mEditor.putInt(FOCUS_NUM, model.getFocus_num());
		mEditor.putInt(FANS_NUM, model.getFans_num());
		mEditor.putInt(IS_FOCUS, model.getIs_focus());
		mEditor.putInt(IS_REGISTER, model.getIs_regist());
		mEditor.putString(CONTACT_PHONE, model.getContact_phone());
		mEditor.putLong(SINA_EXPIRES, model.getSina_expires_in());
		mEditor.putString(SINA_TOKEN, model.getSina_token());
		mEditor.putString(SINA_ID, model.getSina_openid());
		mEditor.putLong(QQ_EXPIRES, model.getQq_expires_in());
		mEditor.putString(QQ_TOKEN, model.getQq_token());
		mEditor.putString(QQ_ID, model.getQq_openid());
		mEditor.putString(CONTACT_QQ, model.getContact_qq());
		mEditor.putString(LAST_LOGIN_TIME, model.getLast_login_time());
		mEditor.commit();
	}

	/**
	 * 获取个人信息
	 */
	public static UserInfoModel getUserInfo(Context context) {
		UserInfoModel model = new UserInfoModel();
		getInstance(context);
		model.setUid(preferences.getInt(USER_ID, 0));
		model.setSex(preferences.getInt(USER_SEX, 0));
		model.setBirth(preferences.getString(USER_BIRTHDAY, ""));
		model.setNick_name(preferences.getString(NICK_NAME, ""));
		model.setReal_name(preferences.getString(REAL_NAME, ""));
		model.setHead_img_url(preferences.getString(USER_PHOTO, ""));
		model.setPho_num(preferences.getString(TELPHONE, ""));
		model.setEmail(preferences.getString(USER_EMAIL, ""));
		model.setAddress(preferences.getString(ADDRESS, ""));
		model.setIntro(preferences.getString(INTRO, ""));
		model.setHobby(preferences.getString(HOBBY, ""));
		model.setIs_vip(preferences.getInt(IS_VIP, 0));
		model.setFocus_num(preferences.getInt(FOCUS_NUM, 0));
		model.setFans_num(preferences.getInt(FANS_NUM, 0));
		model.setIs_focus(preferences.getInt(IS_FOCUS, 0));
		model.setIs_regist(preferences.getInt(IS_REGISTER, 0));
		model.setContact_phone(preferences.getString(CONTACT_PHONE, ""));
		model.setSina_expires_in(preferences.getLong(SINA_EXPIRES, 0));
		model.setSina_token(preferences.getString(SINA_TOKEN, ""));
		model.setSina_openid(preferences.getString(SINA_ID, ""));
		model.setQq_expires_in(preferences.getLong(QQ_EXPIRES, 0));
		model.setQq_token(preferences.getString(QQ_TOKEN, ""));
		model.setQq_openid(preferences.getString(QQ_ID, ""));
		model.setContact_qq(preferences.getString(CONTACT_QQ, ""));
		model.setLast_login_time(preferences.getString(LAST_LOGIN_TIME, ""));
		return model;
	}

	/**
	 * 判断是否登录
	 */
	public static boolean hasLogin(Context context) {
		getInstance(context);
		if (preferences.getInt(LOGIN_TYPE, 0) == 0) {
			return false;
		} else {
			return true;
		}
	}

}
