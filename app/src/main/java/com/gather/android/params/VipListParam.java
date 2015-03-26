package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取达人列表
 */
public class VipListParam extends StringParams {

	/**
	 * 分类获取达人列表
	 * @param context
	 * @param cityId
	 * @param tagId
	 * @param sex
	 * @param userTagId
	 * @param page
	 * @param size
	 */
	public VipListParam(Context context, int cityId, int tagId, int sex, int userTagId, int page, int size) {
		super(context, "act/vip/users");
		setParameter("cityId", cityId);
		if (tagId != 0) {
			setParameter("tagId", tagId);
		}
		if (sex != 0) {
			setParameter("sex", sex);
		}
		if (userTagId != 0) {
			setParameter("userTagId", userTagId);
		}
		setParameter("page", page);
		setParameter("size", size);
	}

	/**
	 * 搜索达人（根据关键字）
	 * @param context
	 * @param cityId
	 * @param keyWords
	 */
	public VipListParam(Context context, int cityId, String keyWords, int page, int size) {
		super(context, "act/vip/users");
		setParameter("cityId", cityId);

		if (keyWords != null && !keyWords.equals("")) {
			setParameter("keyWords", keyWords);
		}
		setParameter("page", page);
		setParameter("size", size);
	}

}
