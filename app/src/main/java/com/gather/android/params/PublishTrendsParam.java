package com.gather.android.params;

import java.util.ArrayList;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 发布动态
 */
public class PublishTrendsParam extends StringParams {

	public PublishTrendsParam(Context context, String content, ArrayList<Integer> list) {
		super(context, "act/dynamic/add");
		setParameter("content", content);
		for (int i = 0; i < list.size(); i++) {
			setParameter("imgIds[" + i + "]", list.get(i));
		}
	}

}
