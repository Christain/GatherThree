package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 资讯详情
 */
public class NewsDetailParam extends StringParams {

	public NewsDetailParam(Context context, int newsId) {
		super(context, "act/news/newsInfo");
		setParameter("newsId", newsId);
	}

}
