package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 收藏（攻略，记忆，订购）
 */
public class CollectNewsParam extends StringParams {

	public CollectNewsParam(Context context, int newsId) {
		super(context, "act/news/lov");
		setParameter("newsId", newsId);
	}

}
