package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 资讯详情
 */
public class NewsDetailParam extends BaseParams {

	public NewsDetailParam(int newsId) {
		super("act/news/newsInfo");
		put("newsId", newsId);
	}

}
