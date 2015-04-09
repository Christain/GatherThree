package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动列表（根据条件获取）
 */
public class ActListParam extends BaseParams {

	public ActListParam(int cityId, int page, int size) {
		super("act/activity/acts");
        put("cityId", cityId);
        put("page", page);
        put("size", size);
	}

	/**
	 * 标签id
	 * 
	 * @param tagId
	 */
	public void setTagId(int tagId) {
        put("tagId", tagId);
	}

	/**
	 * 关键字
	 * 
	 * @param keyWords
	 */
	public void setKeyWords(String keyWords) {
        put("keyWords", keyWords);
	}

	/**
	 * 开始时间（例：1997-07-01 09:00:00） 结束时间（例：1997-07-01 09:00:00）
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public void setTime(String startTime, String endTime) {
        put("startTime", startTime);
        put("endTime", endTime);
	}

}
