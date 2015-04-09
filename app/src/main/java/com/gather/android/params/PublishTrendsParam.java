package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.util.ArrayList;

/**
 * 发布动态
 */
public class PublishTrendsParam extends BaseParams {

	public PublishTrendsParam(String content, ArrayList<Integer> list) {
		super("act/dynamic/add");
        put("content", content);
		for (int i = 0; i < list.size(); i++) {
            put("imgIds[" + i + "]", list.get(i));
		}
	}

}
