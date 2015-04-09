package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 删除动态（自己发布的）
 */
public class DelTrendsParam extends BaseParams {

	public DelTrendsParam(int dynamicId) {
		super("act/dynamic/delete");
        put("dynamicId", dynamicId);
	}

}
