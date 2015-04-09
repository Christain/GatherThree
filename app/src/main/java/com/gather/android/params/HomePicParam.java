package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 首页轮播图
 */
public class HomePicParam extends BaseParams {

	public HomePicParam(int cityId, int page, int size) {
		super("act/news/homeAdverts");
        put("cityId", cityId);
        put("page", page);
        put("size", size);
	}

}
