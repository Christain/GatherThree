package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 取消收藏（攻略，记忆，订购）
 */
public class CancelCollectNewsParam extends StringParams {

	public CancelCollectNewsParam(Context context, int newsId) {
		super(context, "act/news/delLov");
		setParameter("newsId", newsId);
	}

}
