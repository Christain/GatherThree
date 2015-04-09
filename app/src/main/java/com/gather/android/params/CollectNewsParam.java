package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 收藏（攻略，记忆，订购）
 */
public class CollectNewsParam extends BaseParams {

	public CollectNewsParam(int newsId) {
		super("act/news/lov");
        put("newsId", newsId);
	}

}
