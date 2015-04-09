package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 检测版本更新
 */
public class UpdateVersionParam extends BaseParams {

	public UpdateVersionParam() {
		super("act/appInfo/lastVersion");
        put("type", 1);
	}

}
