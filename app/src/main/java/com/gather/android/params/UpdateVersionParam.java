package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 检测版本更新
 */
public class UpdateVersionParam extends StringParams {

	public UpdateVersionParam(Context context) {
		super(context, "act/appInfo/lastVersion");
		setParameter("type", 1);
	}

}
