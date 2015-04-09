package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 取消收藏（攻略，记忆，订购）
 */
public class CancelCollectNewsParam extends BaseParams {

	public CancelCollectNewsParam(int newsId) {
		super("act/news/delLov");
        put("newsId", newsId);
	}

}
