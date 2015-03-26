package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 攻略，回忆，票务，专访列表
 */
public class NewsListParam extends StringParams {

	/**
	 * @param context
	 * @param cityId
	 * @param tagId 标签Id
	 * @param typeId 类型id：1攻略，2回忆，3票务，4专访
	 * @param page
	 * @param size
	 */
	public NewsListParam(Context context, int cityId, int tagId, int typeId, int page, int size) {
		super(context, "act/news/news");
		setParameter("cityId", cityId);
		if (tagId != 0) {
			setParameter("tagId", tagId);
		}
		setParameter("typeId", typeId);
		setParameter("page", page);
		setParameter("size", size);
	}

	/**
	 * 搜索关键字
	 * @param keyWords
	 */	
	public NewsListParam(Context context, int cityId, int typeId, String keyWords, int page, int size) {
		super(context, "act/news/news");
		setParameter("cityId", cityId);
		setParameter("typeId", typeId);
		setParameter("keyWords", keyWords);
		setParameter("page", page);
		setParameter("size", size);
	}
}
