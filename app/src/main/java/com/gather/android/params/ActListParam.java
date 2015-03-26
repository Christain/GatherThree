package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动列表（根据条件获取）
 */
public class ActListParam extends StringParams {

	public ActListParam(Context context, int cityId, int page, int size) {
		super(context, "act/activity/acts");
		setParameter("cityId", cityId);
		setParameter("page", page);
		setParameter("size", size);
	}

	/**
	 * 标签id
	 * 
	 * @param tagId
	 */
	public void setTagId(int tagId) {
		setParameter("tagId", tagId);
	}

	/**
	 * 关键字
	 * 
	 * @param keyWords
	 */
	public void setKeyWords(String keyWords) {
		setParameter("keyWords", keyWords);
	}

	/**
	 * 开始时间（例：1997-07-01 09:00:00） 结束时间（例：1997-07-01 09:00:00）
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public void setTime(String startTime, String endTime) {
		setParameter("startTime", startTime);
		setParameter("endTime", endTime);
	}

}
