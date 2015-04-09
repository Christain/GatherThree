package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 攻略，回忆，票务，专访列表
 */
public class NewsListParam extends BaseParams {

	/**
	 * @param cityId
	 * @param tagId 标签Id
	 * @param typeId 类型id：1攻略，2回忆，3票务，4专访
	 * @param page
	 * @param size
	 */
	public NewsListParam(int cityId, int tagId, int typeId, int page, int size) {
		super("act/news/news");
        put("cityId", cityId);
		if (tagId != 0) {
            put("tagId", tagId);
		}
        put("typeId", typeId);
        put("page", page);
        put("size", size);
	}

	/**
	 * 搜索关键字
	 * @param keyWords
	 */	
	public NewsListParam(int cityId, int typeId, String keyWords, int page, int size) {
		super("act/news/news");
        put("cityId", cityId);
        put("typeId", typeId);
        put("keyWords", keyWords);
        put("page", page);
        put("size", size);
	}
}
