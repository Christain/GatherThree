package com.gather.android.params;

import java.io.File;

import android.content.Context;

import com.gather.android.baseclass.MultipartParams;

public class ShareToSinaParam extends MultipartParams {

	public ShareToSinaParam(Context context, String access_token, String status, File pic) {
		super(context, "https://upload.api.weibo.com/2/statuses/upload.json");
		setParameter("access_token", access_token);
		setParameter("status", status);
		setParameter("pic", pic);
	}

}
