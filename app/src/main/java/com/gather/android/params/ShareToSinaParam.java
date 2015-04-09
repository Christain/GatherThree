package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.io.File;

public class ShareToSinaParam extends BaseParams {

	public ShareToSinaParam(String access_token, String status, File pic) {
		super("https://upload.api.weibo.com/2/statuses/upload.json");
        put("access_token", access_token);
        put("status", status);
        try {
            put("pic", pic);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
